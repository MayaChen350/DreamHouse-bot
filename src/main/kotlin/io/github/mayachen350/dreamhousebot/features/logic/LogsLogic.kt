package io.github.mayachen350.dreamhousebot.features.logic

import dev.kord.common.entity.AuditLogEvent
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.AuditLogEntry
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.effectiveName
import dev.kord.core.entity.interaction.GuildApplicationCommandInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.mayachen350.dreamhousebot.Resources
import io.github.mayachen350.dreamhousebot.configs
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
    displayedUser: User,
    embedExtra: EmbedBuilder.() -> Unit = { },
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

/** Logs an auditLog in the `MessageChannel`.
 *
 * @param auditLogEntry The audit log logged in the `MessageChannel`.**/
suspend fun MessageChannel.auditLogLog(auditLogEntry: AuditLogEntry) {
    createEmbed {
        dreamhouseEmbedLogDefault(auditLogEntry.userId?.let { kord.getUser(it) })

        fun resLoad(prop: String): String = Resources.Logs.AUDIT_LOGS.load(prop)

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

            }

            AuditLogEvent.ChannelDelete -> TODO()
            AuditLogEvent.ChannelOverwriteCreate -> TODO()
            AuditLogEvent.ChannelOverwriteDelete -> TODO()
            AuditLogEvent.ChannelOverwriteUpdate -> TODO()
            AuditLogEvent.ChannelUpdate -> TODO()
            AuditLogEvent.CreatorMonetizationRequestCreated -> TODO()
            AuditLogEvent.CreatorMonetizationTermsAccepted -> TODO()
            AuditLogEvent.EmojiCreate -> TODO()
            AuditLogEvent.EmojiDelete -> TODO()
            AuditLogEvent.EmojiUpdate -> TODO()
            AuditLogEvent.GuildScheduledEventCreate -> TODO()
            AuditLogEvent.GuildScheduledEventDelete -> TODO()
            AuditLogEvent.GuildScheduledEventUpdate -> TODO()
            AuditLogEvent.GuildUpdate -> TODO()
            AuditLogEvent.IntegrationCreate -> TODO()
            AuditLogEvent.IntegrationDelete -> TODO()
            AuditLogEvent.IntegrationUpdate -> TODO()
            AuditLogEvent.InviteCreate -> TODO()
            AuditLogEvent.InviteDelete -> TODO()
            AuditLogEvent.InviteUpdate -> TODO()
            AuditLogEvent.MemberBanAdd -> TODO()
            AuditLogEvent.MemberBanRemove -> TODO()
            AuditLogEvent.MemberDisconnect -> TODO()
            AuditLogEvent.MemberKick -> TODO()
            AuditLogEvent.MemberMove -> TODO()
            AuditLogEvent.MemberPrune -> TODO()
            AuditLogEvent.MemberRoleUpdate -> TODO()
            AuditLogEvent.MemberUpdate -> TODO()
            AuditLogEvent.MessageBulkDelete -> TODO()
            AuditLogEvent.MessageDelete -> TODO()
            AuditLogEvent.MessagePin -> TODO()
            AuditLogEvent.MessageUnpin -> TODO()
            AuditLogEvent.RoleCreate -> TODO()
            AuditLogEvent.RoleDelete -> TODO()
            AuditLogEvent.RoleUpdate -> TODO()
            AuditLogEvent.StageInstanceCreate -> TODO()
            AuditLogEvent.StageInstanceDelete -> TODO()
            AuditLogEvent.StageInstanceUpdate -> TODO()
            AuditLogEvent.StickerCreate -> TODO()
            AuditLogEvent.StickerDelete -> TODO()
            AuditLogEvent.StickerUpdate -> TODO()
            AuditLogEvent.ThreadCreate -> TODO()
            AuditLogEvent.ThreadDelete -> TODO()
            AuditLogEvent.ThreadUpdate -> TODO()
            is AuditLogEvent.Unknown -> TODO()
            AuditLogEvent.WebhookCreate -> TODO()
            AuditLogEvent.WebhookDelete -> TODO()
            AuditLogEvent.WebhookUpdate -> TODO()
        }
        description = "Smth smth ${auditLogEntry.changes.map { it.new.toString() }}"
    }
}