package eu.yeger.dplc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.yeger.kotlin.javafx.delegation
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class Slot(data: Pair<String, Int>) {
    val name = data.first

    val powerProperty = SimpleIntegerProperty(data.second)
    var power by powerProperty.delegation()

    val stateProperty = SimpleStringProperty(null)
    var state: String? by stateProperty.delegation()
}

class Model {

    private val gson = Gson()

    val slots: List<Slot>
    val weapons: List<Slot>
    val armor: List<Slot>

    val powerLevelProperty = SimpleIntegerProperty()
    var powerLevel by powerLevelProperty.delegation()

    val missingPowerProperty = SimpleIntegerProperty()
    var missingPower by missingPowerProperty.delegation()

    val infoProperty = SimpleStringProperty(null)
    var info: String? by infoProperty.delegation()

    init {
        slots = gson.fromJson<List<Pair<String, Int>>>(PersistencyController.load()).map { Slot(it) }
        weapons = slots.subList(0, 3)
        armor = slots.subList(3, slots.size)
    }

    fun save() {
        val json = gson.toJson(slots.map { it.name to it.power })
        PersistencyController.save(json)
    }
}

inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
