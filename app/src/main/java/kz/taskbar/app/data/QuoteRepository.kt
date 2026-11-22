package kz.taskbar.app.data

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

/** Optional network feature. Core task management works fully offline. */
data class QuoteResponse(
    @SerializedName("quote") val quote: String,
    @SerializedName("author") val author: String,
)

interface QuoteApi {
    @GET("quotes/random")
    suspend fun randomQuote(): QuoteResponse
}

data class DailyQuote(val text: String, val author: String, val fromNetwork: Boolean)

interface QuoteRepository {
    suspend fun getQuote(): DailyQuote
}

class NetworkQuoteRepository : QuoteRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (kz.taskbar.app.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        })
        .build()
    private val api: QuoteApi = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuoteApi::class.java)

    override suspend fun getQuote(): DailyQuote = withContext(Dispatchers.IO) {
        try {
            val result = api.randomQuote()
            if (result.quote.isBlank()) error("Empty quote")
            DailyQuote(result.quote.trim(), result.author.trim(), true)
        } catch (_: Exception) {
            DailyQuote("Большие результаты начинаются с маленьких действий.", "TaskBar", false)
        }
    }
}
