package com.ajnsnewmedia.kitchenstories.homeconnect.util

import com.ajnsnewmedia.kitchenstories.homeconnect.HomeConnectApi
import com.ajnsnewmedia.kitchenstories.homeconnect.HomeConnectInterceptor
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.HomeConnectClientCredentials
import com.ajnsnewmedia.kitchenstories.homeconnect.model.jsonadapters.HomeConnectMoshiBuilder
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectSecretsStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal interface HomeConnectApiFactory {
    fun getHomeConnectApi(): HomeConnectApi
}

internal class DefaultHomeConnectApiFactory(
        private val baseUrl: String,
        private val secretsStore: HomeConnectSecretsStore,
        private val clientCredentials: HomeConnectClientCredentials,
) : HomeConnectApiFactory {

    private var api: HomeConnectApi? = null

    override fun getHomeConnectApi(): HomeConnectApi {
        if (api == null) {
            val moshiConverterFactory = MoshiConverterFactory.create(HomeConnectMoshiBuilder.moshiInstance)

            val okHttpClient = OkHttpClient.Builder().apply {
                addInterceptor(HttpLoggingInterceptor())
                addInterceptor(HomeConnectInterceptor(
                        homeConnectSecretsStore = secretsStore,
                        converterFactory = moshiConverterFactory,
                        timeProvider = DefaultTimeProvider(),
                        clientCredentials = clientCredentials
                ))
            }.build()
            api = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(moshiConverterFactory)
                    .client(okHttpClient)
                    .build()
                    .create(HomeConnectApi::class.java)
        }
        return api!!
    }

}