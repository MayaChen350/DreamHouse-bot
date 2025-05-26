package io.github.mayachen350.chesnaybot.features.utils

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Toggle the role in parameter. **/
suspend fun Member.toggleRole(roleId: Snowflake): Unit {
    if (!this.hasRole(roleId))
        this.addRole(roleId)
    else
        this.removeRole(roleId)
}

fun Member.getRoleOrNull(roleId: Snowflake): Snowflake? = this.roleIds.singleOrNull { it == roleId }

suspend fun Member.hasRole(roleId: Snowflake): Boolean = withContext(Dispatchers.IO) {
    this@hasRole.roleIds.any { it == roleId }
}