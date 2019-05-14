package com.parcels.util.network

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

  private var retrofit: Retrofit? = null

  fun getClient(): Retrofit {

    val gson = GsonBuilder()
      .setLenient()
      .create()

    if (retrofit == null) {
      retrofit = Retrofit.Builder()
        .baseUrl(ApiEndPoint.ENDPOINT_SERVER_BASE)
        .client(okClient())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    }
    return retrofit as Retrofit
  }

  private fun okClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
      .connectTimeout(5, TimeUnit.MINUTES)
      .writeTimeout(5, TimeUnit.MINUTES)
      .readTimeout(5, TimeUnit.MINUTES)
      .addInterceptor(logging)
      .build()
  }
}