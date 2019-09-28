package men.brakh.googleDriveBot

import men.brakh.googleDriveBot.driveClient.GoogleDriveClientImpl
import men.brakh.googleDriveBot.vkbot.GoogleDriveBot
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


private fun String.asResource(work: (InputStream) -> Unit) {
    val content = this.javaClass::class.java.getResourceAsStream(this)
    work(content)
}


fun main() {
    val properties = Properties()

    "application.properties".asResource {
        properties.load(it)
    }


    val bot = GoogleDriveBot(gdClient = GoogleDriveClientImpl(properties.getProperty("googledrive.application.name")),
        groupId = properties.getProperty("vk.bot.groupId").toInt(),
        token = properties.getProperty("vk.bot.token"),
        initialGoogleDriveFolderId = properties.getProperty("googledrive.folder.id"),
        dateFormat = SimpleDateFormat(properties.getProperty("vk.bot.date.format")))

    bot.startLongPolling()
}
