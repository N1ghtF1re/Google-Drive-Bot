package men.brakh.googleDriveBot.vkbot.handler

import com.google.gson.JsonObject

interface Handler {
    fun handle(obj: JsonObject)
}