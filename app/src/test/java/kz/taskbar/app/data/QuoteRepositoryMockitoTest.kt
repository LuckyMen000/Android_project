package kz.taskbar.app.data

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class QuoteRepositoryMockitoTest {
    private val api = mock<QuoteApi>()

    @Test fun stubApiReturnsOnlineQuoteAndVerifyCall() = runTest {
        whenever(api.randomQuote()).thenReturn(QuoteResponse("  Act now  ", " Mentor "))
        val repository = NetworkQuoteRepository(api, StandardTestDispatcher(testScheduler))
        val answer = repository.getQuote()
        assertEquals("Act now", answer.text)
        assertEquals("Mentor", answer.author)
        assertTrue(answer.fromNetwork)
        verify(api).randomQuote()
    }

    @Test fun mockApiFailureProducesOfflineFallback() = runTest {
        whenever(api.randomQuote()).thenThrow(IllegalStateException("Test failure"))
        val answer = NetworkQuoteRepository(api, StandardTestDispatcher(testScheduler)).getQuote()
        assertFalse(answer.fromNetwork)
        assertEquals("TaskBar", answer.author)
        verify(api).randomQuote()
    }

    @Test fun emptyRemoteQuoteIsRejected() = runTest {
        whenever(api.randomQuote()).thenReturn(QuoteResponse("  ", "Somebody"))
        assertFalse(NetworkQuoteRepository(api, StandardTestDispatcher(testScheduler)).getQuote().fromNetwork)
    }
}
