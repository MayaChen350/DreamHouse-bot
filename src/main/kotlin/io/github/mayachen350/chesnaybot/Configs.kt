package io.github.mayachen350.chesnaybot

import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class Configs(
    var roleChannelId: Long = 1373485925295722548,
    var logChannelId: Long = 1373485926084509788,
    var serverId: Long = 1373485924779819099
) : Data()

lateinit var configs: Configs