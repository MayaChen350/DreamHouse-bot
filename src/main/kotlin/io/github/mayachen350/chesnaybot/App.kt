package io.github.mayachen350.chesnaybot

import dev.kord.cache.map.MapLikeCollection
import dev.kord.cache.map.internal.MapEntryCache
import dev.kord.cache.map.lruLinkedHashMap
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.gateway.ALL
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.cdimascio.dotenv.Dotenv
import io.github.mayachen350.chesnaybot.features.command.handler.moderationCommands
import io.github.mayachen350.chesnaybot.features.event.handler.logsEventListeners
import io.github.mayachen350.chesnaybot.features.event.handler.roleMessageListeners
import io.github.mayachen350.chesnaybot.features.event.logic.dreamhouseEmbedLogDefault
import io.github.mayachen350.chesnaybot.features.extra.BotStatusHandler.statusBehavior
import io.github.mayachen350.chesnaybot.features.extra.StatusBehavior
import kotlinx.coroutines.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.locale.Language
import me.jakejmattson.discordkt.util.toSnowflake

lateinit var getGuild: suspend () -> Guild

@OptIn(PrivilegedIntent::class)
fun main(): Unit = runBlocking {
    val token = Dotenv.load().get("BOT_TOKEN")

    println("BOT TOKEN OBTAINED")

    statusBehavior = StatusBehavior.Static

    println("BOT STATUS SET TO $statusBehavior")


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

            println("BOT SETUP ENDED")

            this@runBlocking.launch(Dispatchers.Default) {
                statusBehavior.changeStatus(this@onStart)
            }

            println("BOT STATUS LOOP STARTED")
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

/** Logs an effect in the log channel. This is the base function for log functions.
 *
 *  @param guild The discord guild (the server) parameter required to find the logs channel where to send the logs.
 *  @param displayedUser The user we're going to have information displayed of. Default to the one associated to the interaction.
 *  @param embedExtra Extra embed things to add for functions or anonymous functions.**/
@OptIn(DelicateCoroutinesApi::class)
fun log(
    guild: GuildBehavior,
    displayedUser: User?,
    embedExtra: suspend EmbedBuilder.() -> Unit = { },
) {
    GlobalScope.launch(Dispatchers.IO) {
        val channel: GuildChannel? = guild.getChannelOrNull(configs.logChannelId.toSnowflake())
        if (channel != null) {
            channel.asChannelOf<MessageChannel>()
                .createEmbed {
                    dreamhouseEmbedLogDefault(displayedUser)
                    embedExtra()
                }
        } else
            println("Could not log the command log! Log channelId undefined or set to an invalid id.")
    }.start()
}