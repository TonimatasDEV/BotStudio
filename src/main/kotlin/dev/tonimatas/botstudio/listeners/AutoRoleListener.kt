package dev.tonimatas.botstudio.listeners

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.internal.utils.JDALogger
import org.slf4j.Logger

class AutoRoleListener : ListenerAdapter() {
    var logger: Logger = JDALogger.getLog(AutoRoleListener::class.java)
    
    // Real
    //var channelId = "835907242544726057"
    //var roleId = "835907987641729064"

    // Testing
    val channelId = "1386794908421193919"
    val roleId = "1386794676610273420"

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val guild = event.guild
        val channel = guild.getTextChannelById(channelId)
        val role = guild.getRoleById(roleId)
        val member = event.member

        channel?.sendMessage(member.asMention + " Welcome to TonimatasDEV Studios! We already are: " + guild.memberCount + "!")
            ?.queue()

        if (role != null) {
            guild.addRoleToMember(member, role).queue()
        }

        logger.info("{} joined. Count: {}", member.effectiveName, guild.memberCount)
    }
}