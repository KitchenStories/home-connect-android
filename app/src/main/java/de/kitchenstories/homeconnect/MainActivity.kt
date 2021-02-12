package de.kitchenstories.homeconnect

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeAppliance
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeApplianceType
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectClientCredentials
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ProgramOptionKey
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramOption
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramRequest
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.DefaultHomeConnectClient
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectAuthorization
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectClient
import com.ajnsnewmedia.kitchenstories.homeconnect.util.HomeConnectError
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val homeConnectSecretsStore by lazy { MyTestHomeConnectSecretsStore(applicationContext) }

    // private val baseUrl = "https://api.home-connect.com/"
    private val baseUrl = "https://simulator.home-connect.com/"

    private lateinit var homeConnectAuthenticateWebview: WebView
    private lateinit var ovenControls: ViewGroup
    private lateinit var temperatureInput: EditText
    private lateinit var programsList: ViewGroup
    private val credentials = HomeConnectClientCredentials(
        clientId = BuildConfig.homeConnectClientId,
        clientSecret = BuildConfig.homeConnectClientSecret,
    )

    private lateinit var homeConnectClient: HomeConnectClient

    private var homeConnectAuthorization: HomeConnectAuthorization? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        homeConnectClient = DefaultHomeConnectClient(baseUrl, credentials, homeConnectSecretsStore)
        homeConnectAuthenticateWebview = findViewById(R.id.home_connect_authenticate_webview)
        ovenControls = findViewById(R.id.oven_controls)
        temperatureInput = findViewById(R.id.temperature_input)
        programsList = findViewById(R.id.programs_list)
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
        launch {
            val oven = homeConnectClient.getAllHomeAppliances(ofType = HomeApplianceType.Oven)
                .firstOrNull()
            if (oven == null) {
                Toast.makeText(this@MainActivity, "No oven found", Toast.LENGTH_LONG).show()
            } else {
                showAvailablePrograms(oven = oven)

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
        }
    }

    private fun showAvailablePrograms(oven: HomeAppliance) {
        launch {
            val availablePrograms =
                homeConnectClient.getAvailablePrograms(forApplianceId = oven.id, inLocale = "")
            availablePrograms.forEach { program ->
                val programTextView = TextView(this@MainActivity)
                val startButton = Button(this@MainActivity)
                val optionsButton = Button(this@MainActivity)
                programsList.addView(programTextView)
                programsList.addView(startButton)
                programsList.addView(optionsButton)
                programTextView.text = program.name
                startButton.text = "Start Program"
                optionsButton.text = "See Options"
                startButton.setOnClickListener {
                    startProgram(oven, program.key)
                }
                optionsButton.setOnClickListener {
                    showProgramOptions(ovenId = oven.id, programKey = program.key)
                }

            }

        }
    }

    private fun showProgramOptions(ovenId: String, programKey: String) {
        launch {
            val availableProgramOptions = homeConnectClient.getAvailableProgramOptions(
                forApplianceId = ovenId,
                "en",
                forProgramKey = programKey
            )
            availableProgramOptions.forEach { option ->
                val programOptionsButton = TextView(this@MainActivity)
                programsList.addView(programOptionsButton)
                programOptionsButton.text = option.key

            }
        }

    }
}
