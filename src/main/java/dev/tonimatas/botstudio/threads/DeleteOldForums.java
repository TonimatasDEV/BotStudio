package dev.tonimatas.botstudio.threads;

import dev.tonimatas.botstudio.BotStudio;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.awt.*;
import java.time.Duration;
import java.time.OffsetDateTime;

public class DeleteOldForums {
    public static void startThread() {
        new Thread(() -> {
            while (true) {
                for (Guild guild : BotStudio.jda.getGuilds()) {
                    ForumChannel forumChannel = guild.getForumChannelById("1021143740942913638");

                    if (forumChannel == null) continue;
                    
                    for (ThreadChannel threadChannel : forumChannel.retrieveArchivedPublicThreadChannels()) {
                        OffsetDateTime then = threadChannel.getTimeCreated(); // Replace with your actual OffsetDateTime object
                        OffsetDateTime now = OffsetDateTime.now();

                        boolean isMoreThan30DaysAgo = now.minusDays(30).isAfter(then);
                        
                        if (isMoreThan30DaysAgo) {
                            NewsChannel issues = guild.getNewsChannelById("1228732115529765007");
                            
                            if (threadChannel.getName().contains("[Fixed]") || threadChannel.getName().contains("[Closed]") ) {
                                if (issues != null) {
                                    MessageEmbed embed = new EmbedBuilder()
                                            .setAuthor("BotStudio", null, BotStudio.jda.getSelfUser().getAvatarUrl())
                                            .setTitle(threadChannel.getName())
                                            .setDescription("Thread closed.")
                                            .setColor(Color.DARK_GRAY)
                                            .build();

                                    issues.sendMessageEmbeds(embed).queue();
                                }
                            }

                            threadChannel.delete().queue();
                        }
                    }
                }

                try {
                    Thread.sleep(Duration.ofMinutes(1));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
