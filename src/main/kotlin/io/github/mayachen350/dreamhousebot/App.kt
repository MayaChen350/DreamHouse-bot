package io.github.mayachen350.dreamhousebot

import dev.kord.gateway.ALL
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.Dotenv
import io.github.mayachen350.dreamhousebot.features.handler.moderationCommands
import io.github.mayachen350.dreamhousebot.features.handler.roleMessageListeners
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.bot

@OptIn(PrivilegedIntent::class)
fun main() {
    val token = Dotenv.load().get("BOT_TOKEN")

    bot(token) {
        prefix { "+" }

        configure {
            //Allow slash commands to be invoked as text commands.
            dualRegistry = true

            //Remove a command invocation message after the command is executed.
            deleteInvocation = false

            //An emoji added when a command is invoked (use 'null' to disable this).
            commandReaction = null

            //Configure the Discord Gateway intents for your bot.
            intents = Intents.ALL
        }
    }

    // Register those commands groups:
    helloWorld()
    moderationCommands()

    // Register those listeners:
    roleMessageListeners()
}

fun helloWorld() = commands("Basics") {
    slash("Hello", "A 'Hello World' command.") {
        execute {
            respondPublic("Hello World!")
        }
    }
}