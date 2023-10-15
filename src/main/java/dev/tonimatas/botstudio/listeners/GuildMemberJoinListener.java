package dev.tonimatas.botstudio.listeners;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberJoinListener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        TextChannel textChannel = event.getGuild().getTextChannelById("835907242544726057");
        if (textChannel != null) {
            textChannel.sendMessage(event.getMember().getAsMention() + " Welcome to TonimatasDEV Studios! We already are: " + event.getGuild().getMemberCount() + "!").queue();
        }

        Role role = event.getGuild().getRoleById("835907987641729064");
        if (role == null) return;
        event.getGuild().addRoleToMember(event.getMember(), role).queue();

        System.out.println(event.getMember().getEffectiveName() + " joined. Count:" + event.getGuild().getMemberCount());
    }
}
