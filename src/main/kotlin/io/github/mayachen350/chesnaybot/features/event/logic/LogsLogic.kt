package io.github.mayachen350.chesnaybot.features.event.logic

import dev.kord.common.entity.AuditLogEvent
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.AuditLogEntry
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.effectiveName
import dev.kord.core.entity.interaction.GuildApplicationCommandInteraction
import dev.kord.core.event.message.MessageUpdateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.mayachen350.chesnaybot.log
import io.github.mayachen350.chesnaybot.resources.AuditLogsStrings
import kotlinx.datetime.Clock
import me.jakejmattson.discordkt.util.addField

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

/** Logs a moderation punishment in the log channel. **/
suspend fun logModPunishment(
    interaction: GuildApplicationCommandInteraction,
    effectivePunishment: String,
    reason: String,
    punishedMember: User,
    additionalInformation: String = ""
) {
    log(interaction.getGuild(), punishedMember) {
        title = "has been found guilty."
        description = "Time for punishment!"
        addField(
            name = "$effectivePunishment.",
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
    log(message.getGuild(), message.author) {
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

        description = "Smth smth ${auditLogEntry.changes.map { it.new.toString() }}"

        when (auditLogEntry.actionType) {
            AuditLogEvent.ApplicationCommandPermissionUpdate -> {
                title = AuditLogsStrings.APPLICATION_COMMAND_PERMISSION_UPDATE_TITLE
            }

            AuditLogEvent.AutoModerationBlockMessage -> {
                title = AuditLogsStrings.AUTO_MODERATION_BLOCK_MESSAGE_TITLE
            }

            AuditLogEvent.AutoModerationFlagToChannel -> {
                title = AuditLogsStrings.AUTO_MODERATION_FLAG_TO_CHANNEL_TITLE
            }

            AuditLogEvent.AutoModerationRuleCreate -> {
                title = AuditLogsStrings.AUTO_MODERATION_RULE_CREATE_TITLE
            }

            AuditLogEvent.AutoModerationRuleDelete -> {
                title = AuditLogsStrings.AUTO_MODERATION_RULE_DELETE_TITLE
            }

            AuditLogEvent.AutoModerationRuleUpdate -> {
                title = AuditLogsStrings.AUTO_MODERATION_RULE_UPDATE_TITLE
            }

            AuditLogEvent.AutoModerationUserCommunicationDisabled -> {
                title = AuditLogsStrings.AUTO_MODERATION_USER_COMMUNICATION_DISABLED
            }

            AuditLogEvent.BotAdd -> {
                title = AuditLogsStrings.BOT_ADD_TITLE
            }

            AuditLogEvent.ChannelCreate -> {
                title = AuditLogsStrings.CHANNEL_CREATE_TITLE
            }

            AuditLogEvent.ChannelDelete -> {
                title = AuditLogsStrings.CHANNEL_DELETE_TITLE
            }

            AuditLogEvent.ChannelOverwriteCreate -> {
                title = AuditLogsStrings.CHANNEL_OVERWRITE_CREATE_TITLE
            }

            AuditLogEvent.ChannelOverwriteDelete -> {
                title = AuditLogsStrings.CHANNEL_OVERWRITE_DELETE_TITLE
            }

            AuditLogEvent.ChannelOverwriteUpdate -> {
                title = AuditLogsStrings.CHANNEL_OVERWRITE_UPDATE_TITLE
            }

            AuditLogEvent.ChannelUpdate -> {
                title = AuditLogsStrings.CHANNEL_UPDATE_TITLE
            }

            AuditLogEvent.CreatorMonetizationRequestCreated -> {
                title = AuditLogsStrings.CREATOR_MONETIZATION_REQUEST_CREATED_TITLE
            }

            AuditLogEvent.CreatorMonetizationTermsAccepted -> {
                title = AuditLogsStrings.CREATOR_MONETIZATION_TERMS_ACCEPTED_TITLE
            }

            AuditLogEvent.EmojiCreate -> {
                title = AuditLogsStrings.EMOJI_CREATE_TITLE
            }

            AuditLogEvent.EmojiDelete -> {
                title = AuditLogsStrings.EMOJI_DELETE_TITLE
            }

            AuditLogEvent.EmojiUpdate -> {
                title = AuditLogsStrings.EMOJI_UPDATE_TITLE
            }

            AuditLogEvent.GuildScheduledEventCreate -> {
                title = AuditLogsStrings.GUILD_SCHEDULED_EVENT_CREATE_TITLE
            }

            AuditLogEvent.GuildScheduledEventDelete -> {
                title = AuditLogsStrings.GUILD_SCHEDULED_EVENT_DELETE_TITLE
            }

            AuditLogEvent.GuildScheduledEventUpdate -> {
                title = AuditLogsStrings.GUILD_SCHEDULED_EVENT_UPDATE_TITLE
            }

            AuditLogEvent.GuildUpdate -> {
                title = AuditLogsStrings.GUILD_UPDATE_TITLE
            }

            AuditLogEvent.IntegrationCreate -> {
                title = AuditLogsStrings.INTEGRATION_CREATE_TITLE
            }

            AuditLogEvent.IntegrationDelete -> {
                title = AuditLogsStrings.INTEGRATION_DELETE_TITLE
            }

            AuditLogEvent.IntegrationUpdate -> {
                title = AuditLogsStrings.INTEGRATION_UPDATE_TITLE
            }

            AuditLogEvent.InviteCreate -> {
                title = AuditLogsStrings.INVITE_CREATE_TITLE
            }

            AuditLogEvent.InviteDelete -> {
                title = AuditLogsStrings.INVITE_DELETE_TITLE
            }

            AuditLogEvent.InviteUpdate -> {
                title = AuditLogsStrings.INVITE_UPDATE_TITLE
            }

            AuditLogEvent.MemberBanAdd -> {
                title = AuditLogsStrings.MEMBER_BAN_ADD_TITLE
            }

            AuditLogEvent.MemberBanRemove -> {
                title = AuditLogsStrings.MEMBER_BAN_REMOVE_TITLE
            }

            AuditLogEvent.MemberDisconnect -> {
                title = AuditLogsStrings.MEMBER_DISCONNECT_TITLE
            }

            AuditLogEvent.MemberKick -> {
                title = AuditLogsStrings.MEMBER_KICK_TITLE
            }

            AuditLogEvent.MemberMove -> {
                title = AuditLogsStrings.MEMBER_MOVE_TITLE
            }

            AuditLogEvent.MemberPrune -> {
                title = AuditLogsStrings.MEMBER_PRUNE_TITLE
            }

            AuditLogEvent.MemberRoleUpdate -> {
                title = AuditLogsStrings.MEMBER_ROLE_UPDATE_TITLE
            }

            AuditLogEvent.MemberUpdate -> {
                title = AuditLogsStrings.MEMBER_UPDATE_TITLE
            }

            AuditLogEvent.MessageBulkDelete -> {
                title = AuditLogsStrings.MESSAGE_BULK_DELETE_TITLE
            }

            AuditLogEvent.MessageDelete -> {
                title = AuditLogsStrings.MESSAGE_DELETE_TITLE
            }

            AuditLogEvent.MessagePin -> {
                title = AuditLogsStrings.MESSAGE_PIN_TITLE
            }

            AuditLogEvent.MessageUnpin -> {
                title = AuditLogsStrings.MESSAGE_UNPIN_TITLE
            }

            AuditLogEvent.RoleCreate -> {
                title = AuditLogsStrings.ROLE_CREATE_TITLE
            }

            AuditLogEvent.RoleDelete -> {
                title = AuditLogsStrings.ROLE_DELETE_TITLE
            }

            AuditLogEvent.RoleUpdate -> {
                title = AuditLogsStrings.ROLE_UPDATE_TITLE
            }

            AuditLogEvent.StageInstanceCreate -> {
                title = AuditLogsStrings.STAGE_INSTANCE_CREATE_TITLE
            }

            AuditLogEvent.StageInstanceDelete -> {
                title = AuditLogsStrings.STAGE_INSTANCE_DELETE_TITLE
            }

            AuditLogEvent.StageInstanceUpdate -> {
                title = AuditLogsStrings.STAGE_INSTANCE_UPDATE_TITLE
            }

            AuditLogEvent.StickerCreate -> {
                title = AuditLogsStrings.STICKER_CREATE_TITLE
            }

            AuditLogEvent.StickerDelete -> {
                title = AuditLogsStrings.STICKER_DELETE_TITLE
            }

            AuditLogEvent.StickerUpdate -> {
                title = AuditLogsStrings.STICKER_UPDATE_TITLE
            }

            AuditLogEvent.ThreadCreate -> {
                title = AuditLogsStrings.THREAD_CREATE_TITLE
            }

            AuditLogEvent.ThreadDelete -> {
                title = AuditLogsStrings.THREAD_DELETE_TITLE
            }

            AuditLogEvent.ThreadUpdate -> {
                title = AuditLogsStrings.THREAD_UPDATE_TITLE
            }

            is AuditLogEvent.Unknown -> {
                title = AuditLogsStrings.UNKNOWN_TITLE
            }

            AuditLogEvent.WebhookCreate -> {
                title = AuditLogsStrings.WEBHOOK_CREATE_TITLE
            }

            AuditLogEvent.WebhookDelete -> {
                title = AuditLogsStrings.WEBHOOK_DELETE_TITLE
            }

            AuditLogEvent.WebhookUpdate -> {
                title = AuditLogsStrings.WEBHOOK_UPDATE_TITLE
            }
        }
    }
}