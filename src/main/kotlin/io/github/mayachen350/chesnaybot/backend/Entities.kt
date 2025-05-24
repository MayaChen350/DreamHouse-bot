package io.github.mayachen350.chesnaybot.backend

import org.jetbrains.exposed.dao.ImmutableEntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID

class RolesGivenToMemberEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : ImmutableEntityClass<Int, RolesGivenToMemberEntity>(RolesGivenToMemberTable)

    val userId by RolesGivenToMemberTable.userId
    val roleId by RolesGivenToMemberTable.roleId
}