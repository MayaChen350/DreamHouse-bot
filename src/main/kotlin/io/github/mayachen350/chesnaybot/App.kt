package io.github.mayachen350.chesnaybot

import dev.kord.cache.map.MapLikeCollection
import dev.kord.cache.map.internal.MapEntryCache
import dev.kord.cache.map.lruLinkedHashMap
import dev.kord.core.entity.Guild
import dev.kord.gateway.ALL
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.Dotenv
import io.github.mayachen350.chesnaybot.features.command.handler.moderationCommands
import io.github.mayachen350.chesnaybot.features.event.handler.logsEventListeners
import io.github.mayachen350.chesnaybot.features.event.handler.roleMessageListeners
import io.github.mayachen350.chesnaybot.features.extra.BotStatusHandler
import io.github.mayachen350.chesnaybot.features.extra.StatusBehavior
import kotlinx.coroutines.runBlocking
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.locale.Language
import me.jakejmattson.discordkt.util.toSnowflake

lateinit var getGuild: suspend () -> Guild

@OptIn(PrivilegedIntent::class)
fun main(): Unit = runBlocking {
    val token = Dotenv.load().get("BOT_TOKEN")

    BotStatusHandler.statusBehavior = StatusBehavior.Static

    bot(token) {
        configure {
            configs = data("configs/bot_configs.json") { Configs() }

            deleteInvocation = false

            commandReaction = null

            intents = Intents.ALL
        }

        localeOf(Language.EN.locale) {
            notFound = "Nothing found!"
        }

        kord {
            cache {
                messages { cache, description ->
                    MapEntryCache(cache, description, MapLikeCollection.lruLinkedHashMap(maxSize = 100))
                }
            }
        }

        onStart {
            setup(this.kord)

            BotStatusHandler.run(this@onStart, this@runBlocking)
        }
    }

    // Register those commands groups:
    helloWorld()
    moderationCommands()

    // Register those listeners:
    roleMessageListeners()
    logsEventListeners()
}

// I use this command a lot for testing
fun helloWorld() = commands("Basics") {
    slash("Hello", "A 'Hello World' command.") {
        execute {
            interaction?.getGuild()?.getMember(1193018015445430324.toSnowflake())?.fetchUser()?.kord.toString()
                .let { respondPublic(it) }
        }
    }
}