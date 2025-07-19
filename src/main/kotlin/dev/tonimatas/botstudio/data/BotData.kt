package dev.tonimatas.botstudio.data

import java.io.File
import java.util.*

object BotData {
    val propertiesFile = File(dataFolder(), "data.properties")
    var properties: Properties? = null

    fun runProperties() {
        properties = Properties()

        if (!propertiesFile.exists()) {
            propertiesFile.createNewFile()

            properties!!.setProperty("ticketNumber", "0")
            save()
        }

        properties!!.load(propertiesFile.inputStream())
        save()
    }

    fun increaseTicketNumber(): Int {
        val ticketNumber = properties!!.getProperty("ticketNumber", "0")!!.toInt()
        val increased = ticketNumber + 1
        properties!!.setProperty("ticketNumber", "$increased")
        save()
        return increased
    }

    fun dataFolder(): File {
        val dataFolder = File("data")

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        return dataFolder
    }

    fun save() {
        properties!!.store(propertiesFile.outputStream(), "BotStudio Data")
    }
}