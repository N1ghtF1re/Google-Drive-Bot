package men.brakh.googleDriveBot.vkbot.model

import com.google.gson.annotations.SerializedName
import com.vk.api.sdk.objects.messages.MessageAttachment

data class LongPollingMessage(
    @SerializedName("date") val date : Int,
    @SerializedName("from_id") val fromId : Int,
    @SerializedName("id") val id : Int,
    @SerializedName("out") val out : Int,
    @SerializedName("peer_id") val peerId : Int,
    @SerializedName("text") val text : String,
    @SerializedName("conversation_message_id") val conversationMessageId : Int,
    @SerializedName("fwd_messages") val fwdMessages : List<LongPollingMessage>,
    @SerializedName("important") val important : Boolean,
    @SerializedName("random_id") val random_id : Int,
    @SerializedName("attachments") val attachments : List<MessageAttachment>,
    @SerializedName("is_hidden") val isHidden : Boolean
)