package ru.danilin52.historyreference.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class WikiImage(val source: String? = null)

data class WikiSummary(
    val title: String? = null,
    val extract: String? = null,
    val thumbnail: WikiImage? = null
)

interface WikiApi {
    @GET("page/summary/{title}")
    suspend fun summary(@Path("title") title: String): WikiSummary
}

object WikiClient {
    private const val BASE_URL = "https://ru.wikipedia.org/api/rest_v1/"

    val api: WikiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikiApi::class.java)
    }
}