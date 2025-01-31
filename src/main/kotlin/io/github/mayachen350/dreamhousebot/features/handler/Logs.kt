package io.github.mayachen350.dreamhousebot.features.handler

//fun logsEventListener() = listeners {
//    on<GuildAuditLogEntryCreateEvent> {
//        with(kord.getChannel(configs.logChannelId.toSnowflake())) {
//            if (this != null)
//                asChannelOf<MessageChannel>().run {
//                    auditLogLog(auditLogEntry)
//                }
//            else
//                println("Could not log the audit log! Log channelId undefined or with invalid id.")
//        }
//    }
//}