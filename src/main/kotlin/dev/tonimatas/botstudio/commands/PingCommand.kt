package dev.tonimatas.botstudio.commands

import dev.tonimatas.cjda.slash.SlashCommand
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import java.util.function.Function

class PingCommand : SlashCommand {
    override fun execute(interaction: SlashCommandInteraction) {
        val time = System.currentTimeMillis()
        interaction.reply("Pong!").setEphemeral(true).flatMap(Function { v: InteractionHook ->
            interaction.hook.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)
        }).queue()
    }

    override fun getName(): String {
        return "ping"
    }

    override fun getDescription(): String {
        return "Pong!"
    }

    override fun getContexts(): Set<InteractionContextType> {
        return InteractionContextType.ALL
    }
}