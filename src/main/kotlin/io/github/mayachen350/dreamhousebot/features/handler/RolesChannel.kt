package io.github.mayachen350.dreamhousebot.features.handler

import dev.kord.core.event.message.ReactionAddEvent
import io.github.mayachen350.dreamhousebot.features.logic.roleChannelLogic
import me.jakejmattson.discordkt.dsl.listeners

fun roleMessageListeners() = listeners {
    on<ReactionAddEvent> {
        roleChannelLogic(this)
    }
}