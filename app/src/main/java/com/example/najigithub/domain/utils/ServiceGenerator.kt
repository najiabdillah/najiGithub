package com.example.najigithub.domain.utils

import com.example.najigithub.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {
    fun <T> createService(
        serviceClass: Class<T>
    ): T {
        val client: OkHttpClient.Builder = OkHttpClient.Builder()
        client.connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)


        client.addInterceptor(Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("x-localization", "id")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Cache-Control", "no-store")
                .addHeader("accept", "application/json")
                .addHeader(Extensions.AUTH_HEADER, BuildConfig.TOKEN)
                .build()
            chain.proceed(request)
        })

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.URL)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(serviceClass)
    }
}