package men.brakh.googleDriveBot.vkbot

object HashtagsExtractor {
    private val regex = "(#)[^\\s]+".toRegex()

    fun extract (text: String): List<String> {
        return regex.findAll(text)
            .map { matchResult -> matchResult.value }
            .toList()
    }
}