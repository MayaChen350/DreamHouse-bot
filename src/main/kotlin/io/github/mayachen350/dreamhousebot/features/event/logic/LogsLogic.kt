package io.github.mayachen350.dreamhousebot.features.event.logic

import dev.kord.common.entity.AuditLogEvent
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.AuditLogEntry
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.effectiveName
import dev.kord.core.entity.interaction.GuildApplicationCommandInteraction
import dev.kord.core.event.message.MessageUpdateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.mayachen350.dreamhousebot.configs
import io.github.mayachen350.dreamhousebot.utils.Resources
import kotlinx.datetime.Clock
import me.jakejmattson.discordkt.util.addField
import me.jakejmattson.discordkt.util.toSnowflake

/** Default Embed parameters for DreamHouse Bot's logs.
 *
 * ```kt
 *     if (displayedUser != null) {
 *         author {
 *             name = displayedUser.effectiveName
 *             icon = (displayedUser.avatar ?: displayedUser.defaultAvatar).cdnUrl.toUrl()
 *         }
 *         footer {
 *             text = displayedUser.id.toString()
 *         }
 *     }
 *     timestamp = Clock.System.now()
 * ```
 **/
fun EmbedBuilder.dreamhouseEmbedLogDefault(displayedUser: User?) {
    if (displayedUser != null) {
        author {
            name = displayedUser.effectiveName
            icon = (displayedUser.avatar ?: displayedUser.defaultAvatar).cdnUrl.toUrl()
        }
        footer {
            text = displayedUser.id.toString()
        }
    }
    timestamp = Clock.System.now()
}

/** Logs an effect in the log channel. This is the base function for log functions.
 *
 *  @param guild The discord guild (the server) parameter required to find the logs channel where to send the logs.
 *  @param displayedUser The user we're going to have information displayed of. Default to the one associated to the interaction.
 *  @param embedExtra Extra embed things to add for functions or anonymous functions.**/
suspend fun logSmth(
    guild: GuildBehavior,
    displayedUser: User?,
    embedExtra: suspend EmbedBuilder.() -> Unit = { },
): Unit {
    with(guild.getChannelOrNull(configs.logChannelId.toSnowflake())) {
        if (this != null)
            asChannelOf<MessageChannel>()
                .createEmbed {
                    dreamhouseEmbedLogDefault(displayedUser)
                    embedExtra()
                }
        else
            println("Could not log the command log! Log channelId undefined or set to an invalid id.")
    }
}

/** Logs a moderation punishment in the log channel. **/
suspend fun logModPunishment(
    interaction: GuildApplicationCommandInteraction,
    effectivePunishment: String,
    reason: String,
    punishedMember: User,
    additionalInformation: String = ""
): Unit {
    logSmth(interaction.getGuild(), punishedMember) {
        title = "has been found guilty."
        description = "Time for punishment!"
        addField(
            name = effectivePunishment + ".",
            value = "Reason: $reason" + if (additionalInformation != "") "\n$additionalInformation" else ""
        )

        addField(
            name = "Punishment by:",
            value = interaction.user.username
        )
    }
}

// Unused for now
//suspend fun logDeletedMessage(
//    event: MessageDeleteEvent
//) {
//    val message: Message? = event.message
//    logSmth(event.guild!!, message?.author) {
//        dreamhouseEmbedLogDefault(message?.author)
//        title = "Message deleted"
//        description = message?.content ?: "message id:"
//    }
//}

suspend fun logEditedMessage(
    event: MessageUpdateEvent
) {
    val message: Message = event.getMessage()
    logSmth(message.getGuild(), message.author) {
        dreamhouseEmbedLogDefault(message.author)
        title = "Message edited"
        description = (event.old?.content ?: "Impossible to fetch") + "->" + message.content
    }
}

/** Logs an auditLog in the `MessageChannel`.
 *
 * @param auditLogEntry The audit log logged in the `MessageChannel`.**/
suspend fun MessageChannel.auditLogLog(auditLogEntry: AuditLogEntry) {
    createEmbed {
        dreamhouseEmbedLogDefault(auditLogEntry.userId?.let { kord.getUser(it) })

        fun resLoad(prop: String): String = Resources.Logs.AUDIT_LOGS.load(prop)

        description = "Smth smth ${auditLogEntry.changes.map { it.new.toString() }}"

        when (auditLogEntry.actionType) {
            AuditLogEvent.ApplicationCommandPermissionUpdate -> {
                title = resLoad("application_command_permission_update_title")
            }

            AuditLogEvent.AutoModerationBlockMessage -> {
                title = resLoad("auto_moderation_block_message_title")
            }

            AuditLogEvent.AutoModerationFlagToChannel -> {
                title = resLoad("auto_moderation_flag_to_channel_title")
            }

            AuditLogEvent.AutoModerationRuleCreate -> {
                title = resLoad("auto_moderation_rule_create_title")
            }

            AuditLogEvent.AutoModerationRuleDelete -> {
                title = resLoad("auto_moderation_rule_delete_title")
            }

            AuditLogEvent.AutoModerationRuleUpdate -> {
                title = resLoad("auto_moderation_rule_update_title")
            }

            AuditLogEvent.AutoModerationUserCommunicationDisabled -> {
                title = resLoad("auto_moderation_user_communication_disabled")
            }

            AuditLogEvent.BotAdd -> {
                title = resLoad("bot_add_title")
            }

            AuditLogEvent.ChannelCreate -> {
                title = resLoad("channel_create_title")
            }

            AuditLogEvent.ChannelDelete -> {
                title = resLoad("channel_delete_title")
            }

            AuditLogEvent.ChannelOverwriteCreate -> {
                title = resLoad("channel_overwrite_create_title")
            }

            AuditLogEvent.ChannelOverwriteDelete -> {
                title = resLoad("channel_overwrite_delete_title")
            }

            AuditLogEvent.ChannelOverwriteUpdate -> {
                title = resLoad("channel_overwrite_update_title")
            }

            AuditLogEvent.ChannelUpdate -> {
                title = resLoad("channel_update_title")
            }

            AuditLogEvent.CreatorMonetizationRequestCreated -> {
                title = resLoad("creator_monetization_request_created_title")
            }

            AuditLogEvent.CreatorMonetizationTermsAccepted -> {
                title = resLoad("creator_monetization_terms_accepted_title")
            }

            AuditLogEvent.EmojiCreate -> {
                title = resLoad("emoji_create_title")
            }

            AuditLogEvent.EmojiDelete -> {
                title = resLoad("emoji_delete_title")
            }

            AuditLogEvent.EmojiUpdate -> {
                title = resLoad("emoji_update_title")
            }

            AuditLogEvent.GuildScheduledEventCreate -> {
                title = resLoad("guild_scheduled_event_create_title")
            }

            AuditLogEvent.GuildScheduledEventDelete -> {
                title = resLoad("guild_scheduled_event_delete_title")
            }

            AuditLogEvent.GuildScheduledEventUpdate -> {
                title = resLoad("guild_scheduled_event_update_title")
            }

            AuditLogEvent.GuildUpdate -> {
                title = resLoad("guild_update_title")
            }

            AuditLogEvent.IntegrationCreate -> {
                title = resLoad("integration_create_title")
            }

            AuditLogEvent.IntegrationDelete -> {
                title = resLoad("integration_delete_title")
            }

            AuditLogEvent.IntegrationUpdate -> {
                title = resLoad("integration_update_title")
            }

            AuditLogEvent.InviteCreate -> {
                title = resLoad("invite_create_title")
            }

            AuditLogEvent.InviteDelete -> {
                title = resLoad("invite_delete_title")
            }

            AuditLogEvent.InviteUpdate -> {
                title = resLoad("invite_update_title")
            }

            AuditLogEvent.MemberBanAdd -> {
                title = resLoad("member_ban_add_title")
            }

            AuditLogEvent.MemberBanRemove -> {
                title = resLoad("member_ban_remove_title")
            }

            AuditLogEvent.MemberDisconnect -> {
                title = resLoad("member_disconnect_title")
            }

            AuditLogEvent.MemberKick -> {
                title = resLoad("member_kick_title")
            }

            AuditLogEvent.MemberMove -> {
                title = resLoad("member_move_title")
            }

            AuditLogEvent.MemberPrune -> {
                title = resLoad("member_prune_title")
            }

            AuditLogEvent.MemberRoleUpdate -> {
                title = resLoad("member_role_update_title")
            }

            AuditLogEvent.MemberUpdate -> {
                title = resLoad("member_update_title")
            }

            AuditLogEvent.MessageBulkDelete -> {
                title = resLoad("message_bulk_delete_title")
            }

            AuditLogEvent.MessageDelete -> {
                title = resLoad("message_delete_title")
            }

            AuditLogEvent.MessagePin -> {
                title = resLoad("message_pin_title")
            }

            AuditLogEvent.MessageUnpin -> {
                title = resLoad("message_unpin_title")
            }

            AuditLogEvent.RoleCreate -> {
                title = resLoad("role_create_title")
            }

            AuditLogEvent.RoleDelete -> {
                title = resLoad("role_delete_title")
            }

            AuditLogEvent.RoleUpdate -> {
                title = resLoad("role_update_title")
            }

            AuditLogEvent.StageInstanceCreate -> {
                title = resLoad("stage_instance_create_title")
            }

            AuditLogEvent.StageInstanceDelete -> {
                title = resLoad("stage_instance_delete_title")
            }

            AuditLogEvent.StageInstanceUpdate -> {
                title = resLoad("stage_instance_update_title")
            }

            AuditLogEvent.StickerCreate -> {
                title = resLoad("sticker_create_title")
            }

            AuditLogEvent.StickerDelete -> {
                title = resLoad("sticker_delete_title")
            }

            AuditLogEvent.StickerUpdate -> {
                title = resLoad("sticker_update_title")
            }

            AuditLogEvent.ThreadCreate -> {
                title = resLoad("thread_create_title")
            }

            AuditLogEvent.ThreadDelete -> {
                title = resLoad("thread_delete_title")
            }

            AuditLogEvent.ThreadUpdate -> {
                title = resLoad("thread_update_title")
            }

            is AuditLogEvent.Unknown -> {
                title = resLoad("unknown_title")
            }

            AuditLogEvent.WebhookCreate -> {
                title = resLoad("webhook_create_title")
            }

            AuditLogEvent.WebhookDelete -> {
                title = resLoad("webhook_delete_title")
            }

            AuditLogEvent.WebhookUpdate -> {
                title = resLoad("webhook_update_title")
            }
        }
    }
}