package io.github.mayachen350.dreamhousebot.features.logic

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.dreamhousebot.configs

suspend fun roleChannelLogicAdd(event: ReactionAddEvent) {
    suspend fun addRole(roleId: Long) = with(
        event.getUser().asMember(event.guildId!!)
    ) {
        if (!roleIds.any { it.value.toLong() == roleId })
            addRole(Snowflake(roleId))
    }

    println(event.getMessage().content)

    if (event.getChannel().id.value.toLong() == configs.roleChannelId) event.getMessage().let {
        if (it.id.value.toLong() == 1317728843615834133) when (event.emoji.name) {
            "furina_true" -> addRole(1317660304292712476)

            "\uD83D\uDD25" -> addRole(1317660304292712472)
        }
    }
}

suspend fun roleChannelLogicRemove(event: ReactionRemoveEvent) {
    suspend fun removeRole(roleId: Long) = with(
        event.getUser().asMember(event.guildId!!)
    ) {
        if (roleIds.any { it.value.toLong() == roleId })
            removeRole(Snowflake(roleId))
    }

    println(event.getMessage().content)

    if (event.getChannel().id.value.toLong() == configs.roleChannelId) event.getMessage().let {
        if (it.id.value.toLong() == 1317728843615834133) when (event.emoji.name) {
            "furina_true" -> removeRole(1317660304292712476)

            "\uD83D\uDD25" -> removeRole(1317660304292712472)
        }
    }
}

//fun parseRoleList(): List<Pair> {
//
//}