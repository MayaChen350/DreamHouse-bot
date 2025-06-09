package io.github.mayachen350.chesnaybot.features.command.logic

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Member
import dev.kord.core.entity.interaction.GuildApplicationCommandInteraction
import io.github.mayachen350.chesnaybot.features.event.logic.logModPunishment
import io.github.mayachen350.chesnaybot.log
import io.github.mayachen350.chesnaybot.resources.ModerationStrings
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import me.jakejmattson.discordkt.Args1
import me.jakejmattson.discordkt.Args2
import me.jakejmattson.discordkt.Args3
import kotlin.time.DurationUnit
import kotlin.time.toDuration

suspend fun punishMemberLogic(
    interaction: GuildApplicationCommandInteraction,
    args: Args2<Member, String>,
    userAfterstate: String,
    memberPunishment: suspend Member.() -> Unit
): Unit {
    val result: String = ModerationStrings.DEFAULT_RESULT_ANSWER
        .replace("!MEMBERID!", "${args.first.id}")
        .replace("!USERAFTERSTATE!", userAfterstate)
        .replace("!REASON!", args.second)

    with(interaction) {
        guild.getMember(args.first.id).memberPunishment()
        respondPublic { content = result }
    }
}

suspend fun kickCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args2<Member, String>): Unit {
    punishMemberLogic(interaction, args, ModerationStrings.KICK_AFTERSTATE) {
        kick(reason = args.second)
    }

    logModPunishment(interaction, "Kicked", args.second, args.first)
}

suspend fun banCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args2<Member, String>): Unit {
    punishMemberLogic(interaction, args, ModerationStrings.BAN_AFTERSTATE) {
        ban {
            this.reason = args.second
            deleteMessageDuration = 7.toDuration(DurationUnit.DAYS)
        }
    }

    logModPunishment(interaction, "Banned", args.second, args.first)
}

suspend fun muteCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args3<Member, Int, String>): Unit {
    punishMemberLogic(
        interaction,
        Args2(args.first, args.third),
        ModerationStrings.MUTE_AFTERSTATE
    ) {
        edit {
            // Timeout until Right Now + the time argument for the mute
            communicationDisabledUntil = Clock.System.now().plus(args.second.toDuration(DurationUnit.SECONDS))
            reason = args.third
        }
    }

    logModPunishment(interaction, "Muted", args.third, args.first, "For ${args.second}")
}

suspend fun purgeCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args1<Int>): Unit {
    val numberOfMessages = args.first

    with(interaction) {
        with(channel) {
            val listOfMessagesToDelete: Iterable<Snowflake> =
                createMessage(
                    ModerationStrings.DELETING_MESSAGES_MSG
                        .replace("!NBMESSAGES!", numberOfMessages.toString())
                ).id.run {
                    getMessagesBefore(this).take(numberOfMessages - 1).map { it.id }
                        .toList().also { deleteMessage(this) }
                }

            bulkDelete(listOfMessagesToDelete)
        }

        respondEphemeral {
            content = ModerationStrings.DELETED_MESSAGES_AFTERMATH_ANS
                .replace("!NBMESSAGES!", numberOfMessages.toString())
        }
    }

    log(interaction.getGuild(), interaction.user) {
        title = "Deleted $numberOfMessages messages."
        description = "in <#${interaction.channel.id.value}>"
    }
}