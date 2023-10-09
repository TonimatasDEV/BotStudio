package dev.tonimatas;

import dev.tonimatas.listeners.Test;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
    public static void main(String[] args) {
        JDABuilder builder = JDABuilder.createDefault("MTA3NDM0MTcwNjc4ODUwMzU2Mw.GdHm28.SOLSGzM0slM1RiswKHoTrPLatpe84TJ7a1dNtc");

        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY);
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setActivity(Activity.playing("Starting"));

        builder.addEventListeners(new Test());

        builder.build();
    }
}