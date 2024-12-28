package io.github.mayachen350.dreamhousebot.features.handler

import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.dreamhousebot.features.logic.roleChannelLogicAdd
import io.github.mayachen350.dreamhousebot.features.logic.roleChannelLogicRemove
import me.jakejmattson.discordkt.dsl.listeners

fun roleMessageListeners() = listeners {
    on<ReactionAddEvent> {
        roleChannelLogicAdd(this)
    }
    on<ReactionRemoveEvent> {
        roleChannelLogicRemove(this)
    }
}