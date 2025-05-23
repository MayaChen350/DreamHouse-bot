package io.github.mayachen350.chesnaybot.features.utils

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message

/** Check if the message is in the right channel.**/
fun Message.isInChannel(channelId: Snowflake): Boolean =
    this.channelId == channelId