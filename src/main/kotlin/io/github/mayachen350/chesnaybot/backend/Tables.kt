package io.github.mayachen350.chesnaybot.backend

import org.jetbrains.exposed.dao.id.IntIdTable

object RolesGivenToMemberTable : IntIdTable("rolesGivenToMember") {
    val userId = long("userId")
    val roleId = long("roleId")
}