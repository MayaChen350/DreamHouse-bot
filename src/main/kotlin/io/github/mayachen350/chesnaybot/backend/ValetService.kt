package io.github.mayachen350.chesnaybot.backend

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert


object ValetService {
    suspend fun saveRoleAdded(pUserId: Long, pRoleId: Long) = withContext(Dispatchers.IO) {
        RolesGivenToMember.insert {
            it[userId] = pUserId
            it[roleId] = pRoleId
        }
    }

    suspend fun saveRoleRemoved(pUserId: Long, pRoleId: Long) = withContext(Dispatchers.IO) {
        RolesGivenToMember.deleteWhere {
            (this.userId eq pUserId) and (this.roleId eq pRoleId)
        }
    }
}