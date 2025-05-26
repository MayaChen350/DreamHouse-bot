package io.github.mayachen350.chesnaybot.features.event.handler

import dev.kord.core.event.message.MessageUpdateEvent
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.chesnaybot.configs
import io.github.mayachen350.chesnaybot.features.system.roleChannel.RoleChannelDispenser
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.util.toSnowflake

fun roleMessageListeners() = listeners {
    on<ReactionAddEvent> {
        RoleChannelDispenser(this, null).execute()
    }
    on<ReactionRemoveEvent> {
        RoleChannelDispenser(null, this).execute()
    }
}