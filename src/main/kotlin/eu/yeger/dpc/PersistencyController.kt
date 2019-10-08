package eu.yeger.dpc

import java.io.File

object PersistencyController {

    private val file = File("${getAppDirectory()}/Yeger/DPC", "save.json")

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

    private fun loadDefaults() = String(this.javaClass.getResourceAsStream("/defaults.json").readAllBytes())

    private fun getAppDirectory() = when {
        System.getProperty("os.name").contains("Win") -> System.getenv("AppData")
        else -> File(PersistencyController::class.java.protectionDomain.codeSource.location.toURI()).parentFile.absolutePath
    }
}