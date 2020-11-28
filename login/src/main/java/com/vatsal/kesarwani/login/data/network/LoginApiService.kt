package com.vatsal.kesarwani.login.data.network

import com.vatsal.kesarwani.login.data.request.LoginRequest
import com.vatsal.kesarwani.login.data.response.LoginResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {

    @POST("api/auth/login")
    fun login(
            @Body body : LoginRequest
    ) : Single<LoginResponse>

}