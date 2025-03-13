package io.github.mayachen350.dreamhousebot

import dev.kord.cache.map.MapLikeCollection
import dev.kord.cache.map.internal.MapEntryCache
import dev.kord.cache.map.lruLinkedHashMap
import dev.kord.gateway.ALL
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.Dotenv
import io.github.mayachen350.dreamhousebot.features.handler.logsEventListeners
import io.github.mayachen350.dreamhousebot.features.handler.moderationCommands
import io.github.mayachen350.dreamhousebot.features.handler.roleMessageListeners
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.locale.Language
import me.jakejmattson.discordkt.util.toSnowflake
import java.io.FileInputStream

private lateinit var presenceLines: List<String>

@OptIn(PrivilegedIntent::class)
fun main(): Unit = runBlocking {
    val token = Dotenv.load().get("BOT_TOKEN")

    with(FileInputStream("src/main/resources/raw/lyrics.txt"))
    {
        presenceLines = bufferedReader().readLines()
        close()
    }

    bot(token) {
        configure {
            configs = data("configs/bot_configs.json") { Configs() }

            //Remove a command invocation message after the command is executed.
            deleteInvocation = false

            //An emoji added when a command is invoked (use 'null' to disable this).
            commandReaction = null

            //Configure the Discord Gateway intents for your bot.
            intents = Intents.ALL
        }

//        presence { state = "Real" }

        //Configure the locale for this bot.
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
            launch {
                var indexLyrics: Int = 0
                fun incrementIndex() {
                    if (indexLyrics < presenceLines.size - 1) indexLyrics++ else indexLyrics = 0
                }

                while (true) {
                    this@onStart.kord.editPresence {
                        delay(10000L)
                        incrementIndex()
                        state = presenceLines[indexLyrics]
                    }
                }
            }
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