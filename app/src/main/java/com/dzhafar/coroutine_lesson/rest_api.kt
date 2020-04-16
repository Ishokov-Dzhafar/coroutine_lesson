package com.dzhafar.coroutine_lesson

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable
import java.time.Duration
import java.util.concurrent.TimeUnit

interface RestApi {
    @GET("/facts/random")
    suspend fun fetchRandomFacts(@Query("animal_type") animalType: String, @Query("amount") amount: Int): Response<List<FactRes>>
}


data class FactRes(

    @SerializedName("used") val used: Boolean,
    @SerializedName("source") val source: String,
    @SerializedName("type") val type: String,
    @SerializedName("deleted") val deleted: Boolean,
    @SerializedName("_id") val _id: String,
    @SerializedName("__v") val __v: Int,
    @SerializedName("text") val text: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("status") val status: Status,
    @SerializedName("user") val user: String
) : Serializable


data class Status(

    @SerializedName("verified") val verified: Boolean,
    @SerializedName("sentCount") val sentCount: Int
) : Serializable

class FactRepository {
    var client = RetrofitService.createService(RestApi::class.java)

    suspend fun fetchRandomFacts(animalType: String, amount: Int) =
        client.fetchRandomFacts(animalType, amount)
}

object RetrofitService {

    var logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).connectTimeout(
        60, TimeUnit.SECONDS
    ).readTimeout(60, TimeUnit.SECONDS).build()

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://cat-fact.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }
}