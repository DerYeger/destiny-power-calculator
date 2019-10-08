package eu.yeger.dpc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object PersistencyController {

    private val file = File("${getAppDirectory()}/Yeger/DPC", "save.json")

    init {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            write(loadDefaults())
        }
    }

    private fun write(json: String) {
        file.writeText(json, Charsets.UTF_8)
    }

    fun save(model: Model) {
        write(Gson().toJson(ModelData.fromModel(model)))
    }

    fun load() = Gson().fromJson<ModelData>(String(file.readBytes()))

    private fun loadDefaults() = String(this.javaClass.getResourceAsStream("/defaults.json").readAllBytes())

    private fun getAppDirectory() = when {
        System.getProperty("os.name").contains("Win") -> System.getenv("AppData")
        else -> File(PersistencyController::class.java.protectionDomain.codeSource.location.toURI()).parentFile.absolutePath
    }

    private inline fun <reified T> Gson.fromJson(json: String): T =
        this.fromJson<T>(json, object : TypeToken<T>() {}.type)
}

class ModelData(
    val weapons: List<SlotData>,
    val characters: List<CharacterData>
) {
    companion object {
        fun fromModel(model: Model) = ModelData(
            model.weapons.map { SlotData.fromSlot(it) },
            model.characters.map { CharacterData.fromCharacter(it) }
        )
    }
}

class CharacterData(
    val name: String,
    val armor: List<SlotData>
) {
    companion object {
        fun fromCharacter(character: Character) = CharacterData(
            character.name,
            character.armor.map { SlotData.fromSlot(it) }
        )
    }
}

class SlotData(
    val name: String,
    val power: Int
) {
    companion object {
        fun fromSlot(slot: Slot) = SlotData(
            slot.name,
            slot.power
        )
    }
}