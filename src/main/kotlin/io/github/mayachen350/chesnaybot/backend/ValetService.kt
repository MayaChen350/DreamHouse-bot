package io.github.mayachen350.chesnaybot.backend

import io.github.mayachen350.chesnaybot.backend.RolesGivenToMemberTable.roleId
import io.github.mayachen350.chesnaybot.backend.RolesGivenToMemberTable.userId
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


object ValetService {
    suspend fun getAllSavedReactionRoles(): Deferred<Queue<RolesGivenToMemberEntity>> = suspendedTransactionAsync {
        LinkedList(RolesGivenToMemberEntity.all().toList())
    }

    suspend fun hasAddedUserRole(pUserId: Long, pRoleId: Long): Deferred<Boolean> = suspendedTransactionAsync {
        RolesGivenToMemberTable.selectAll()
            .where((userId eq pUserId) and (roleId eq pRoleId))
            .any()
    }

    suspend fun saveRoleAdded(pUserId: Long, pRoleId: Long): Unit = withContext(Dispatchers.IO) {
        transaction {
            RolesGivenToMemberTable.insert {
                it[userId] = pUserId
                it[roleId] = pRoleId
            }
        }
    }

    suspend fun saveRoleRemoved(pUserId: Long, pRoleId: Long): Unit = withContext(Dispatchers.IO) {
        transaction {
            RolesGivenToMemberTable.deleteWhere {
                (this.userId eq pUserId) and (this.roleId eq pRoleId)
            }
        }
    }

    suspend fun removeAllSavedRolesFromMember(pUserId: Long): Unit = withContext(Dispatchers.IO) {
        transaction {
            RolesGivenToMemberTable.deleteWhere {
                this.userId eq pUserId
            }
        }
    }
}