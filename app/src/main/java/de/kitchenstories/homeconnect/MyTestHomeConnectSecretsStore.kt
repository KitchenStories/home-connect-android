package de.kitchenstories.homeconnect

import android.content.Context
import androidx.core.content.edit
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectAccessToken
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectSecretsStore

class MyTestHomeConnectSecretsStore(context: Context) : HomeConnectSecretsStore {

    private val preferences = context.getSharedPreferences("home_connect_sample_app", Context.MODE_PRIVATE)

    override var accessToken: HomeConnectAccessToken?
        get() {
            val token = preferences.getString("access_token", null) ?: return null
            val refreshToken = preferences.getString("refresh_token", null) ?: return null
            val expiresAt = preferences.getLong("token_expires_at", 0L)
            return HomeConnectAccessToken(token, expiresAt, refreshToken)
        }
        set(value) {
            preferences.edit {
                if (value == null) {
                    remove("access_token")
                    remove("token_expires_at")
                    remove("refresh_token")
                } else {
                    putString("access_token", value.token)
                    putString("refresh_token", value.refreshToken)
                    putLong("token_expires_at", value.expiresAt)
                }
            }
        }

}