package dev.tonimatas.botstudio;

import dev.tonimatas.botstudio.listeners.GuildMemberJoinListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class BotStudio {
    public static Logger logger = Logger.getLogger("Server");

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        new File("logs").mkdir();

        try {
            FileHandler fileHandler = new FileHandler("logs/latest.txt");
            logger.addHandler(fileHandler);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
        } catch (SecurityException | IOException e) {
            logger.info("Exception:" + e.getMessage());
        }

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