package dev.tonimatas.botstudio.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping" -> {
                long time = System.currentTimeMillis();
                event.reply("Pong!").setEphemeral(true).flatMap(v -> event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)).queue();
            }
        }
    }
}
