package io.github.mayachen350.chesnaybot.features.event.handler

import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.chesnaybot.features.system.roleChannel.RoleChannelDispenser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import me.jakejmattson.discordkt.dsl.listeners

fun roleMessageListeners() = listeners {
    on<ReactionAddEvent> {
        if (!this.getUser().isBot)
            withContext(roleChannelThreadContext) {
                eventQueue.send(RoleChannelDispenser(this@on, null))
            }
    }
    on<ReactionRemoveEvent> {
        if (!this.getUser().isBot)
            withContext(roleChannelThreadContext) {
                eventQueue.send(RoleChannelDispenser(null, this@on))
            }
    }
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val roleChannelThreadContext = newSingleThreadContext("roleChannelContext")

private val eventQueue = Channel<RoleChannelDispenser>()

suspend fun startLoop() = coroutineScope {
    println("STARTING ROLE LOOP")
    while (true) {
        val element = eventQueue.tryReceive().getOrNull() ?: break
        withContext(roleChannelThreadContext) {
            element.execute()
        }
    }
}