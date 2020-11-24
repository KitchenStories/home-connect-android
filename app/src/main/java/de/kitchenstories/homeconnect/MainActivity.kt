package de.kitchenstories.homeconnect

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeAppliance
import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeApplianceType
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectClientCredentials
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramOption
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramRequest
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectAuthorization
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.DefaultHomeConnectClient
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val homeConnectSecretsStore by lazy { MyTestHomeConnectSecretsStore(applicationContext) }

    //    private val baseUrl = "https://api.home-connect.com/"
    private val baseUrl = "https://simulator.home-connect.com/"

    private lateinit var homeConnectAuthenticateWebview: WebView
    private lateinit var ovenControls: ViewGroup
    private lateinit var temperatureInput: EditText
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
        if (homeConnectSecretsStore.accessToken != null) {
            showOvenControls()
        } else {
            homeConnectAuthorization = HomeConnectAuthorization(onRequestAccessTokenStarted = {})
            launch {
                try {
                    homeConnectAuthorization?.authorize(homeConnectAuthenticateWebview)
                    showOvenControls()
                } catch (e: Throwable) {
                    Log.e("SampleApp", "authorization failed", e)
                }
            }
        }
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
            val oven = homeConnectClient.getAllHomeAppliances(ofType = HomeApplianceType.Oven).firstOrNull()
            if (oven == null) {
                Toast.makeText(this@MainActivity, "No oven found", Toast.LENGTH_LONG).show()
            } else {
                val availablePrograms = homeConnectClient.getAvailablePrograms(forApplianceId = oven.id)
                availablePrograms.forEach { program ->
                    val programButton = Button(this@MainActivity)
                    ovenControls.addView(programButton)
                    programButton.text = when (program.key) {
                        "Cooking.Oven.Program.HeatingMode.PreHeating" -> ProgramNames.PREHEAT.title
                        "Cooking.Oven.Program.HeatingMode.HotAir" -> ProgramNames.HOT_AIR.title
                        "Cooking.Oven.Program.HeatingMode.TopBottomHeating" -> ProgramNames.TOP_BOTTOM_HEATING.title
                        "Cooking.Oven.Program.HeatingMode.PizzaSetting" -> ProgramNames.PIZZA_SETTING.title
                        else -> program.key

                    }
                    programButton.setOnClickListener {
                        startProgram(oven, program.key)
                    }
                }

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
                            key = "Cooking.Oven.Option.SetpointTemperature",
                            value = enteredTemperature,
                            unit = "°C",
                        )
                    ),
                ),
            )
        }
    }
}
