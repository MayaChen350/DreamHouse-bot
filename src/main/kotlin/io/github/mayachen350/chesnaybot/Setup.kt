package io.github.mayachen350.chesnaybot

import dev.kord.core.entity.Member
import io.github.mayachen350.chesnaybot.backend.RolesGivenToMemberTable
import io.github.mayachen350.chesnaybot.backend.ValetService
import io.github.mayachen350.chesnaybot.features.extra.BotStatusHandler
import io.github.mayachen350.chesnaybot.features.utils.hasRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.util.toSnowflake
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists

/*Run all the setup and onStart steps*/
fun setup(ctx: Discord) = runBlocking {
    getGuild = { ctx.kord.getGuild(configs.serverId.toSnowflake()) }

    setupDatabase()

    launch(Dispatchers.IO) { updateRoles() }
    launch(Dispatchers.IO) { BotStatusHandler.configure() }
}

private suspend fun updateRoles() = with(getGuild()) {
    val rolesOfMember = ValetService.getAllSavedReactionRoles().await()

    // Remove roles that were removed
    while (rolesOfMember.any()) {
        val roleMember = rolesOfMember.poll()

        val member: Member? = members.takeIf { id.value.toLong() == roleMember.userId }?.singleOrNull()

        if (member != null) {
            if (!member.hasRole(roleMember.roleId.toSnowflake()))
                ValetService.saveRoleRemoved(member.id.value.toLong(), roleMember.roleId)
        } else {
            ValetService.removeAllSavedRolesFromMember(roleMember.userId)
            rolesOfMember.removeIf { it.id.value.toLong() == roleMember.userId }
        }
    }

    // Add roles that were added
}

private fun setupDatabase() {
    Path("data/").run {
        if (notExists())
            createDirectory()
    }

    Database.connect(
        "jdbc:mysql://192.168.0.188:3306/SalonChesnay",
        "com.mysql.cj.jdbc.Driver",
        "Maya",
        "Furina"
    )

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(RolesGivenToMemberTable)
    }
}