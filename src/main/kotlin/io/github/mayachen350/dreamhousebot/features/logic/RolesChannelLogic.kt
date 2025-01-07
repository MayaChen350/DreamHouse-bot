package io.github.mayachen350.dreamhousebot.features.logic

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.dreamhousebot.configs
import me.jakejmattson.discordkt.util.toSnowflake
import me.jakejmattson.discordkt.util.trimToID

/** Union of the logic for the role assignment channel.**/
class RoleChannelLogic(private val addEvent: ReactionAddEvent?, private val removeEvent: ReactionRemoveEvent?) {

    /** Abstraction class making using both type of events easier by sharing common properties and methods. **/
    private inner class ReactionEvent(addEvent: ReactionAddEvent?, removeEvent: ReactionRemoveEvent?) {
        val emoji = addEvent?.emoji ?: removeEvent!!.emoji
        suspend fun getMessage() = addEvent?.getMessage() ?: removeEvent!!.getMessage()
        suspend fun getRole(id: Snowflake) =
            addEvent?.getGuildOrNull()?.getRole(id) ?: removeEvent!!.getGuildOrNull()!!.getRole(id)
    }

    /** Will be an ReactionAddEvent or a ReactionRemoveEvent depending on the context. **/
    private val event: ReactionEvent = ReactionEvent(addEvent, removeEvent)

    /** The actual discord event listener logic. **/
    public suspend fun execute(): Unit {
        if (isMessageInRoleChannel()) {
            // Search for the role
            val roleFoundId: Snowflake? = findRole()

            // Give/Remove the user role based on the emoji
            if (roleFoundId != null)
                toggleMemberRole(roleFoundId);
        }
    }

    /** Find the role from the message reacted and returns its snowflake.
     *
     * Return null if not found.**/
    private suspend fun findRole(): Snowflake? {
        with(event.getMessage().content) {
            // Search if the name of the emoji appears on the message
            if (indexOf(event.emoji.mention) != -1) {
                // Cut the message from when it finds the emoji (also removes '<' if it has)
                val firstCut: String = substring(indexOf(event.emoji.mention) + 1 /* possible '<' char*/)

                // Get the role id from the first or second '<' character found
                val roleId: Snowflake = firstCut.run {
                    val roleMentionStartIndex = indexOf("<")
                    val nbCharsRoleMention = 23

                    substring(roleMentionStartIndex, roleMentionStartIndex + nbCharsRoleMention)
                        .trimToID()
                        .toSnowflake()
                }

                return roleId
            } else return null;
        }
    }

    /** Check if the message is in the role assignment channel.
     *
     * The role assignment channel has its id stored in configs\bot_configs.json. **/
    private suspend fun isMessageInRoleChannel(): Boolean =
        event.getMessage().channelId == configs.roleChannelId.toSnowflake()

    /** Toggle the role in parameter depending on if the event is a ReactionAddEvent or a ReactionRemoveEvent. **/
    private suspend fun toggleMemberRole(roleId: Snowflake): Unit {
        if (addEvent != null)
            addEvent.getUserAsMember()?.addRole(roleId)
        else
            removeEvent!!.getUserAsMember()?.removeRole(roleId)
    }
}