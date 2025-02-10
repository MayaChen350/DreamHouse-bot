package io.github.mayachen350.dreamhousebot

import java.io.FileInputStream
import java.util.*

object Resources {
    object Bot {
        /**Load the property from the resource.
         * @param prop Property to be loaded.  **/
        fun load(prop: String): String {
            return loadPropertiesFromFile("src/main/resources/bot.properties", prop)
        }
    }

    enum class Logs {
        TITLE {
            override fun load(prop: String): String {
                return loadPropertiesFromFile("src/main/resources/logs/title.properties", prop)
            }
        };

        /**Load the property from the resource.
         * @param prop Property to be loaded.  **/
        abstract fun load(prop: String): String
    }

    enum class BotMessages {
        MODERATION {
            override fun load(prop: String): String {
                return loadPropertiesFromFile("src/main/resources/botMessages/moderation.properties", prop)
            }
        };

        /**Load the property from the resource.
         * @param prop Property to be loaded.  **/
        abstract fun load(prop: String): String
    }

    /**Load the property from the file specified. **/
    private fun loadPropertiesFromFile(filePath: String, property: String): String {
        val p = Properties().run {
            load(FileInputStream(filePath))
            getProperty(property)
        }

        if (p == null)
            throw IllegalArgumentException("The resource: $property was not found in the properties file chosen.")
        else
            return p
    }
}