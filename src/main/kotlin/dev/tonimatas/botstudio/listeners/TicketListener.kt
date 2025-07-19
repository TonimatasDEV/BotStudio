package dev.tonimatas.botstudio.listeners

import dev.tonimatas.botstudio.data.BotData
import dev.tonimatas.botstudio.listeners.TicketListener.Companion.ButtonIds.entries
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class TicketListener : ListenerAdapter() {
    // Real
    //val channelId = "1275392672143511552"
    //val categoryId = "1275393063778521170"

    // Testing
    val channelId = "1395881462909370550"
    val categoryId = "1396090738877530132"
    var message: Message? = null
    
    override fun onReady(event: ReadyEvent) {
        val channel = event.jda.getTextChannelById(channelId)
        
        if (channel != null) {
            val messageId = channel.latestMessageId

            channel.retrieveMessageById(messageId).queue({
                this.message = it
            }, {
                val embed = defaultEmbed(event.jda, "To create a ticket and request support, click 📩")

                channel.sendMessageEmbeds(embed).addComponents(
                    ActionRow.of(
                        defaultButton(ButtonIds.CREATE, "Create ticket", "📩")
                    )).queue { message -> 
                        this.message = message 
                    } 
            })
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val buttonId = entries.firstOrNull { it.id == event.componentId }
        val category = event.jda.getCategoryById(categoryId)!!
        
        if (buttonId != null) {
            when (buttonId) {
                ButtonIds.CREATE -> {
                    val ticketNumber = BotData.increaseTicketNumber()
                    val formatted = ticketNumber.toString().padStart(4, '0')
                    
                    category.createTextChannel("ticket-$formatted").queue {
                        val embed = defaultEmbed(
                            event.jda,
                            "Support will be with you shortly. To close this ticket react with 🔒"
                        )

                        it.sendMessageEmbeds(embed).addComponents(
                            ActionRow.of(
                                defaultButton(ButtonIds.CLOSE, "Close", "🔒")
                            )
                        ).queue()

                        event.reply("✔ Ticket Created ${it.asMention}").setEphemeral(true).queue()
                    }
                }
                
                ButtonIds.CLOSE -> {
                    event.deferEdit().queue()
                    event.channel.sendMessage("Are you sure you would like to close this ticket?").addComponents(
                        ActionRow.of(
                            Button.of(ButtonStyle.DANGER, ButtonIds.CLOSE_CONFIRMATION.id, "Close"),
                            Button.of(ButtonStyle.SECONDARY, ButtonIds.CANCEL_CLOSE.id, "Cancel")
                        )).queue()
                }
                
                ButtonIds.CLOSE_CONFIRMATION -> {
                    // TODO
                }
                
                ButtonIds.CANCEL_CLOSE -> {
                    event.deferEdit().queue()
                    event.message.delete().queue()
                }
                
                ButtonIds.OPEN -> {
                    // TODO
                }
                
                ButtonIds.DELETE -> {
                    // TODO
                }
            }
        }
    }
    
    companion object {
        enum class ButtonIds(val id: String) {
            CREATE("botstudio-ticket-create"),
            CLOSE("botstudio-ticket-close"),
            CLOSE_CONFIRMATION("botstudio-ticket-close-confirmation"),
            CANCEL_CLOSE("botstudio-ticket-cancel-close"),
            OPEN("botstudio-ticket-open"),
            DELETE("botstudio-ticket-delete"),;
        }
        
        fun defaultEmbed(jda: JDA, description: String): MessageEmbed {
            return EmbedBuilder()
                .setTitle("Tickets")
                .setDescription(description)
                .setFooter("BotStudio - Ticket System", jda.selfUser.avatarUrl)
                .setColor(Color.GREEN.darker())
                .build()
        }
        
        fun defaultButton(buttonId: ButtonIds, label: String, emoji: String): Button {
            return Button.of(ButtonStyle.SECONDARY, buttonId.id, label, Emoji.fromFormatted(emoji))
        }
    }
}