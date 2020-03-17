package com.appleto.loppyuser.retrofit2

import com.appleto.loppyuser.apiModels.*
import com.appleto.loppyuser.helper.Const
import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface ApiService {

    companion object {
        fun create(token: String): ApiService {

            val httpClient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            httpClient.addInterceptor(interceptor)

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")

                // Adding Authorization token (API Key)
                // Requests will be denied without API key
                /*if (!TextUtils.isEmpty(token)) {
                    requestBuilder.addHeader("token", token)
                }*/

                val request = requestBuilder.build()
                chain.proceed(request)
            }

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .client(httpClient.build())
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl(Const.BASE_URL)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("phone") phone: String, @Field("fcm_token") fcmToken: String):
            Observable<JsonObject>

    @Multipart
    @POST("register")
    fun register(@PartMap map: HashMap<String, RequestBody>, @Part licenceImage: MultipartBody.Part): Observable<JsonObject>

    @FormUrlEncoded
    @POST("register")
    fun registerDirect(@FieldMap map: HashMap<String, String>): Observable<JsonObject>

    @FormUrlEncoded
    @POST("otp_verification")
    fun verifyOTP(@Field("user_id") userId: String, @Field("otp") otp: String):
            Observable<JsonObject>

    @FormUrlEncoded
    @POST("forgot_password")
    fun forgotPassword(
        @Field("phone") phone: String, @Field("new_password") newPassword: String, @Field(
            "conf_password"
        ) confPassword: String
    ): Observable<JsonObject>

    @GET("truck_details")
    fun truckDetails(): Observable<TruckListModel>

    @FormUrlEncoded
    @POST("add_ride_request")
    fun addRideRequest(@FieldMap fieldMap: HashMap<String, String>): Observable<JsonObject>

    @GET("load_types")
    fun getLoadTypes(): Observable<LoadTypesModel>

    @GET("good_types")
    fun getGoodType(): Observable<GoodTypesModel>

    @GET("get_offers")
    fun offersDetails(): Observable<OffersListModel>

    @FormUrlEncoded
    @POST("user_pending_rides")
    fun getPendingRides(@Field("user_id") driver_id: String): Observable<PendingRideModel>

    @FormUrlEncoded
    @POST("user_complete_rides")
    fun getCompleteRides(@Field("user_id") driver_id: String): Observable<PendingRideModel>

    @FormUrlEncoded
    @POST("get_driver_location")
    fun getDriverLocation(@Field("driver_id") driverId: String): Observable<JsonObject>

    @FormUrlEncoded
    @POST("get_user_notification")
    fun getNotifications(@Field("user_id") userId: String): Observable<NotificationListModel>
}