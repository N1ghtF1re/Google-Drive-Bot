package men.brakh.googleDriveBot.vkbot.handler

import com.google.gson.Gson
import com.google.gson.JsonObject
import men.brakh.googleDriveBot.vkbot.model.LongPollingMessage

abstract class MessageHandler(val callback: (msg: LongPollingMessage) -> Any?) : Handler {
    override fun handle(obj: JsonObject) {
        val message = extractMessage(obj)
        callback(message)
    }


    private fun extractMessage (json: JsonObject): LongPollingMessage {
        return Gson().fromJson(json, LongPollingMessage::class.java)
    }
}

class NewMessageHandler(callback: (msg: LongPollingMessage) -> Any?) : MessageHandler(callback)