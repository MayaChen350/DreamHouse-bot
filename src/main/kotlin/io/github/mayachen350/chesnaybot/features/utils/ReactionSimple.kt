package io.github.mayachen350.chesnaybot.features.utils

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import dev.kord.core.entity.Reaction

data class ReactionSimple(val reaction: Reaction, val reactorId: Snowflake, val message: Message)
