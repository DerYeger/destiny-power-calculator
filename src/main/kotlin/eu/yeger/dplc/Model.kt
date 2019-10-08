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

class Class(val armor: List<Slot>, weapons: List<Slot>) {
    val slots: List<Slot>

    val powerLevelProperty = SimpleIntegerProperty()
    var powerLevel by powerLevelProperty.delegation()

    val missingPowerProperty = SimpleIntegerProperty()
    var missingPower by missingPowerProperty.delegation()

    val infoProperty = SimpleStringProperty(null)
    var info: String? by infoProperty.delegation()

    init {
        slots = listOf(*weapons.toTypedArray(), *armor.toTypedArray())
    }
}

class Model {

    val weapons: List<Slot>

    val hunter: Class
    val titan: Class
    val warlock: Class

    init {
        val slots = Gson().fromJson<List<List<Pair<String, Int>>>>(PersistencyController.load())
            .map { it.map { slot -> Slot(slot) } }
        weapons = slots[0]
        hunter = Class(slots[1], weapons)
        titan = Class(slots[2], weapons)
        warlock = Class(slots[3], weapons)
    }

    fun save() {
        val list =
            listOf(weapons, hunter.armor, titan.armor, warlock.armor).map { it.map { slot -> slot.name to slot.power } }
        val json = Gson().toJson(list)
        PersistencyController.save(json)
    }
}

inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
