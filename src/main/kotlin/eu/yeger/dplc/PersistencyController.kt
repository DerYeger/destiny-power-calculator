package eu.yeger.dplc

import java.io.File

object PersistencyController {

    private val file = File("${System.getenv("AppData")}/Yeger/DPLC", "save.json")

    init {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            save(loadDefaults())
        }
    }

    fun save(json: String) {
        file.writeText(json, Charsets.UTF_8)
    }

    fun load() = String(file.readBytes())

    private fun loadDefaults() = String(this.javaClass.getResourceAsStream("/defaults.json").readAllBytes()).apply { println(this) }
}