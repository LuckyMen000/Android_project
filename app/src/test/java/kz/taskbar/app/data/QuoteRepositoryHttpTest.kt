package kz.taskbar.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Real HTTP transport to a loopback fake server, never a production dependency. */
class QuoteRepositoryHttpTest {
    private lateinit var server: MockWebServer
    private lateinit var repo: NetworkQuoteRepository

    @Before fun setUp() {
        server = MockWebServer().also { it.start() }
        val client = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(150, TimeUnit.MILLISECONDS).build()
        val api = Retrofit.Builder().baseUrl(server.url("/"))
            .client(client).addConverterFactory(GsonConverterFactory.create())
            .build().create(QuoteApi::class.java)
        repo = NetworkQuoteRepository(api, Dispatchers.IO)
    }
    @After fun tearDown() { server.shutdown() }

    @Test fun status200ParsesJsonAndPath() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200)
            .setBody("""{"quote":"Do it","author":"QA"}"""))
        val result = repo.getQuote()
        assertTrue(result.fromNetwork)
        assertEquals("Do it", result.text)
        assertEquals("/quotes/random", server.takeRequest().path)
    }

    @Test fun status404UsesFallback() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(404))
        assertFalse(repo.getQuote().fromNetwork)
    }

    @Test fun status500UsesFallback() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))
        assertFalse(repo.getQuote().fromNetwork)
    }

    @Test fun timeoutUsesFallback() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200)
            .setBodyDelay(1000, TimeUnit.MILLISECONDS)
            .setBody("""{"quote":"Delayed","author":"QA"}"""))
        assertFalse(repo.getQuote().fromNetwork)
    }

    @Test fun malformedJsonUsesFallback() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("{not-valid-JSON"))
        assertFalse(repo.getQuote().fromNetwork)
    }
}
