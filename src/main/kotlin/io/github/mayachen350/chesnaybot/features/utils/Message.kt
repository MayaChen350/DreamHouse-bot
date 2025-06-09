package io.github.mayachen350.chesnaybot.features.utils

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import io.github.mayachen350.chesnaybot.configs
import me.jakejmattson.discordkt.util.toSnowflake

/** Check if the message is in the right channel.**/
fun Message.isInChannel(channelId: Snowflake): Boolean =
    this.channelId == channelId

/** Check if the message is in the role assignment channel.
 *
 * The role assignment channel has its id stored in configs\bot_configs.json. **/
fun Message.isInRoleChannel(): Boolean = isInChannel(configs.roleChannelId.toSnowflake())