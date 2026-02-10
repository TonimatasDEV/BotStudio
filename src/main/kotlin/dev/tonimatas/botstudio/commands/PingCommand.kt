package dev.tonimatas.botstudio.commands

import net.dv8tion.jda.api.interactions.InteractionHook
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.jda.actor.SlashCommandActor
import revxrsal.commands.jda.annotation.CommandPermission
import java.util.function.Function

class PingCommand {
    @Command("ping")
    @Description("See what ping you have to the bot.")
    @CommandPermission
    fun execute(actor: SlashCommandActor) {
        val time = System.currentTimeMillis()
        actor.replyToInteraction("Pong!").setEphemeral(true).flatMap(Function { _: InteractionHook ->
            actor.hook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)
        }).queue()
    }
}