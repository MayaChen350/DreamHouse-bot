package io.github.mayachen350.dreamhousebot.features.command.logic

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Member
import dev.kord.core.entity.interaction.GuildApplicationCommandInteraction
import io.github.mayachen350.dreamhousebot.features.event.logic.logModPunishment
import io.github.mayachen350.dreamhousebot.features.event.logic.logSmth
import io.github.mayachen350.dreamhousebot.utils.Resources
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
    val result: String = Resources.BotMessages.MODERATION.load("default_result_answer")
        .replace("!MEMBERID!", "${args.first.id}")
        .replace("!USERAFTERSTATE!", userAfterstate)
        .replace("!REASON!", args.second)

    with(interaction) {
        guild.getMember(args.first.id).memberPunishment()
        respondPublic { content = result }
    }
}

suspend fun kickCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args2<Member, String>): Unit {
    punishMemberLogic(interaction, args, Resources.BotMessages.MODERATION.load("kick_afterstate")) {
        kick(reason = args.second)
    }

    logModPunishment(interaction, "Kicked", args.second, args.first)
}

suspend fun banCmdLogic(interaction: GuildApplicationCommandInteraction, args: Args2<Member, String>): Unit {
    punishMemberLogic(interaction, args, Resources.BotMessages.MODERATION.load("ban_afterstate")) {
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
        Resources.BotMessages.MODERATION.load("mute_afterstate")
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
                    Resources.BotMessages.MODERATION.load("deleting_messages_msg")
                        .replace("!NBMESSAGES!", numberOfMessages.toString())
                ).id.run {
                    getMessagesBefore(this).take(numberOfMessages - 1).map { it.id }
                        .toList().also { deleteMessage(this) }
                }

            bulkDelete(listOfMessagesToDelete)
        }

        respondEphemeral {
            content = Resources.BotMessages.MODERATION.load("deleted_messages_aftermath_ans")
                .replace("!NBMESSAGES!", numberOfMessages.toString())
        }
    }

    logSmth(interaction.getGuild(), interaction.user) {
        title = "Deleted $numberOfMessages messages."
        description = "in <#${interaction.channel.id.value}>"
    }
}