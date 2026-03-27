package com.meta.pixelandtexel.scanner.datasource.network

import com.meta.pixelandtexel.scanner.BuildConfig
import com.meta.pixelandtexel.scanner.datasource.network.SmartHomeApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface TokenProvider {
    fun bearerToken(): String
}

class StaticTokenProvider(
    private val token: String
) : TokenProvider {
    override fun bearerToken(): String = "Bearer $token"
}

const val BASE_URL_GET = BuildConfig.HTTP_API

private fun authInterceptor(tokenProvider: TokenProvider) = Interceptor { chain ->
    val request = chain.request()
        .newBuilder()
        .addHeader("Authorization", tokenProvider.bearerToken())
        .addHeader("Content-Type", "application/json")
        .build()
    chain.proceed(request)
}

fun provideHttpClient(tokenProvider: TokenProvider): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(authInterceptor(tokenProvider))
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()
}


fun provideRetrofit(
    okHttpClient: OkHttpClient,
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL_GET)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideService(retrofit: Retrofit): SmartHomeApi =
    retrofit.create(SmartHomeApi::class.java)

val networkModule = module {
    single<TokenProvider> { StaticTokenProvider(token = BuildConfig.HOME_ASSISTANT_TOKEN) }
    single { provideHttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideService(get()) }
}