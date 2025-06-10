package io.github.mayachen350.chesnaybot.backend

import io.github.mayachen350.chesnaybot.backend.RolesGivenToMemberTable.roleId
import io.github.mayachen350.chesnaybot.backend.RolesGivenToMemberTable.userId
import io.github.mayachen350.chesnaybot.resources.DebugProdStrings
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


object ValetService {
    val db by lazy {
        Database.connect(
            DebugProdStrings.DB_CONNECTION,
            "com.mysql.cj.jdbc.Driver",
            "Maya",
            "Furina"
        )
    }

    suspend fun getAllSavedReactionRoles(): Deferred<Queue<RolesGivenToMemberEntity>> =
        suspendedTransactionAsync(db = db) {
            LinkedList(RolesGivenToMemberEntity.all().toList())
        }

    suspend fun hasAddedUserRole(pUserId: ULong, pRoleId: ULong): Deferred<Boolean> = suspendedTransactionAsync(db = db) {
        RolesGivenToMemberTable.selectAll()
            .where((userId eq pUserId) and (roleId eq pRoleId))
            .any()
    }

    suspend fun saveRoleAdded(pUserId: ULong, pRoleId: ULong): Unit = withContext(Dispatchers.IO) {
        transaction(db) {
            RolesGivenToMemberTable.insert {
                it[userId] = pUserId
                it[roleId] = pRoleId
            }
        }
    }

    suspend fun saveRoleRemoved(pUserId: ULong, pRoleId: ULong): Unit = withContext(Dispatchers.IO) {
        transaction(db) {
            RolesGivenToMemberTable.deleteWhere {
                (this.userId eq pUserId) and (this.roleId eq pRoleId)
            }
        }
    }

    suspend fun removeAllSavedRolesFromMember(pUserId: ULong): Unit = withContext(Dispatchers.IO) {
        transaction(db) {
            RolesGivenToMemberTable.deleteWhere {
                this.userId eq pUserId
            }
        }
    }
}