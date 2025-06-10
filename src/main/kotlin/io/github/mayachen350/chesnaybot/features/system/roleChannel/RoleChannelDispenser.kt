package io.github.mayachen350.chesnaybot.features.system.roleChannel

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import dev.kord.rest.request.RestRequestException
import io.github.mayachen350.chesnaybot.backend.ValetService
import io.github.mayachen350.chesnaybot.features.event.logic.dreamhouseEmbedLogDefault
import io.github.mayachen350.chesnaybot.features.utils.ReactionEvent
import io.github.mayachen350.chesnaybot.features.utils.hasRole
import io.github.mayachen350.chesnaybot.features.utils.isInRoleChannel
import io.github.mayachen350.chesnaybot.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.jakejmattson.discordkt.util.toSnowflake
import me.jakejmattson.discordkt.util.trimToID

/** Union of the logic for the role assignment channel.**/
class RoleChannelDispenser(
    private val addEvent: ReactionAddEvent? = null,
    private val removeEvent: ReactionRemoveEvent? = null
) {

    companion object {
        /** Find the role from the message reacted and returns its snowflake.
         *
         * Return null if not found.**/
        fun findRoleFromEmoji(messageContent: String, emoji: ReactionEmoji): Snowflake? = with(messageContent) {
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
            } else null
        }
    }

    /** Will be an ReactionAddEvent or a ReactionRemoveEvent depending on the context. **/
    private val event: ReactionEvent = ReactionEvent(addEvent, removeEvent)

    /** The actual discord event listener logic. **/
    suspend fun execute() = coroutineScope {
        val message: Message? = event.getMessageOrNull()

        if (message?.isInRoleChannel() == true) {
            // Search for the role
            val roleFoundId: Snowflake? = withContext(Dispatchers.Default) {
                findRoleFromEmoji(message.content, event.emoji)
            }

            if (roleFoundId != null) {
                val member = event.getUserAsMember()

                if (member != null) {
                    try {
                        if (!event.getUser().isBot && event.getMessage().reactions
                                .filter { it.emoji == event.emoji }
                                .any { it.selfReacted }
                        )
                            message.addReaction(event.emoji)

                        if (addEvent != null && !member.hasRole(roleFoundId)) {
                            member.addRole(roleFoundId)
                            ValetService.saveRoleAdded(member.id.value.toLong(), roleFoundId.value.toLong())
                        } else if (addEvent == null && member.hasRole(roleFoundId)) {
                            event.getUserAsMember()?.removeRole(roleFoundId)
                            ValetService.saveRoleRemoved(member.id.value.toLong(), roleFoundId.value.toLong())
                        }

                        log(event.guild!!, event.getUser()) {
                            dreamhouseEmbedLogDefault(event.getUser())

                            title =
                                if (addEvent != null) "Role added via the role channel"
                                else "Role removed via the role channel"
                            description = "Role: ${event.getRole(roleFoundId).mention}"
                        }
                    } catch (e: RestRequestException) {
                        println("An error happened in the role channe: ${e.error}")
                        log(event.guild!!, event.getUser()) {
                            dreamhouseEmbedLogDefault(event.getUser())

                            title =
                                if (addEvent != null) "Couldn't add role via the role channel"
                                else "Couldn't remove Role via the role channel"
                            description = "An error happened: ${e.error?.message ?: e.message}\n" +
                                    "Role: ${event.getRole(roleFoundId).mention}"
                        }
                    }
                }
            }
        }
    }
}
