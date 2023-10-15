package dev.tonimatas.botstudio;

import dev.tonimatas.botstudio.listeners.GuildMemberJoinListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class BotStudio {
    public static Logger logger = LogManager.getLogManager().getLogger("Server");

    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        File file = new File("key.txt");

        Scanner scanner;

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        JDABuilder builder = JDABuilder.createDefault(scanner.next());


        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setBulkDeleteSplittingEnabled(false);

        builder.addEventListeners(new GuildMemberJoinListener());
        builder.setActivity(Activity.playing("Bot The Game"));

        builder.build();
        logger.info("Done (" + ((double) (System.currentTimeMillis() - time)/1000) + "s)!");
    }
}