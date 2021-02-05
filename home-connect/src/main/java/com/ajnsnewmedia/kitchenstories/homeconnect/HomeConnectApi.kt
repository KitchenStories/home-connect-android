package com.ajnsnewmedia.kitchenstories.homeconnect

import com.ajnsnewmedia.kitchenstories.homeconnect.model.appliances.HomeAppliancesData
import com.ajnsnewmedia.kitchenstories.homeconnect.model.auth.AccessTokenResponse
import com.ajnsnewmedia.kitchenstories.homeconnect.model.base.HomeConnectApiError
import com.ajnsnewmedia.kitchenstories.homeconnect.model.base.HomeConnectApiRequest
import com.ajnsnewmedia.kitchenstories.homeconnect.model.base.HomeConnectApiResponse
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.ActiveProgram
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.AvailableProgramsData
import com.ajnsnewmedia.kitchenstories.homeconnect.model.programs.StartProgramRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HomeConnectApi {

    @GET("/api/homeappliances")
    suspend fun getAllHomeAppliances(): HomeConnectApiResponse<HomeAppliancesData>

    @GET("/api/homeappliances/{id}/programs/active")
    suspend fun getActiveProgram(@Path("id") forApplianceId: String): HomeConnectApiResponse<ActiveProgram>

    @GET("/api/homeappliances/{id}/programs/available")
    suspend fun getAvailablePrograms(
            @Path("id") forApplianceId: String,
            @Header("Accept-Language") inLocale: String = "",
    ): HomeConnectApiResponse<AvailableProgramsData>

    //TODO: see if the `grant_type` could be an annotation
    @FormUrlEncoded
    @POST(ACCESS_TOKEN_ENDPOINT)
    suspend fun postAuthorizationCode(
            @Field("code") authorizationCode: String,
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("grant_type") grantType: String = "authorization_code",
    ): AccessTokenResponse

    @PUT("/api/homeappliances/{id}/programs/active")
    suspend fun startProgram(
            @Path("id") forApplianceId: String,
            @Body program: HomeConnectApiRequest<StartProgramRequest>,
    ): Response<HomeConnectApiError?>
}
