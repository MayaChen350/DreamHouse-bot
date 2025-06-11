package io.github.mayachen350.chesnaybot.features.event.handler

import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.chesnaybot.features.system.roleChannel.RoleChannelDispenser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.jakejmattson.discordkt.dsl.listeners

fun roleMessageListeners() = listeners {
    on<ReactionAddEvent> {
        kord.launch(roleChannelThreadContext) {
            if (!this@on.getUser().isBot)
                RoleChannelDispenser(this@on, null).execute()
        }
    }
    on<ReactionRemoveEvent> {
        kord.launch(roleChannelThreadContext) {
            if (!this@on.getUser().isBot)
                RoleChannelDispenser(null, this@on).execute()
        }
    }
}

private val roleChannelThreadContext = Dispatchers.Default.limitedParallelism(1)