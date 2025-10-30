package dev.tonimatas.botstudio

import dev.tonimatas.botstudio.commands.PingCommand
import dev.tonimatas.botstudio.data.BotData
import dev.tonimatas.botstudio.listeners.AutoRoleListener
import dev.tonimatas.botstudio.listeners.ForumArchiveListener
import dev.tonimatas.botstudio.listeners.TicketListener
import dev.tonimatas.cjda.CJDABuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.internal.utils.JDALogger
import org.slf4j.Logger
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class Main {
    init {
        val time = System.currentTimeMillis()
        BotData.runProperties()
        val file = File("token.txt")

        if (!file.exists()) {
            try {
                file.createNewFile()
                logger.error("You need to add key file in the file \"token.txt\".")
                Runtime.getRuntime().halt(0)
            } catch (e: IOException) {
                logger.error("Error on create key file: {}", e.message)
                Runtime.getRuntime().halt(0)
            }
        }

        val scanner: Scanner

        try {
            scanner = Scanner(file)
        } catch (e: FileNotFoundException) {
            logger.error("Error on read the token file: {}", e.message)
            throw RuntimeException()
        }

        if (!scanner.hasNext()) {
            logger.error("You need to add key file in the file \"token.txt\".")
            Runtime.getRuntime().halt(0)
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

        val cjda = CJDABuilder.create(jda)

        cjda.registerCommands(
            PingCommand()
        ).init().queue()

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
        var logger: Logger = JDALogger.getLog(Main::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            Main()
        }
    }
}