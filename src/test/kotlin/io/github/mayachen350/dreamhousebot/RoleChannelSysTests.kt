package io.github.mayachen350.dreamhousebot

import dev.kord.common.entity.optional.Optional
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User
import io.github.cdimascio.dotenv.Dotenv
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.async
import me.jakejmattson.discordkt.util.toSnowflake

class RoleChannelSysTests : FunSpec ({
    kotlinx.coroutines.runBlocking {
        val user = async {
            User(
                data = UserData(
                    id = 1193018015445430324.toSnowflake(),
                    username = 0x009a2c23.toString(),
                    discriminator = Optional(0.toString()),
                    globalName = Optional("MayChen"),
                    avatar = "ba1cacd2e77ba8efcba25915ecb66dfe",
                ),
                kord = Kord(Dotenv.load().get("BOT_TOKEN"))
            )
        }

        context("An user chooses a correct reaction in the role channel") {
            test("Is the correct role given?") {

            }
        }
    }
})