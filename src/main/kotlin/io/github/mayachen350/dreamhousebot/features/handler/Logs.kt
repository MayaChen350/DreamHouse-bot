package io.github.mayachen350.dreamhousebot.features.handler

import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.guild.GuildAuditLogEntryCreateEvent
import dev.kord.core.event.message.MessageUpdateEvent
import io.github.mayachen350.dreamhousebot.configs
import io.github.mayachen350.dreamhousebot.features.logic.auditLogLog
import io.github.mayachen350.dreamhousebot.features.logic.logEditedMessage
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.util.toSnowflake

fun logsEventListeners() = listeners {
    on<GuildAuditLogEntryCreateEvent> {
        with(kord.getChannel(configs.logChannelId.toSnowflake())) {
            if (this != null)
                asChannelOf<MessageChannel>().auditLogLog(auditLogEntry)
            else
                println("Could not log the audit log! Log channelId undefined or with invalid id.")
        }
    }
    on<MessageUpdateEvent> {
        if (this.getMessage().author?.isBot == false)
            logEditedMessage(this)
    }
//    on<MessageDeleteEvent> {
//        this.guild?.let {
//            if (this.message?.author?.isBot == false)
//                logDeletedMessage(this)
//        }
//    }
}