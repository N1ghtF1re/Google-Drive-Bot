package men.brakh.googleDriveBot.vkbot

import com.vk.api.sdk.callback.longpoll.responses.GetLongPollEventsResponse
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.Actor
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.exceptions.LongPollServerKeyExpiredException
import com.vk.api.sdk.objects.groups.responses.GetLongPollServerResponse
import men.brakh.googleDriveBot.vkbot.handler.Handler
import org.slf4j.LoggerFactory


class LongPollListener(private val vk: VkApiClient,
                       private val actor: Actor) : Thread() {

    private val logger = LoggerFactory.getLogger(LongPollListener::class.java)

    private val handlers: MutableMap<String, Handler> = mutableMapOf()

    private var server: String = ""
    private var key: String = ""
    private var ts: Int = 0

    protected fun connect() {
        val getLongPollResponse: GetLongPollServerResponse = vk.groups()
            .getLongPollServer(actor as GroupActor)
            .execute()

        server = getLongPollResponse.server
        key = getLongPollResponse.key
        ts = getLongPollResponse.ts
    }

    override fun run() {
        connect()
        while (true) {
            try {
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
            } catch (e: LongPollServerKeyExpiredException) {
                logger.info("Key expired. Regeneration...")
                connect()
            }

        }
    }

    fun addHandler(event: String, handler: Handler): LongPollListener {
        handlers[event] = handler
        return this
    }
}