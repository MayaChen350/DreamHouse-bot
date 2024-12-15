package io.github.mayachen350.dreamhousebot

import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.gateway.Intents
import dev.kord.gateway.NON_PRIVILEGED
import io.github.cdimascio.dotenv.Dotenv
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.util.intentsOf

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
            intents = Intents.NON_PRIVILEGED + intentsOf<MessageCreateEvent>()
        }
    }
    helloWorld()
}

fun helloWorld() =
    commands("Basics") {
        slash("Hello", "A 'Hello World' command.") {
            execute {
                respondPublic("Hello World!")
            }
        }
    }