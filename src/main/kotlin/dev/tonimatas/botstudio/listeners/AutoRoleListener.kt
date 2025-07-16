package dev.tonimatas.botstudio.listeners

import dev.tonimatas.botstudio.Main
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class AutoRoleListener : ListenerAdapter() {
    // Real
    //var channelId = "835907242544726057"
    //var roleId = "835907987641729064"
    
    // Testing
    var channelId = "1386794908421193919"
    var roleId = "1386794676610273420"
    
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val guild = event.guild
        val channel = guild.getTextChannelById(channelId)
        val role = guild.getRoleById(roleId)
        val member = event.member

        channel?.sendMessage(member.asMention + " Welcome to TonimatasDEV Studios! We already are: " + guild.memberCount + "!")?.queue()

        if (role != null) {
            guild.addRoleToMember(member, role).queue()
        }

        Main.Companion.logger.info("{} joined. Count: {}", member.effectiveName, guild.memberCount)
    }
}