package de.kitchenstories.homeconnect

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.ArrayRes
import androidx.annotation.IntDef
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeAppliance
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeApplianceType
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectClientCredentials
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.*


import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.DefaultHomeConnectClient
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectAuthorization
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectClient
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectError
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {


    private val homeConnectSecretsStore by lazy { MyTestHomeConnectSecretsStore(applicationContext) }


    private val baseUrl = "https://api.home-connect.com/"
    // private val baseUrl = "https://simulator.home-connect.com/"

    private lateinit var homeConnectAuthenticateWebview: WebView
    private lateinit var ovenControls: ViewGroup
    private lateinit var temperatureInput: EditText
    private lateinit var temperatureConstraintsTextView: TextView
    private lateinit var constraintsListTextview: TextView
    private lateinit var programsList: ViewGroup
    private lateinit var localeSpinner: Spinner
    private val credentials = HomeConnectClientCredentials(
        clientId = BuildConfig.homeConnectClientId,
        clientSecret = BuildConfig.homeConnectClientSecret,
    )
    private var selectedLocale = ""

    private lateinit var homeConnectClient: HomeConnectClient

    private var homeConnectAuthorization: HomeConnectAuthorization? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        homeConnectClient = DefaultHomeConnectClient(baseUrl, credentials, homeConnectSecretsStore)
        homeConnectAuthenticateWebview = findViewById(R.id.home_connect_authenticate_webview)
        ovenControls = findViewById(R.id.oven_controls)
        temperatureInput = findViewById(R.id.temperature_input)
        temperatureConstraintsTextView = findViewById(R.id.temperature_constraints)
        constraintsListTextview = findViewById(R.id.constraints_list)
        programsList = findViewById(R.id.programs_list)
        localeSpinner = findViewById(R.id.locale_spinner)
        localeSpinner.setup(this, R.array.home_connect_locale_array)
        localeSpinner.onSelectionChange {
            selectedLocale = it
            programsList.removeAllViews()
            temperatureConstraintsTextView.text = "-"
            constraintsListTextview.text = "-"
            fetchAppliances()
        }

        if (homeConnectSecretsStore.accessToken != null) {
            showOvenControls()
        } else {
            val activity = this
            launch {
                try {
                    homeConnectAuthorization = HomeConnectAuthorization()
                    homeConnectAuthorization?.authorize(homeConnectAuthenticateWebview,
                        savedInstanceState,
                        onRequestAccessTokenStarted = {})
                    showOvenControls()
                } catch (e: Throwable) {
                    if (e is HomeConnectError.UserAbortedAuthorization) {
                        Toast.makeText(
                            activity,
                            "All is fine, the user aborted ${e.javaClass.canonicalName}:'${e.message}' ",
                            Toast.LENGTH_LONG
                        ).show()
                        activity.finish()
                        return@launch
                    }
                    val message = if (e is HomeConnectError && e.message != null) {
                        "The error description ist \"${e.message}\""
                    } else {
                        "something else failed. \"${e.localizedMessage}\""
                    }
                    MaterialAlertDialogBuilder(activity)
                        .setTitle("Something went wrong")
                        .setMessage(message)
                        .setPositiveButton("OK") { _, _ -> }
                        .create()
                        .show()
                    Log.e("SampleApp", "authorization failed", e)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        homeConnectAuthorization?.saveInstanceState(homeConnectAuthenticateWebview, outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        homeConnectAuthorization = null
    }

    private fun showOvenControls() {
        if (ovenControls.visibility == View.VISIBLE) return
        homeConnectAuthenticateWebview.isVisible = false
        ovenControls.isVisible = true
        fetchAppliances()
    }

    private fun fetchAppliances() {
        launch {
            val oven = homeConnectClient.getAllHomeAppliances(ofType = HomeApplianceType.Oven).firstOrNull()
            if (oven == null) {
                Toast.makeText(this@MainActivity, "No oven found", Toast.LENGTH_LONG).show()
            } else {
                val availablePrograms = homeConnectClient.getAvailablePrograms(forApplianceId = oven.id, inLocale = "")
                availablePrograms.forEach { program ->
                    val programButton = Button(this@MainActivity)
                    ovenControls.addView(programButton)
                    programButton.text = program.key
                    programButton.setOnClickListener {
                        startProgram(oven, program.key)
                    }
                }
            } catch (e:Exception){
                MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle("Could not fetch the appliances")
                        .setMessage("'$e.message'")
                        .setPositiveButton("OK") { _, _ -> }
                        .setNeutralButton("Retry") { _, _ -> fetchAppliances() }
                        .create()
                        .show()
            }
        }
    }

    private fun startProgram(oven: HomeAppliance, program: String) {
        val enteredTemperature = temperatureInput.text.toString().toIntOrNull()
        if (enteredTemperature == null) {
            Toast.makeText(this, "Please enter a temperature", Toast.LENGTH_LONG).show()
            return
        }

        launch {
            try {
                homeConnectClient.startProgram(
                        forApplianceId = oven.id,
                        program = StartProgramRequest(
                                key = program,
                                options = listOf(
                                        StartProgramOption(
                                                key = ProgramOptionKey.SetpointTemperature,
                                                value = enteredTemperature,
                                                unit = "°C",
                                        )
                                ),
                        ),
                )
            } catch (e: Exception){
                MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle("Could not start the program")
                        .setMessage("Error: '$e.message'")
                        .setPositiveButton("OK") { _, _ -> }
                        .setNeutralButton("Retry") { _, _ -> startProgram(oven, program) }
                        .create()
                        .show()
            }

        }
    }

    private fun showAvailablePrograms(oven: HomeAppliance) {
        launch {
            try{
                val availablePrograms = homeConnectClient.getAvailablePrograms(forApplianceId = oven.id, inLocale = selectedLocale)
                addToView(availablePrograms, oven)
            } catch (e:Exception){
                Toast.makeText(this@MainActivity, "Error: could not load the programs: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addToView(availablePrograms: List<AvailableProgram>, oven: HomeAppliance) {
        availablePrograms
                .sortedBy { it.name }
                .forEach { program ->
                    TextView(this@MainActivity).also {
                        it.text = program.name
                        programsList.addView(it)
                    }
                    val startButton = Button(this@MainActivity).also {
                        it.text = "Start Program"
                        it.setOnClickListener {
                            startProgram(oven, program.key)
                        }
                    }
                    val optionsButton = Button(this@MainActivity).also {
                        it.text = "See Options"
                        it.setOnClickListener {
                            showProgramOptions(ovenId = oven.id, programKey = program.key)
                        }
                    }
                    LinearLayout(this@MainActivity).also {
                        it.orientation = LinearLayout.HORIZONTAL
                        it.addView(startButton)
                        it.addView(optionsButton)
                        programsList.addView(it)
                    }
                }
    }

    private fun showProgramOptions(ovenId: String, programKey: String) {
        launch {
            try {
                val availableProgramOptions = homeConnectClient.getAvailableProgramOptions(
                        forApplianceId = ovenId,
                        selectedLocale,
                        forProgramKey = programKey
                )
                constraintsListTextview.text = "Options: ${availableProgramOptions.asSimpleListString()}"

                availableProgramOptions.find { it.key.endsWith("SetpointTemperature") }?.let {
                    temperatureConstraintsTextView.text = "min: ${it.constraints.min} max: ${it.constraints.max}"
                }
            } catch (e:Exception){
                Toast.makeText(this@MainActivity, "Error: could not load the program options: ${e.message}", Toast.LENGTH_LONG).show()
            }

        }
    }
}

private fun Spinner.onSelectionChange(function: (value: String) -> Unit) {
    onItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            function(parent.getItemAtPosition(position).toString())
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            function("")
        }
    }
}

private fun Spinner.setup(ccontext: Context, @ArrayRes arrayResource: Int) {
    ArrayAdapter.createFromResource(
            ccontext,
            arrayResource,
            android.R.layout.simple_spinner_item
    ).also { adapter ->
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        this.adapter = adapter
    }
}

private fun List<ProgramOptions>.asSimpleListString(): String = sortedBy { it.key }
        .map { it.name ?: it.key.split(".").last() }
        .joinToString(", ")
