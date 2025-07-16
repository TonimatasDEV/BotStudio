package dev.tonimatas.botstudio.listeners

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class ForumArchiveListener : ListenerAdapter() {
    // Real
    //val channelId = "1021143740942913638"
    //val issuesChannelId = "1228732115529765007"

    // Testing
    val channelId = "1395121536113053798"
    val issuesChannelId = "1395126142561812640"

    override fun onChannelUpdateArchived(event: ChannelUpdateArchivedEvent) {
        val channel = event.channel

        if (event.newValue == true && event.channelType == ChannelType.GUILD_PUBLIC_THREAD && channel.asThreadChannel().parentChannel.id == channelId) {
            val embed = EmbedBuilder()
                .setAuthor("BotStudio", null, event.jda.selfUser.avatarUrl)
                .setTitle(channel.name)
                .setDescription("Thread closed.")
                .setColor(Color.ORANGE)
                .build()

            val issuesChannel = event.guild.getNewsChannelById(issuesChannelId)
            issuesChannel?.sendMessageEmbeds(embed)?.queue()
            channel.delete().queue()
        }
    }
}