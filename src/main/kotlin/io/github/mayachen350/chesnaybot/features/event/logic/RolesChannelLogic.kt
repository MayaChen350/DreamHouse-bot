package io.github.mayachen350.chesnaybot.features.event.logic

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Emoji
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.chesnaybot.configs
import io.github.mayachen350.chesnaybot.features.system.roleChannel.findRoleFromEmoji
import io.github.mayachen350.chesnaybot.features.utils.ReactionEvent
import io.github.mayachen350.chesnaybot.features.utils.isInChannel
import kotlinx.coroutines.sync.Mutex
import me.jakejmattson.discordkt.util.toSnowflake

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
            val roleFoundId: Snowflake? = findRoleFromEmoji(event.getMessage().content, event.emoji as Emoji)

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