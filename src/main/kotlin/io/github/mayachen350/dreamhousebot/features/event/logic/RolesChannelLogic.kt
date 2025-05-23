package io.github.mayachen350.dreamhousebot.features.event.logic

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.dreamhousebot.configs
import io.github.mayachen350.dreamhousebot.features.utils.ReactionEvent
import io.github.mayachen350.dreamhousebot.features.utils.isInChannel
import kotlinx.coroutines.sync.Mutex
import me.jakejmattson.discordkt.util.toSnowflake
import me.jakejmattson.discordkt.util.trimToID

private val mutex = Mutex()

/** Union of the logic for the role assignment channel.**/
class RoleChannelLogic(
    private val addEvent: ReactionAddEvent? = null,
    private val removeEvent: ReactionRemoveEvent? = null
) {

    /** Will be an ReactionAddEvent or a ReactionRemoveEvent depending on the context. **/
    private val event: ReactionEvent = ReactionEvent(addEvent, removeEvent)

    /** The actual discord event listener logic. **/
    public suspend fun execute(): Unit {
        if (isMessageInRoleChannel()) {
            // Search for the role
            val roleFoundId: Snowflake? = findRole(event.getMessage().content)

            if (roleFoundId != null)
            // Give/Remove the user role based on the emoji
                toggleMemberRole(roleFoundId)

            logSmth(event.guild!!, event.getUser()) {
                dreamhouseEmbedLogDefault(event.getUser())

                title =
                    if (addEvent != null) "Role added via the role channel"
                    else "Role removed via the role channel"
                description = "Role: ${event.getRole(roleFoundId!!).mention}"
            }
        }
    }

    /** Find the role from the message reacted and returns its snowflake.
     *
     * Return null if not found.**/
    private fun findRole(messageContent: String): Snowflake? {
        with(messageContent) {
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
        event.getMessage().isInChannel(configs.roleChannelId.toSnowflake())

    /** Toggle the role in parameter depending on if the event is a ReactionAddEvent or a ReactionRemoveEvent. **/
    private suspend fun toggleMemberRole(roleId: Snowflake): Unit {
        if (addEvent != null)
            event.getUserAsMember()?.addRole(roleId)
        else
            event.getUserAsMember()?.removeRole(roleId)
    }
}