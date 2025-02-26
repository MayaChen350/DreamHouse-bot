package io.github.mayachen350.dreamhousebot

import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class Configs(
    var roleChannelId: Long = 1317717883807334400,
    var logChannelId: Long = 1326237540277157908
) : Data()

lateinit var configs: Configs