package dev.tonimatas.botstudio

import dev.tonimatas.botstudio.commands.PingCommand
import dev.tonimatas.botstudio.data.BotData
import dev.tonimatas.botstudio.listeners.AutoRoleListener
import dev.tonimatas.botstudio.listeners.ForumArchiveListener
import dev.tonimatas.botstudio.listeners.TicketListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.internal.utils.JDALogger
import org.slf4j.Logger
import revxrsal.commands.jda.JDALamp
import revxrsal.commands.jda.JDAVisitors
import revxrsal.commands.jda.actor.SlashCommandActor
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class Main {
    var logger: Logger = JDALogger.getLog(Main::class.java)

    init {
        val time = System.currentTimeMillis()
        BotData.runProperties()
        val file = File("token.txt")

        if (!file.exists()) {
            try {
                file.createNewFile()
                logger.error("You need to add key file in the file \"token.txt\".")
            } catch (e: IOException) {
                logger.error("Error on create key file: {}", e.message)
            }

            throw RuntimeException("Invalid token")
        }

        val scanner: Scanner

        try {
            scanner = Scanner(file)
        } catch (e: FileNotFoundException) {
            logger.error("Error reading the token file: {}", e.message)
            throw RuntimeException("Invalid token")
        }

        if (!scanner.hasNext()) {
            logger.error("You need to add key file in the file \"token.txt\".")
            throw RuntimeException("Empty token")
        }

        val jda = JDABuilder.createDefault(scanner.next())
            .enableIntents(GatewayIntent.entries)
            .enableCache(CacheFlag.entries)
            .setBulkDeleteSplittingEnabled(false)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .addEventListeners(AutoRoleListener(), ForumArchiveListener(), TicketListener())
            .setActivity(Activity.playing("Bot The Game"))
            .setAutoReconnect(true)
            .build()

        val lamp = JDALamp.builder<SlashCommandActor?>().build()

        lamp.register(PingCommand())
        lamp.accept(JDAVisitors.slashCommands(jda))

        jda.awaitReady()

        addStopHook(jda)

        logger.info("Done! ({}s)", ((System.currentTimeMillis() - time) / 1000).toFloat())
    }

    fun addStopHook(jda: JDA) {
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Stopping...")

            jda.shutdown()
            jda.awaitShutdown()

            logger.info("Stopped!")
        })
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Main()
        }
    }
}