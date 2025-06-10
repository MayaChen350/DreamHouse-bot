package io.github.mayachen350.chesnaybot

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.TextChannel
import io.github.mayachen350.chesnaybot.backend.RolesGivenToMemberEntity
import io.github.mayachen350.chesnaybot.backend.RolesGivenToMemberTable
import io.github.mayachen350.chesnaybot.backend.ValetService
import io.github.mayachen350.chesnaybot.features.event.logic.dreamhouseEmbedLogDefault
import io.github.mayachen350.chesnaybot.features.extra.BotStatusHandler
import io.github.mayachen350.chesnaybot.features.system.roleChannel.RoleChannelDispenser.Companion.findRoleFromEmoji
import io.github.mayachen350.chesnaybot.features.utils.ReactionSimple
import io.github.mayachen350.chesnaybot.utils.toSnowflake
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.jakejmattson.discordkt.util.toSnowflake
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists

/*Run all the setup and onStart steps*/
suspend fun setup(ctx: Kord) = coroutineScope {
    getGuild = { ctx.getGuild(configs.serverId.toSnowflake()) }

    setupDatabase()

    launch(Dispatchers.IO) { updateRoles() }
    launch(Dispatchers.IO) { BotStatusHandler.configure() }
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun updateRoles(): Unit = coroutineScope {
    println("Updating roles!")
    with(getGuild()) {
        val messages = getChannel(configs.roleChannelId.toSnowflake()).asChannelOf<TextChannel>().messages

        val rolesOfMemberQueue = ValetService.getAllSavedReactionRoles().await()

        val rolesOfMember = rolesOfMemberQueue.toList()

        val reactions = messages.flatMapConcat { message ->
            message.reactions.map { reaction ->
                message.getReactors(reaction.emoji).map { reactor ->
                    ReactionSimple(reaction, reactor.id, message)
                }
            }.merge().filter { this.kord.selfId != it.reactorId }
        }.toList()

        // Remove roles that were removed
        launch {
            val membersAffected: MutableSet<Snowflake> = mutableSetOf()

            while (rolesOfMemberQueue.isNotEmpty()) {
                val roleMember: RolesGivenToMemberEntity = rolesOfMemberQueue.remove()

                val member: Member? = members.firstOrNull() { it.id.value == roleMember.userId }

                if (member != null) {
                    var roleFound: Snowflake? = null
                    val hasNoCorrespondingReaction: Boolean = reactions.none {
                        roleFound = withContext(Dispatchers.Default) {
                            findRoleFromEmoji(
                                it.message.content,
                                it.reaction.emoji
                            )
                        }

                        it.reactorId == member.id && roleFound == roleMember.roleId.toSnowflake()
                    }

                    if (hasNoCorrespondingReaction) {
                        membersAffected.add(member.id)
                        withContext(Dispatchers.IO) {
                            launch {
                                roleFound?.let { member.removeRole(it) }
                                log(this@with, member.asUser()) {
                                    dreamhouseEmbedLogDefault(member.asUser())

                                    title = "Role added after restart"
                                    description = "Role: ${roleFound?.let { getRole(it).mention } ?: "null"}"
                                }
                            }
                            launch { ValetService.saveRoleRemoved(member.id.value, roleMember.roleId) }
                        }
                    }
                } else {
                    ValetService.removeAllSavedRolesFromMember(roleMember.userId)
                    rolesOfMemberQueue.removeIf { it.userId == roleMember.userId }
                }
            }

            if (membersAffected.isNotEmpty())
                println("Removed roles to ${membersAffected.count()} members!")
        }

        // Add roles that were added
        launch {
            val membersAffected: MutableSet<Snowflake> = mutableSetOf()

            reactions.forEach { reaction ->
                val roleFound = async(Dispatchers.Default) {
                    findRoleFromEmoji(
                        reaction.message.content,
                        reaction.reaction.emoji
                    )!!
                }

                val member: Member? = members.firstOrNull { it.id == reaction.reactorId }

                if (member != null) {
                    if (rolesOfMember.none { it.roleId.toSnowflake() == roleFound.await() }) {
                        membersAffected.add(member.id)
                        launch {
                            member.addRole(roleFound.await())
                            log(this@with, member.asUser()) {
                                dreamhouseEmbedLogDefault(member.asUser())

                                title = "Role removed after restart"
                                description = "Role: ${getRole(roleFound.await()).mention}"
                            }
                        }
                        launch {
                            ValetService.saveRoleAdded(
                                member.id.value,
                                roleFound.await().value
                            )
                        }
                    }
                } else {
                    messages.collect {
                        it.deleteReaction(reaction.reactorId, reaction.reaction.emoji)
                    }
                    ValetService.removeAllSavedRolesFromMember(reaction.reactorId.value)
                    rolesOfMemberQueue.removeIf { it.id.value.toLong() == reaction.reactorId.value.toLong() }
                }
            }

            if (membersAffected.isNotEmpty())
                println("Added roles to ${membersAffected.count()} members!")
        }
    }
    println("Roles updated!")
}

private fun setupDatabase() {
    println("Setting up database!")
    Path("data/").run {
        if (notExists())
            createDirectory()
    }

    transaction(ValetService.db) {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(RolesGivenToMemberTable)
    }

    println("Database set and connection successful!")
}