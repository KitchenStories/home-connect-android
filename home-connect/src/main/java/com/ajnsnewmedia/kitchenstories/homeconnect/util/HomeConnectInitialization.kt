package com.ajnsnewmedia.kitchenstories.homeconnect.util

import com.ajnsnewmedia.kitchenstories.homeconnect.HomeConnectApi
import com.ajnsnewmedia.kitchenstories.homeconnect.HomeConnectInterceptor
import com.ajnsnewmedia.kitchenstories.homeconnect.sdk.HomeConnectSecretsStore
import com.ajnsnewmedia.kitchenstories.homeconnect.model.jsonadapters.HomeConnectMoshiBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal fun getHomeConnectApi(baseUrl: String, homeConnectSecretsStore: HomeConnectSecretsStore): HomeConnectApi {
    val moshiConverterFactory = MoshiConverterFactory.create(HomeConnectMoshiBuilder.moshiInstance)

    val okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor())
        addInterceptor(HomeConnectInterceptor(
                homeConnectSecretsStore = homeConnectSecretsStore,
                converterFactory = moshiConverterFactory,
                timeProvider = DefaultTimeProvider(),
        ))
    }.build()

    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .client(okHttpClient)
            .build()
            .create(HomeConnectApi::class.java)
}