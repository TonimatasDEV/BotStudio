package dev.tonimatas.botstudio;

import dev.tonimatas.botstudio.listeners.GuildMemberJoinListener;
import dev.tonimatas.botstudio.listeners.SlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class BotStudio {
    public static Logger logger = JDALogger.getLog(BotStudio.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        File file = new File("token.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
                logger.error("You need to add key file in the file \"token.txt\".");
                Runtime.getRuntime().halt(0);
            } catch (IOException e) {
                logger.error("Error on create key file.");
                Runtime.getRuntime().halt(0);
            }
        }

        Scanner scanner;

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            logger.error("Error on read the token file.");
            throw new RuntimeException();
        }

        if (!scanner.hasNext()) {
            logger.error("You need to add key file in the file \"token.txt\".");
            Runtime.getRuntime().halt(0);
        }

        JDABuilder builder = JDABuilder.createDefault(scanner.next());

        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setBulkDeleteSplittingEnabled(false);

        builder.addEventListeners(new GuildMemberJoinListener(), new SlashCommandListener());
        builder.setActivity(Activity.playing("Bot The Game"));

        JDA jda = builder.build();

        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(Commands.slash("ping", "See what ping you have to the bot."));
        commands.queue();
        
        logger.info("Done ({}s)!", (float) ((System.currentTimeMillis() - time) / 1000));
    }
}