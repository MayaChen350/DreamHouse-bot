package io.github.mayachen350.chesnaybot.features.command.handler

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import io.github.mayachen350.chesnaybot.features.command.logic.banCmdLogic
import io.github.mayachen350.chesnaybot.features.command.logic.kickCmdLogic
import io.github.mayachen350.chesnaybot.features.command.logic.muteCmdLogic
import io.github.mayachen350.chesnaybot.features.command.logic.purgeCmdLogic
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.arguments.MemberArg
import me.jakejmattson.discordkt.arguments.TimeArg
import me.jakejmattson.discordkt.commands.commands

fun moderationCommands() = commands(
    "Moderation"
) {

    val memberToBePunishedArg = MemberArg("user", "User to be punished.")
    val reasonPunishment = AnyArg("reason", "The reason of the punishment.")
    val timeoutPunishmentArg = TimeArg("time", "How long the punishment last.")
    
    slash(
        "kick", "Kick someone annoying.",
        requiredPermissions = Permissions(Permission.KickMembers)
    ) {
        execute(memberToBePunishedArg, reasonPunishment) {
            kickCmdLogic(this.interaction!!, this.args)
        }
    }

    slash(
        "ban", "Ban someone annoying.",
        requiredPermissions = Permissions(Permission.BanMembers)
    ) {
        execute(memberToBePunishedArg, reasonPunishment) {
            banCmdLogic(this.interaction!!, this.args)
        }
    }

    slash(
        "mute", "Mute someone annoying.",
        requiredPermissions = Permissions(Permission.ModerateMembers)
    ) {
        execute(memberToBePunishedArg, timeoutPunishmentArg, reasonPunishment) {
            muteCmdLogic(this.interaction!!, args = this.args)
        }
    }

    slash(
        "purge", "Delete a number of messages.",
        requiredPermissions = Permissions(Permission.ManageMessages)
    ) {
        execute(IntegerArg("number_of_message", "Amount of messages to be deleted from bottom to top.")) {
            purgeCmdLogic(this.interaction!!, this.args)
        }
    }
}