package io.github.mayachen350.chesnaybot.features.event.handler

import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.chesnaybot.features.system.roleChannel.RoleChannelDispenser
import me.jakejmattson.discordkt.dsl.listeners

fun roleMessageListeners() = listeners {
    on<ReactionAddEvent> {
        if (!this.getUser().isBot)
            RoleChannelDispenser(this, null).execute()
    }
    on<ReactionRemoveEvent> {
        if (!this.getUser().isBot)
            RoleChannelDispenser(null, this).execute()
    }
}