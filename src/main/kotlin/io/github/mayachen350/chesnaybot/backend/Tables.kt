package io.github.mayachen350.chesnaybot.backend

import org.jetbrains.exposed.dao.id.IntIdTable

object RolesGivenToMember : IntIdTable("rolesGivenToMember") {
    val userId = long("userId")
    val roleId = long("roleId")
}