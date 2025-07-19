package dev.tonimatas.botstudio.listeners

import dev.tonimatas.botstudio.data.BotData
import dev.tonimatas.botstudio.listeners.TicketListener.Companion.ButtonIds.entries
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
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
import java.util.concurrent.TimeUnit

class TicketListener : ListenerAdapter() {
    // Real
    //val channelId = "1275392672143511552"
    //val categoryId = "1275393063778521170"
    //val moderatorRoles = listOf("835908803764813834", "835905363593003008", "835909793297465375", "835905361583407115")

    // Testing
    val channelId = "1395881462909370550"
    val categoryId = "1396090738877530132"
    val moderatorRoles = listOf("1396112148706365531")
    var message: Message? = null
    
    override fun onReady(event: ReadyEvent) {
        val channel = event.jda.getTextChannelById(channelId)
        
        if (channel != null) {
            val messageId = channel.latestMessageId

            channel.retrieveMessageById(messageId).queue({
                this.message = it
            }, {
                val embed = defaultEmbed(event.jda, Color.GREEN.darker(), "To create a ticket and request support, click 📩")

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
        val member = event.member
        val guild = event.guild
        
        if (member == null || guild == null) {
            return
        }
        
        if (buttonId != null) {
            when (buttonId) {
                ButtonIds.CREATE -> {
                    val ticketNumber = BotData.increaseTicketNumber()
                    val formatted = ticketNumber.toString().padStart(4, '0')

                    val createAction = category.createTextChannel("ticket-$formatted")
                        .addPermissionOverride(member, listOf(Permission.VIEW_CHANNEL), emptyList())
                        .addPermissionOverride(guild.publicRole, emptyList(), listOf(Permission.VIEW_CHANNEL))

                    for (role in guild.roles) {
                        if (moderatorRoles.contains(role.id)) {
                            createAction.addPermissionOverride(role, listOf(Permission.VIEW_CHANNEL), emptyList())
                        }
                    }
                    
                    createAction.queue {
                        val embed = defaultEmbed(
                            event.jda,
                            Color.GREEN.darker(),
                            "Support will be with you shortly. To close this ticket react with 🔒"
                        )

                        it.sendMessage("${member.asMention} Welcome").addEmbeds(embed).addComponents(
                            ActionRow.of(
                                defaultButton(ButtonIds.CLOSE, "Close", "🔒")
                            )
                        ).queue()

                        event.reply("✔ Ticket Created ${it.asMention}").setEphemeral(true).queue()
                    }
                }
                
                ButtonIds.CLOSE -> {
                    if (event.channel.name.split("-")[0] != "closed") {
                        event.deferEdit().queue()
                        event.channel.sendMessage("Are you sure you would like to close this ticket?").addComponents(
                            ActionRow.of(
                                Button.of(ButtonStyle.DANGER, ButtonIds.CLOSE_CONFIRMATION.id, "Close"),
                                Button.of(ButtonStyle.SECONDARY, ButtonIds.CANCEL_CLOSE.id, "Cancel")
                            )
                        ).queue()
                    } else {
                        event.reply("> **Warning:** ticket already closed").setEphemeral(true).queue()
                    }
                }
                
                ButtonIds.CLOSE_CONFIRMATION -> {
                    val channel = event.channel.asTextChannel()

                    event.deferEdit().complete()
                    event.message.delete().complete()

                    TimeUnit.MILLISECONDS.sleep(500)
                    
                    val closeEmbed = EmbedBuilder().setDescription("Ticket Closed by ${event.member?.asMention}").setColor(Color.YELLOW.darker()).build()
                    channel.sendMessageEmbeds(closeEmbed).complete()
                    
                    val channelAction = channel.manager.setName("closed-${channel.name.split("-")[1]}")
                    
                    for (memberOverride in channel.memberPermissionOverrides) {
                        val overrideMember = memberOverride.member

                        if (overrideMember != null && !overrideMember.isOwner && !overrideMember.user.isBot) {
                            channelAction.putPermissionOverride(overrideMember, emptyList(), listOf(Permission.VIEW_CHANNEL))
                        }
                    }
                    
                    channelAction.complete()
                    
                    val controlsEmbed = EmbedBuilder().setDescription("```Support team ticket controls```").setColor(Color.DARK_GRAY).build()
                    channel.sendMessageEmbeds(controlsEmbed)
                        .addComponents(
                            ActionRow.of(
                                defaultButton(ButtonIds.OPEN, "Open", "🔓"),
                                defaultButton(ButtonIds.DELETE, "Delete", "⛔")
                            )
                        ).complete()
                }
                
                ButtonIds.CANCEL_CLOSE -> {
                    event.deferEdit().complete()
                    event.message.delete().queue()
                }
                
                ButtonIds.OPEN -> {
                    val channel = event.channel.asTextChannel()

                    val channelAction = channel.manager.setName("ticket-${channel.name.split("-")[1]}")

                    for (memberOverride in channel.memberPermissionOverrides) {
                        val overrideMember = memberOverride.member

                        if (overrideMember != null && !overrideMember.isOwner && !overrideMember.user.isBot) {
                            channelAction.putPermissionOverride(overrideMember, listOf(Permission.VIEW_CHANNEL), emptyList())
                        }
                    }

                    event.deferEdit().complete()
                    channelAction.queue()

                    channel.history.retrievePast(10).queue {
                        for (message in it) {
                            val contentRaw = message.embeds.first().description

                            if (contentRaw != null && contentRaw == "```Support team ticket controls```") {
                                message.delete().queue()
                                break
                            }
                        }
                    }
                    
                    val embed = EmbedBuilder().setDescription("Ticket Opened by ${event.member?.asMention}").setColor(Color.GREEN).build()
                    channel.sendMessageEmbeds(embed).queue()
                }
                
                ButtonIds.DELETE -> {
                    if (event.channel.name.split("-")[0] == "closed") {
                        event.deferEdit().queue()
                        val embed = EmbedBuilder().setDescription("Ticket will be deleted in a few seconds").setColor(Color.RED).build()
                        event.channel.sendMessageEmbeds(embed).queue {
                            TimeUnit.SECONDS.sleep(5)
                            it.channel.delete().queue()
                        }
                    }
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
        
        fun defaultEmbed(jda: JDA, color: Color, description: String): MessageEmbed {
            return EmbedBuilder()
                .setTitle("Tickets")
                .setDescription(description)
                .setFooter("BotStudio - Ticket System", jda.selfUser.avatarUrl)
                .setColor(color)
                .build()
        }
        
        fun defaultButton(buttonId: ButtonIds, label: String, emoji: String): Button {
            return Button.of(ButtonStyle.SECONDARY, buttonId.id, label, Emoji.fromFormatted(emoji))
        }
    }
}