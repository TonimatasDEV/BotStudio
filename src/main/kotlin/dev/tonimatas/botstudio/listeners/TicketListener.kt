package dev.tonimatas.botstudio.listeners

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class TicketListener : ListenerAdapter() {
    // Real
    //val channelId = "1275392672143511552"

    // Testing
    val channelId = "1395881462909370550"
    var message: Message? = null
    
    override fun onReady(event: ReadyEvent) {
        val channel = event.jda.getTextChannelById(channelId)
        
        if (channel != null) {
            val messageId = channel.latestMessageId

            channel.retrieveMessageById(messageId).queue({
                this.message = it
            }, {
                val embed = EmbedBuilder()
                    .setTitle("Tickets")
                    .setDescription("To create a ticket and request support, click 📩")
                    .setFooter("BotStudio - Ticket System", event.jda.selfUser.avatarUrl)
                    .setColor(Color.GREEN.darker())
                    .build()
                
                channel.sendMessageEmbeds(embed).addComponents(
                    ActionRow.of(
                        Button.of(ButtonStyle.SECONDARY, "create-ticket", "Create ticket", Emoji.fromFormatted("📩"))
                    )).queue { message -> 
                        this.message = message 
                    } 
            })
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        super.onButtonInteraction(event)
    }
}