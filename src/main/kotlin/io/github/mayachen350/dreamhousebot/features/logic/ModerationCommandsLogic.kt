package io.github.mayachen350.dreamhousebot.features.logic

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Member
import dev.kord.core.entity.interaction.GuildApplicationCommandInteraction
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
    memberPunishment: suspend Member.(String) -> Unit
): Unit {
    val member = args.first
    val reason = args.second
    val result: String =
        "That one user with ID: `${member.id}` was **$userAfterstate** for this reason:\n:sparkles: $reason :sparkles:"

    with(interaction) {
        guild.getMember(member.id).memberPunishment(reason)
        respondPublic { content = result }
    }
}

suspend fun kickCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args2<Member, String>): Unit {
    punishMemberLogic(interaction, args, "kicked") {
        kick(reason = args.second)
    }
}

suspend fun banCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args2<Member, String>): Unit {
    punishMemberLogic(interaction, args, "finally banned") {
        ban {
            this.reason = args.second
            deleteMessageDuration = 7.toDuration(DurationUnit.DAYS)
        }
    }
}

suspend fun muteCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args3<Member, Int, String>): Unit {
    punishMemberLogic(interaction, Args2(args.first, args.third), "muted") {
        edit {
            // Timeout until Right Now + the time argument for the mute
            communicationDisabledUntil = Clock.System.now().plus(args.second.toDuration(DurationUnit.SECONDS))
            reason = args.third
        }
    }
}

suspend fun purgeCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args1<Int>): Unit {
    val numberOfMessages = args.first

    with(interaction) {
        with(channel) {
            val listOfMessagesToDelete: Iterable<Snowflake> =
                createMessage("Deleting $numberOfMessages messages :3").id.run {
                    getMessagesBefore(this).take(numberOfMessages - 1).map { it.id }
                        .toList().also { deleteMessage(this) }
                }

            bulkDelete(listOfMessagesToDelete)
        }

        respondEphemeral { content = "$numberOfMessages messages have been successfully deleted!" }
    }
}