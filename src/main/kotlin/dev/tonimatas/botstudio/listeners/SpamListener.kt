package dev.tonimatas.botstudio.listeners

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class SpamListener : ListenerAdapter() {
    // Real
    //val channelId = "1523018613978693643"

    // Testing
    val channelId = "1523021000198062121"

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val channel = event.channel
        val member = event.member

        if (channel.id == channelId) {
            if (member != null && !member.isOwner && !member.user.isBot) {
                member.ban(1, TimeUnit.HOURS).complete()
            }
        }
    }
}