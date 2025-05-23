package io.github.mayachen350.chesnaybot.features.utils

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member

/** Toggle the role in parameter. **/
suspend fun Member.toggleRole(roleId: Snowflake): Unit {
    if (!this.hasRole(roleId))
        this.addRole(roleId)
    else
        this.removeRole(roleId)
}

fun Member.getRoleOrNull(roleId: Snowflake): Snowflake? = this.roleIds.singleOrNull { it == roleId }

fun Member.hasRole(roleId: Snowflake): Boolean = this.roleIds.any { it == roleId }