package com.ajnsnewmedia.kitchenstories.homeconnect.util

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

internal data class HomeConnectAuthorizationState(
        val webViewState: Bundle?,
        val authorizationCode: String?,
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readBundle(Bundle::class.java.classLoader), parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeBundle(webViewState)
        parcel.writeString(authorizationCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeConnectAuthorizationState> {
        override fun createFromParcel(parcel: Parcel): HomeConnectAuthorizationState {
            return HomeConnectAuthorizationState(parcel)
        }

        override fun newArray(size: Int): Array<HomeConnectAuthorizationState?> {
            return arrayOfNulls(size)
        }
    }

}