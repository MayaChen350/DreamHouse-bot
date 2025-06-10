package io.github.mayachen350.chesnaybot.features.event.handler

import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.chesnaybot.features.system.roleChannel.RoleChannelDispenser
import kotlinx.coroutines.*
import me.jakejmattson.discordkt.dsl.listeners

@ExperimentalCoroutinesApi
fun roleMessageListeners() = listeners {
    on<ReactionAddEvent> {
        if (!this.getUser().isBot)
            withContext(roleChannelThreadContext) {
                RoleChannelDispenser(this@on, null).execute()
            }
    }
    on<ReactionRemoveEvent> {
        if (!this.getUser().isBot)
            withContext(roleChannelThreadContext) {
                RoleChannelDispenser(null, this@on).execute()
            }
    }
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val roleChannelThreadContext = newSingleThreadContext("roleChannelContext")