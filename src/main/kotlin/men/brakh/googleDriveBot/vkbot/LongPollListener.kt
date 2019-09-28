package men.brakh.googleDriveBot.vkbot

import com.vk.api.sdk.callback.longpoll.responses.GetLongPollEventsResponse
import com.vk.api.sdk.client.VkApiClient
import men.brakh.googleDriveBot.vkbot.handler.Handler
import org.slf4j.LoggerFactory


class LongPollListener(val vk: VkApiClient,
                       val server: String,
                       val key: String,
                       var ts: Int) : Thread() {

    private val logger = LoggerFactory.getLogger(LongPollListener::class.java)

    private val handlers: MutableMap<String, Handler> = mutableMapOf()

    override fun run() {
        while (true) {
            val response: GetLongPollEventsResponse = vk.longPoll()
                .getEvents(server, key, ts)
                .waitTime(30)
                .execute()

            ts = response.ts
            val updates = response.updates


            updates.forEach {
                val type = it.get("type").asString

                logger.info(it.toString())

                handlers[type]?.handle(it.get("object").asJsonObject)
            }

        }
    }

    fun addHandler(event: String, handler: Handler): LongPollListener {
        handlers[event] = handler
        return this
    }
}