package io.github.mayachen350.dreamhousebot.features.logic

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

/** Logs an effect in the log channel. This is the base function for log functions.
 *
 *  @param interaction The discord interaction required to do the logs.
 *  @param displayedUser The user we're going to have information displayed of. Default to the one associated to the interaction.
 *  @param embedExtra Extra embed things to add for functions or anonymous functions.**/
suspend fun dreamhouseLog(
    interaction: GuildApplicationCommandInteraction,
    displayedUser: User = interaction.user,
    embedExtra: suspend EmbedBuilder.() -> Unit = { },
): Unit {
    interaction.getGuild().getChannel(configs.logChannelId.toSnowflake()).asChannelOf<MessageChannel>()
        .createEmbed {
            author {
                name = displayedUser.effectiveName
                icon = displayedUser.avatar?.cdnUrl?.toUrl()
            }
            footer {
                text = displayedUser.id.toString()
            }
            timestamp = Clock.System.now()
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
    dreamhouseLog(interaction, punishedMember) {
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

