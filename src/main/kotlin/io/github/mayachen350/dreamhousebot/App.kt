package io.github.mayachen350.dreamhousebot

import dev.kord.gateway.ALL
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.Dotenv
import io.github.mayachen350.dreamhousebot.features.handler.moderationCommands
import io.github.mayachen350.dreamhousebot.features.handler.roleMessageListeners
import io.github.mayachen350.dreamhousebot.utils.BotException
import io.github.mayachen350.dreamhousebot.utils.cmdErrorHandling
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.locale.Language

@OptIn(PrivilegedIntent::class)
fun main() {
    val token = Dotenv.load().get("BOT_TOKEN")

    bot(token) {
        configure {
            //Remove a command invocation message after the command is executed.
            deleteInvocation = false

            //An emoji added when a command is invoked (use 'null' to disable this).
            commandReaction = null

            //Configure the Discord Gateway intents for your bot.
            intents = Intents.ALL
        }

        presence { state = "Real" }
    }

    // Register those commands groups:
    helloWorld()
    moderationCommands()

    // Register those listeners:
    roleMessageListeners()
}

// I use this command a lot for testing
fun helloWorld() = commands("Basics") {
    slash("Hello", "A 'Hello World' command.") {
        execute {
            respondPublic("Hello World!")
        }
    }
}