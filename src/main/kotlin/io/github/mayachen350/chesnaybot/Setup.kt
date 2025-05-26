package io.github.mayachen350.chesnaybot

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.Reaction
import dev.kord.core.entity.channel.TextChannel
import io.github.mayachen350.chesnaybot.backend.RolesGivenToMemberTable
import io.github.mayachen350.chesnaybot.backend.ValetService
import io.github.mayachen350.chesnaybot.features.extra.BotStatusHandler
import io.github.mayachen350.chesnaybot.features.system.roleChannel.RoleChannelDispenser.Companion.findRoleFromEmoji
import io.github.mayachen350.chesnaybot.features.utils.ReactionSimple
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
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

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun updateRoles(): Unit = withContext(Dispatchers.IO) {
    with(getGuild()) {
        val messages = async {
            (getChannel(configs.roleChannelId.toSnowflake()) as TextChannel)
                .messages
        }

        val rolesOfMemberQueue = ValetService.getAllSavedReactionRoles().await()

        val rolesOfMember = rolesOfMemberQueue.toList()

        val reactions: Deferred<List<ReactionSimple>> = async {
            messages.await().run {
                val reactions = map { it.reactions.toList() }.toList().flatten()

                reactions.map { reaction: Reaction ->
                    val reactors = map { message: Message ->
                        message.getReactors(reaction.emoji)
                            .map {
                                message to it.id
                            }
                    }.flattenConcat()

                    reactors.map { ReactionSimple(reaction, it.second, it.first) }.toList()
                }.flatten()
            }
        }

        launch {
            // Remove roles that were removed
            while (rolesOfMember.any()) {
                val roleMember = rolesOfMemberQueue.poll()

                val member: Member? = members.takeIf { id.value.toLong() == roleMember.userId }?.singleOrNull()

                if (member != null) {
                    var roleFound: Snowflake? = null
                    val hasNoCorrespondingReaction: Boolean = reactions.await().none {
                        roleFound = findRoleFromEmoji(
                            it.message.content,
                            it.reaction.emoji
                        )

                        it.reactorId == member.id && roleFound == roleMember.roleId.toSnowflake()
                    }

                    if (hasNoCorrespondingReaction) {
                        launch { member.removeRole(roleFound!!) }
                        launch { ValetService.saveRoleRemoved(member.id.value.toLong(), roleMember.roleId) }
                    } else {
                        ValetService.removeAllSavedRolesFromMember(roleMember.userId)
                        rolesOfMemberQueue.removeIf { it.id.value.toLong() == roleMember.userId }
                    }
                }
            }

            // Add roles that were added
            launch {
                reactions.await().forEach { reaction ->
                    val roleFound = async(Dispatchers.Default) {
                        findRoleFromEmoji(
                            reaction.message.content,
                            reaction.reaction.emoji
                        )!!
                    }

                    val member: Member? = members.takeIf { id == reaction.reactorId }?.singleOrNull()

                    if (member != null) {
                        if (rolesOfMember.none { it.roleId.toSnowflake() == roleFound.await() }) {
                            launch { member.addRole(roleFound.await()) }
                            launch {
                                ValetService.saveRoleAdded(
                                    member.id.value.toLong(),
                                    roleFound.await().value.toLong()
                                )
                            }
                        }
                    } else {
                        ValetService.removeAllSavedRolesFromMember(reaction.reactorId.value.toLong())
                        rolesOfMemberQueue.removeIf { it.id.value.toLong() == reaction.reactorId.value.toLong() }
                    }
                }
            }
        }
    }
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