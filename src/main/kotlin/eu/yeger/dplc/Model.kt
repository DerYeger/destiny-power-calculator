package eu.yeger.dplc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.yeger.kotlin.javafx.delegation
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty

private val BASE_POWER_LEVEL = 750

class Slot(slotData: SlotData) {
    
    val name = slotData.name
    val powerProperty = SimpleIntegerProperty(slotData.power)
    var power by powerProperty.delegation()

    fun getData() = SlotData(name, power)
}

class SlotData(val name: String, val power: Int) {
    constructor(name: String) : this(name, BASE_POWER_LEVEL)
}

class Model {

    val gson = Gson()
    
    val weaponJson = "[{\"name\":\"Kinetic\",\"power\":666},{\"name\":\"Energy\",\"power\":666},{\"name\":\"Heavy\",\"power\":666}]"
    val armorJson = "[{\"name\":\"Helmet\",\"power\":666},{\"name\":\"Gauntlets\",\"power\":666},{\"name\":\"Chest\",\"power\":666},{\"name\":\"Legs\",\"power\":666},{\"name\":\"Class\",\"power\":666}]"

    val weapons = gson.fromJson<List<SlotData>>(weaponJson).map { Slot(it) }

    val armor = gson.fromJson<List<SlotData>>(armorJson).map { Slot(it) }

    val slots = listOf(*weapons.toTypedArray(), *armor.toTypedArray())

    val powerLevelProperty = SimpleDoubleProperty(BASE_POWER_LEVEL.toDouble())
    var powerLevel by powerLevelProperty.delegation()

    init {
        load()
        slots.forEach { it.powerProperty.addListener { _, _, _ -> updatePowerLevel() } }
    }

    private fun updatePowerLevel() {
        powerLevel = slots.map { it.power }.average()
        save()
    }

    private fun save() {
        val gson = Gson()
        val json = gson.toJson(slots.map { it.toSlotData() })
        println(json)
    }

    private fun load() {
        weapons = gson.fromJson<List<SlotData>>(weaponJson).map { Slot(it) }
    }

    fun reset() {
        slots.forEach { it.power = BASE_POWER_LEVEL }
    }
}

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)