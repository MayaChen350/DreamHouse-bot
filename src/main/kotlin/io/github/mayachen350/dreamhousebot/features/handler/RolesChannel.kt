package io.github.mayachen350.dreamhousebot.features.handler

import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.dreamhousebot.features.logic.RoleChannelLogic
import me.jakejmattson.discordkt.dsl.listeners

fun roleMessageListeners() = listeners {
    on<ReactionAddEvent> {
        RoleChannelLogic(this, null).execute()
    }
    on<ReactionRemoveEvent> {
        RoleChannelLogic(null, this).execute()
    }
}