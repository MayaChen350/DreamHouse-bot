package io.github.mayachen350.chesnaybot

import MigrationUtils
import io.github.mayachen350.chesnaybot.backend.RolesGivenToMember
import io.github.mayachen350.chesnaybot.features.extra.BotStatusHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists

/*Run all the setup and onStart steps*/
fun setup() = runBlocking {
    setupDatabase()

    launch(Dispatchers.IO) { updateRoles() }
    launch(Dispatchers.IO) { BotStatusHandler.configure() }
}

private fun updateRoles() {

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

        SchemaUtils.create(RolesGivenToMember)
    }
}