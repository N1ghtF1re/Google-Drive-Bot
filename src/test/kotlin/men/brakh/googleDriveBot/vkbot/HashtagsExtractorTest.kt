package men.brakh.googleDriveBot.vkbot

import org.junit.Test
import kotlin.test.assertEquals

class HashtagsExtractorTest {

    @Test
    fun extract() {
        val text = """
            Hello, #World #IT
            #My name
            is #Alex
        """.trimIndent()

        val hashtags = HashtagsExtractor.extract(text)
        assertEquals(listOf("#World", "#IT", "#My", "#Alex"), hashtags)
    }

    @Test
    fun extractEmpty() {
        val text = """
            Hello, World
            My name
            is Alex
        """.trimIndent()

        val hashtags = HashtagsExtractor.extract(text)
        assertEquals(listOf(), hashtags)
    }

    @Test
    fun extractOne() {
        val text = """
            Hello, #World
            My name
            is Alex
        """.trimIndent()

        val hashtags = HashtagsExtractor.extract(text)
        assertEquals(listOf("#World"), hashtags)
    }
}