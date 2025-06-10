package io.github.mayachen350.chesnaybot.features.utils

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent

/** Abstraction class making using both type of events easier by sharing common properties and methods. **/
class ReactionEvent(
    private val addEvent: ReactionAddEvent? = null,
    private val removeEvent: ReactionRemoveEvent? = null
) {
    val emoji = addEvent?.emoji ?: removeEvent!!.emoji
    val guild = addEvent?.guild ?: removeEvent!!.guild
    suspend fun getMessage() = addEvent?.getMessage() ?: removeEvent!!.getMessage()
    suspend fun getMessageOrNull() = addEvent?.getMessageOrNull() ?: removeEvent!!.getMessageOrNull()
    suspend fun getUserAsMember() = addEvent?.getUserAsMember() ?: removeEvent!!.getUserAsMember()
    suspend fun getUser() = addEvent?.getUser() ?: removeEvent!!.getUser()
    suspend fun getRole(id: Snowflake) =
        addEvent?.getGuildOrNull()?.getRole(id) ?: removeEvent!!.getGuildOrNull()!!.getRole(id)
}