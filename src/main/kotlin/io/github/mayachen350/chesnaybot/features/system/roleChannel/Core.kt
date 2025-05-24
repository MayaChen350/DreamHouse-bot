package io.github.mayachen350.chesnaybot.features.system.roleChannel

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Emoji
import dev.kord.core.entity.ReactionEmoji
import me.jakejmattson.discordkt.util.toSnowflake
import me.jakejmattson.discordkt.util.trimToID

/** Find the role from the message reacted and returns its snowflake.
 *
 * Return null if not found.**/
fun findRoleFromEmoji(messageContent: String, emoji: Emoji): Snowflake? {
    with(messageContent) {
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

            return roleId
        } else return null;
    }
}