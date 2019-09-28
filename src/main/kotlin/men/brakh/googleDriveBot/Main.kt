package men.brakh.googleDriveBot

import men.brakh.googleDriveBot.driveClient.GoogleDriveClientImpl
import men.brakh.googleDriveBot.vkbot.GoogleDriveBot
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

object ResourceLoader {
    fun load(name: String): InputStream? {
        return ResourceLoader::javaClass::class.java.classLoader.getResourceAsStream(name)
    }

}

fun main() {
    val properties = Properties()

    properties.load(ResourceLoader.load("application.properties"))

    val bot = GoogleDriveBot(gdClient = GoogleDriveClientImpl(properties.getProperty("googledrive.application.name")),
        groupId = properties.getProperty("vk.bot.groupId").toInt(),
        token = properties.getProperty("vk.bot.token"),
        initialGoogleDriveFolderId = properties.getProperty("googledrive.folder.id"),
        dateFormat = SimpleDateFormat(properties.getProperty("vk.bot.date.format")))

    bot.startLongPolling()
}
