package io.github.mayachen350.chesnaybot.utils

import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.GuildApplicationCommandInteraction

suspend fun cmdErrorHandling(
    messageChannel: MessageChannel,
    interaction: GuildApplicationCommandInteraction?,
    action: suspend (() -> Unit)
): Unit {
    try {
        action()
    } catch (e: Exception) {

        val errorMsg = when (e) {
            is BotException -> "${e.message}"
            else -> "An unexpected error occurred: ${e.message}"
        }

        if (interaction != null) interaction.respondPublic {
            content = errorMsg
        } else
            messageChannel.createMessage(errorMsg)
    }

}

class BotException(message: String) : Exception(message)