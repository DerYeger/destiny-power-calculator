package eu.yeger.dpc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.yeger.kotlin.javafx.delegation
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class Slot(data: Pair<String, Int>, val powerProperty: IntegerProperty = SimpleIntegerProperty(data.second)) {

    constructor(slot: Slot) : this(slot.name to slot.power, slot.powerProperty)

    val name = data.first
    var power by powerProperty.delegation()

    val stateProperty = SimpleStringProperty(null)
    var state: String? by stateProperty.delegation()
}

class Character(val name: String, val armor: List<Slot>, weaponData: List<Slot>) {
    val weapons = weaponData.map { Slot(it) }
    val slots: List<Slot> = listOf(*weapons.toTypedArray(), *armor.toTypedArray())

    val powerLevelProperty = SimpleIntegerProperty()
    var powerLevel by powerLevelProperty.delegation()

    val missingPowerProperty = SimpleIntegerProperty()
    var missingPower by missingPowerProperty.delegation()

    val infoProperty = SimpleStringProperty(null)
    var info: String? by infoProperty.delegation()

    val stateProperty = SimpleStringProperty(null)
    var state: String? by stateProperty.delegation()

}

class Model {
    val weapons: List<Slot>

    val hunter: Character
    val titan: Character
    val warlock: Character

    val characters: List<Character>

    init {
        val slots = Gson().fromJson<List<List<Pair<String, Int>>>>(PersistencyController.load())
            .map { it.map { slot -> Slot(slot) } }
        weapons = slots[0]
        hunter = Character("Hunter", slots[1], weapons)
        titan = Character("Titan", slots[2], weapons)
        warlock = Character("Warlock", slots[3], weapons)

        characters = listOf(hunter, titan, warlock)
    }

    fun save() {
        val list =
            listOf(weapons, hunter.armor, titan.armor, warlock.armor).map { it.map { slot -> slot.name to slot.power } }
        val json = Gson().toJson(list)
        PersistencyController.save(json)
    }
}

inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
