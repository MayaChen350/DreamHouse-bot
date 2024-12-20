package io.github.mayachen350.dreamhousebot.features.handler

import io.github.mayachen350.dreamhousebot.features.logic.banCmdLogic
import io.github.mayachen350.dreamhousebot.features.logic.kickCmdLogic
import io.github.mayachen350.dreamhousebot.features.logic.muteCmdLogic
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.MemberArg
import me.jakejmattson.discordkt.arguments.TimeArg
import me.jakejmattson.discordkt.commands.commands

fun moderationCommands() = commands("Moderation") {

    val memberToBePunishedArg: MemberArg = MemberArg("User", "User to be punished.")
    val reasonPunisment: AnyArg = AnyArg("Reason", "The reason of the punishment.")
    val timeoutPunishmentArg: TimeArg = TimeArg("Amount", "How long the punishment last.")


    slash("kick", "Kick someone annoying.") {
        execute(memberToBePunishedArg, reasonPunisment) {
            kickCmdLogic(this)
        }
    }

    slash("ban", "Ban someone annoying.") {
        execute(memberToBePunishedArg, reasonPunisment) {
            banCmdLogic(this)
        }
    }

    slash("mute", "Mute someone annoying.") {
        execute(memberToBePunishedArg, timeoutPunishmentArg, reasonPunisment) {
            muteCmdLogic(this)
        }
    }
}