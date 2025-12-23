package com.example.dms.network

import com.example.dms.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/me")
    suspend fun getProfile(): UserProfile

    @POST("api/logout")
    fun logout(): Call<Void>

    // --- Новости ---
    @GET("api/news")
    suspend fun getAllNews(): Response<List<News>>

    @GET("api/news/{id}")
    suspend fun getNewsById(@Path("id") id: Int): Response<News>

    @POST("api/news")
    suspend fun createNews(@Body news: NewsRequest): Response<Void>

    @PUT("api/news/{id}")
    suspend fun updateNews(@Path("id") id: Int, @Body news: NewsRequest): Response<Void>

    @DELETE("api/news/{id}")
    suspend fun deleteNews(@Path("id") id: Int): Response<Void>

    // --- Dormitory (Buildings, Floors, Rooms) ---
    @GET("buildings")
    fun getBuildings(): Call<ApiResponse<List<Building>>>

    @GET("api/buildings/{id}")
    fun getBuilding(@Path("id") id: Int): Call<Building>

    @GET("api/floors")
    fun getFloors(): Call<ApiResponse<List<Floor>>>

    @GET("api/floors/{id}")
    fun getFloor(@Path("id") id: Int): Call<Floor>

    @GET("api/rooms")
    fun getRooms(): Call<ApiResponse<List<Room>>>

    @GET("api/rooms/{id}")
    fun getRoom(@Path("id") id: Int): Call<Room>

    // --- Requests Live (Запросы на проживание) ---
    @POST("api/requests/live")
    fun createRequestLive(@Body request: RequestLiveCreate): Call<RequestLive>

    @GET("api/requests/live")
    fun getMyRequestsLive(): Call<List<RequestLive>>

    // --- Requests Repair (Запросы на ремонт) ---
    @POST("api/requests/repair")
    fun createRequestRepair(@Body request: RequestRepairCreate): Call<RequestRepair>

    @GET("api/requests/repair")
    fun getMyRequestsRepair(): Call<List<RequestRepair>>

    // --- Requests Sports (Запросы на физкультуру) ---
    @POST("api/requests/sports")
    fun createRequestSports(@Body request: RequestSportsCreate): Call<RequestSports>

    @GET("api/requests/sports")
    fun getMyRequestsSports(): Call<List<RequestSports>>
}
