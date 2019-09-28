package men.brakh.googleDriveBot.men.brakh.googldeDriveBot.bot.extentions

import com.vk.api.sdk.objects.messages.MessageAttachment
import com.vk.api.sdk.objects.messages.MessageAttachmentType
import org.apache.commons.io.FilenameUtils
import java.net.URL

private fun String.extractName(): String {
    return FilenameUtils.getName(URL(this).path)
}

val MessageAttachment.attachmentTitle: String?
    get() {
        return when (this.type!!) {
            MessageAttachmentType.PHOTO -> this.photo.photo1280.extractName()
            MessageAttachmentType.AUDIO -> this.audio.title
            MessageAttachmentType.DOC -> this.doc.title
            MessageAttachmentType.LINK -> this.link.title + ".txt"
            else -> null
        }
    }

val MessageAttachment.attachmentUrl: String?
    get() {
        return when (this.type!!) {
            MessageAttachmentType.PHOTO -> this.photo.photo1280
            MessageAttachmentType.AUDIO -> this.audio.url
            MessageAttachmentType.DOC -> this.doc.url
            MessageAttachmentType.LINK -> this.link.url
            else -> null
        }
    }