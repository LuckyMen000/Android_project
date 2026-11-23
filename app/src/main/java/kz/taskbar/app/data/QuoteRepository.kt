package kz.taskbar.app.data

import com.google.gson.annotations.SerializedName
import kz.taskbar.app.diagnostics.TaskBarLog
import kotlinx.coroutines.CoroutineDispatcher
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

/** Public construction seam for mocked API tests and MockWebServer integration tests. */
fun defaultQuoteApi(): QuoteApi {
    val client = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (kz.taskbar.app.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }).build()
    return Retrofit.Builder().baseUrl("https://dummyjson.com/")
        .client(client).addConverterFactory(GsonConverterFactory.create())
        .build().create(QuoteApi::class.java)
}

class NetworkQuoteRepository(
    private val api: QuoteApi = defaultQuoteApi(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : QuoteRepository {
    override suspend fun getQuote(): DailyQuote = withContext(dispatcher) {
        try {
            val result = api.randomQuote()
            if (result.quote.isBlank()) error("Empty quote")
            TaskBarLog.i(TaskBarLog.NETWORK, "Quote HTTP request succeeded")
            DailyQuote(result.quote.trim(), result.author.trim(), true)
        } catch (error: Exception) {
            // Coroutine cancellation must propagate instead of being converted into a fallback.
            if (error is kotlinx.coroutines.CancellationException) throw error
            TaskBarLog.w(TaskBarLog.NETWORK, "Offline quote fallback: ${error.javaClass.simpleName}")
            DailyQuote("Большие результаты начинаются с маленьких действий.", "TaskBar", false)
        }
    }
}
