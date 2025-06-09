package io.github.mayachen350.chesnaybot.features.extra

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.jakejmattson.discordkt.Discord
import kotlin.io.path.Path
import kotlin.io.path.readText

object BotStatusHandler {
    lateinit var statusBehavior: StatusBehavior

    fun configure() {
        println("Configuring bot status!")
        statusBehavior.configure()
        println("Bot status configured!")
    }
}

sealed class StatusBehavior {
    abstract suspend fun changeStatus(ctx: Discord)
    open fun configure() {}

    data object Static : StatusBehavior() {
        override suspend fun changeStatus(ctx: Discord) {
            ctx.kord.editPresence { state = "Real" }
        }
    }

    data object Singer : StatusBehavior() {
        private lateinit var presenceLines: List<String>
        private var indexLyrics: Int = 0

        private fun incrementIndex() {
            if (indexLyrics < presenceLines.size - 1) indexLyrics++ else indexLyrics = 0
        }

        override suspend fun changeStatus(ctx: Discord) {
            println("STARTING BOT STATUS LOOP")
            while (true) {
                ctx.kord.editPresence {
                    delay(10000L)
                    incrementIndex()
                    state = presenceLines[indexLyrics]
                }
            }
        }

        override fun configure() {
            super.configure()
            presenceLines = Path("src/main/resources/raw/lyrics.txt").readText().lines()
        }
    }
}