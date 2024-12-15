package io.github.mayachen350.dreamhousebot.features.logic

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.message.ReactionAddEvent

suspend fun roleChannelLogic(event: ReactionAddEvent) {
    suspend fun addRole(roleId: Long) = event.getUser().asMember(event.guildId!!).addRole(Snowflake(roleId))
    println("just saying this works")

    event.getMessage().let {
        if (it.id.value.toLong() == 1317728843615834133)
            when (event.emoji.name) {
                "furina_true" -> addRole(1317660304292712476)

                "\uD83D\uDD25" -> addRole(1317660304292712472)
            }
    }
}