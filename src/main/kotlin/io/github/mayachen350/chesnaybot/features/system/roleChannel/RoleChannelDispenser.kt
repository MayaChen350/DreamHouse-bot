package io.github.mayachen350.chesnaybot.features.system.roleChannel

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import io.github.mayachen350.chesnaybot.configs
import io.github.mayachen350.chesnaybot.features.event.logic.dreamhouseEmbedLogDefault
import io.github.mayachen350.chesnaybot.features.event.logic.logSmth
import io.github.mayachen350.chesnaybot.features.utils.ReactionEvent
import io.github.mayachen350.chesnaybot.features.utils.hasRole
import io.github.mayachen350.chesnaybot.features.utils.isInChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import me.jakejmattson.discordkt.util.toSnowflake
import me.jakejmattson.discordkt.util.trimToID

private val mutex = Mutex()

/** Union of the logic for the role assignment channel.**/
class RoleChannelDispenser(
    private val addEvent: ReactionAddEvent? = null,
    private val removeEvent: ReactionRemoveEvent? = null
) {

    companion object {
        /** Find the role from the message reacted and returns its snowflake.
         *
         * Return null if not found.**/
        suspend fun findRoleFromEmoji(messageContent: String, emoji: ReactionEmoji): Snowflake? = with(messageContent) {
            withContext(Dispatchers.Default) {
                // Search if the name of the emoji appears on the message
                if (indexOf(emoji.mention) != -1) {
                    // Cut the message from when it finds the emoji (also removes '<' if it has)
                    val firstCut: String = substring(indexOf(emoji.mention) + 1 /* possible '<' char*/)

                    // Get the role id from the first or second '<' character found
                    val roleId: Snowflake = firstCut.run {
                        val roleMentionStartIndex = indexOf("<")
                        val nbCharsRoleMention = 23

                        substring(roleMentionStartIndex, roleMentionStartIndex + nbCharsRoleMention)
                            .trimToID()
                            .toSnowflake()
                    }

                    roleId
                } else null;
            }
        }
    }

    /** Will be an ReactionAddEvent or a ReactionRemoveEvent depending on the context. **/
    private val event: ReactionEvent = ReactionEvent(addEvent, removeEvent)

    /** The actual discord event listener logic. **/
    suspend fun execute(): Unit {
        val message: Message = event.getMessage()

        if (message.isInRoleChannel()) {
            // Search for the role
            val roleFoundId: Snowflake? = findRoleFromEmoji(message.content, event.emoji)

            if (roleFoundId != null) {
                // Give/Remove the user role based on the emoji
                event.getUserAsMember()?.toggleRole(roleFoundId)

                if (addEvent != null && !event.getUser().isBot)
                    message.addReaction(event.emoji)

                logSmth(event.guild!!, event.getUser()) {
                    dreamhouseEmbedLogDefault(event.getUser())

                    title =
                        if (addEvent != null) "Role added via the role channel"
                        else "Role removed via the role channel"
                    description = "Role: ${event.getRole(roleFoundId!!).mention}"
                }
            }
        }
    }

    /** Check if the message is in the role assignment channel.
     *
     * The role assignment channel has its id stored in configs\bot_configs.json. **/
    private suspend fun Message.isInRoleChannel(): Boolean =
        event.getMessage().isInChannel(configs.roleChannelId.toSnowflake())

    /** Toggle the role in parameter depending on if the event is a ReactionAddEvent or a ReactionRemoveEvent. **/
    private suspend fun Member.toggleRole(roleId: Snowflake) {
        if (addEvent != null) {
            if (!this.hasRole(roleId))
                this.addRole(roleId)
        } else if (!this.hasRole(roleId))
            event.getUserAsMember()?.removeRole(roleId)
    }
}