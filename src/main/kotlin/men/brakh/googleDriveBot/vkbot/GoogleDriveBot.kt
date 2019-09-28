package men.brakh.googleDriveBot.vkbot

import com.vk.api.sdk.client.TransportClient
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.messages.MessageAttachment
import men.brakh.googleDriveBot.driveClient.GoogleDriveClient
import men.brakh.googleDriveBot.driveClient.GoogleDriveFile
import men.brakh.googleDriveBot.men.brakh.googldeDriveBot.bot.extentions.attachmentTitle
import men.brakh.googleDriveBot.men.brakh.googldeDriveBot.bot.extentions.attachmentUrl
import men.brakh.googleDriveBot.vkbot.handler.Handler
import men.brakh.googleDriveBot.vkbot.handler.NewMessageHandler
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.net.URL
import java.text.DateFormat
import java.util.*

class GoogleDriveBot(private val gdClient: GoogleDriveClient,
                     private val initialGoogleDriveFolderId: String,
                     groupId: Int,
                     token: String,
                     private val dateFormat: DateFormat) {

    private var vk: VkApiClient
    private var actor: GroupActor
    private val infoFileName = "INFORMATION"

    private val logger = LoggerFactory.getLogger(LongPollListener::class.java)

    init {
        val transportClient: TransportClient = HttpTransportClient.getInstance()
        vk = VkApiClient(transportClient)
        actor = GroupActor(groupId, token)
    }

    fun startLongPolling() {
        LongPollListener(vk = vk,
                        actor = actor)
            .addHandler("message_new", getNewMessageHandler())
            .start()
    }

    private fun getNewMessageHandler(): Handler {
        return NewMessageHandler{ message ->
            val hashTags: List<String> = HashtagsExtractor.extract(message.text)
            if (hashTags.size == 1) {
                logger.info("New request: $message")

                val hashTag = hashTags.first()

                val attachments: List<MessageAttachment> = message.attachments

                if (attachments.isEmpty()) {
                    appendInTextFile(hashTag = hashTag, text = message.text)
                } else {
                    uploadAttachments(hashTag = hashTag, attachments = attachments)
                }

                vk.messages().send(actor)
                    .message("Uploaded")
                    .peerId(message.peerId)
                    .randomId(Random().nextInt())
                    .execute()
            }
        }
    }

    private fun uploadAttachments(hashTag: String, attachments: List<MessageAttachment>) {
        val subject = getSubject(hashTag)

        val subjectFolder: GoogleDriveFile = getSubjectFolder(subject)


        attachments.forEach{ attachment ->
            val url = attachment.attachmentUrl?.let { s -> URL(s) }

            if (url != null) {

                val inputStream = BufferedInputStream(url.openStream())

                inputStream.use {
                    gdClient.uploadFile(
                        folderId = subjectFolder.id,
                        mimeType = url.openConnection().contentType,
                        inputStream = inputStream,
                        fileName = attachment.attachmentTitle ?: getFileName(hashTag)
                    )
                }
            }
        }
    }

    private fun appendInTextFile(hashTag: String, text: String) {
        val subject = getSubject(hashTag)

        val subjectFolder: GoogleDriveFile = getSubjectFolder(subject)


        gdClient.uploadFile(
            fileName = getFileName(hashTag),
            inputStream = ByteArrayInputStream(processText(text, hashTag).toByteArray()),
            mimeType = "text/plain",
            folderId = subjectFolder.id
        )

    }

    private fun getSubjectFolder(subject: String): GoogleDriveFile {
        val files: List<GoogleDriveFile> = gdClient.getFiles(initialGoogleDriveFolderId)

        return files.find { file -> file.name.toUpperCase() == subject }
            ?: gdClient.createFolder(initialGoogleDriveFolderId, subject)
    }

    private fun getSubject(hashTag: String): String {
        return hashTag
            .replace("#", "")
            .replace("_(.)*".toRegex(), "")
            .toUpperCase()
    }

    private fun processText(text: String, hashTag: String): String {
        return text.replace(hashTag, "")
    }

    private fun getFileName(hashTag: String):String {
        val arr = hashTag.split("_")
        return if (arr.size < 2) {
            infoFileName + "_" + currDate() + ".txt"
        } else {
            arr[1] + " " + currDate() + ".txt"
        }
    }

    private fun currDate(): String {
        return dateFormat.format(Date())
    }
}