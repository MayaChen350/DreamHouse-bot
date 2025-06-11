package io.github.mayachen350.chesnaybot

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
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
import java.util.*
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

        val reactions = messages.flatMapConcat { message ->
            message.reactions.map { reaction ->
                message.getReactors(reaction.emoji).map { reactor ->
                    ReactionSimple(reaction, reactor.id, message)
                }
            }.merge()
        }.toList()

        // Remove roles that were removed
        removeRoles(ValetService.getAllSavedReactionRoles().await(), members, reactions)

        // Add roles that were added
        addRoles(ValetService.getAllSavedReactionRoles().await().toList(), members, reactions, messages)

        println("Roles updated!")
    }
}

private suspend fun removeRoles(
    rolesOfMemberQueue: Queue<RolesGivenToMemberEntity>,
    members: Flow<Member>,
    reactions: List<ReactionSimple>
) {
    val membersAffected: MutableSet<Snowflake> = mutableSetOf()

    while (rolesOfMemberQueue.isNotEmpty()) {
        val roleMember: RolesGivenToMemberEntity = rolesOfMemberQueue.remove()

        val member: Member? = members.firstOrNull { it.id.value == roleMember.userId }

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
                coroutineScope {
                    launch {
                        roleFound!!.let { member.removeRole(it) }
                        log(getGuild(), member.asUser()) {
                            dreamhouseEmbedLogDefault(member.asUser())

                            title = "Role added after restart"
                            description = "Role: ${roleFound?.let { getGuild().getRole(it).mention } ?: "null"}"
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

private suspend fun addRoles(
    rolesOfMember: List<RolesGivenToMemberEntity>,
    members: Flow<Member>,
    reactions: List<ReactionSimple>,
    messages: Flow<Message>
) = coroutineScope {
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
            if (rolesOfMember.none { it.roleId == roleFound.await().value && it.userId == member.id.value }) {
                membersAffected.add(member.id)
                launch {
                    member.addRole(roleFound.await())
                    log(getGuild(), member.asUser()) {
                        dreamhouseEmbedLogDefault(member.asUser())

                        title = "Role removed after restart"
                        description = "Role: ${getGuild().getRole(roleFound.await()).mention}"
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
        }
    }

    if (membersAffected.isNotEmpty())
        println("Added roles to ${membersAffected.count()} members!")
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