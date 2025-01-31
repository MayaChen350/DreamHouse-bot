package io.github.mayachen350.dreamhousebot.features.logic

import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.effectiveName
import dev.kord.core.entity.interaction.GuildApplicationCommandInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.mayachen350.dreamhousebot.configs
import kotlinx.datetime.Clock
import me.jakejmattson.discordkt.util.addField
import me.jakejmattson.discordkt.util.toSnowflake

/** Default Embed parameters for DreamHouse Bot's logs.
 *
 * ```kt
 *     author {
 *         name = displayedUser.effectiveName
 *         icon = (displayedUser.avatar ?: displayedUser.defaultAvatar).cdnUrl.toUrl()
 *     }
 *     footer {
 *         text = displayedUser.id.toString()
 *     }
 *     timestamp = Clock.System.now()
 * ```
 **/
fun EmbedBuilder.dreamhouseEmbedLogDefault(displayedUser: User) {
    author {
        name = displayedUser.effectiveName
        icon = (displayedUser.avatar ?: displayedUser.defaultAvatar).cdnUrl.toUrl()
    }
    footer {
        text = displayedUser.id.toString()
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

// Is this even useful to do at all?
// Maybe TODO later

///** Logs an auditLog in the `MessageChannel`.
// *
// * @param auditLogEntry The audit log logged in the `MessageChannel`.**/
//suspend fun MessageChannel.auditLogLog(auditLogEntry: AuditLogEntry) {
//    auditLogEntry.userId?.let { auditLogUserId ->
//        val userFromLog: User = kord.getUser(auditLogUserId)!! // I can't think of any way it wouldn't work
//
//        createEmbed {
//            dreamhouseEmbedLogDefault(userFromLog)
//
//            title = auditLogEntry.actionType.toString() // fuck it
//            description = "Smth smth ${auditLogEntry.changes.map { it.new.toString() }}"
//        }
//    }
//}