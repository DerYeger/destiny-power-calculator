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

class Class(val name: String, val armor: List<Slot>, val weapons: List<Slot>) {
    val slots: List<Slot>

    val powerLevelProperty = SimpleIntegerProperty()
    var powerLevel by powerLevelProperty.delegation()

    val missingPowerProperty = SimpleIntegerProperty()
    var missingPower by missingPowerProperty.delegation()

    val infoProperty = SimpleStringProperty(null)
    var info: String? by infoProperty.delegation()

    val stateProperty = SimpleStringProperty(null)
    var state: String? by stateProperty.delegation()

    init {
        slots = listOf(*weapons.toTypedArray(), *armor.toTypedArray())
    }
}

class Model {

    val weapons: List<Slot>

    val hunter: Class
    val titan: Class
    val warlock: Class

    val classes: List<Class>

    init {
        val slots = Gson().fromJson<List<List<Pair<String, Int>>>>(PersistencyController.load())
            .map { it.map { slot -> Slot(slot) } }
        weapons = slots[0]
        hunter = Class("Hunter", slots[1], weapons)
        titan = Class("Titan", slots[2], weapons)
        warlock = Class("Warlock", slots[3], weapons)

        classes = listOf(hunter, titan, warlock)
    }

    fun save() {
        val list =
            listOf(weapons, hunter.armor, titan.armor, warlock.armor).map { it.map { slot -> slot.name to slot.power } }
        val json = Gson().toJson(list)
        PersistencyController.save(json)
    }
}

inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
