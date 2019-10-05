package eu.yeger.dplc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.yeger.kotlin.javafx.delegation
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import kotlin.math.ceil

private const val BASE_POWER_LEVEL = 750

class Slot(data: Pair<String, Int>) {
    val name = data.first
    val powerProperty = SimpleIntegerProperty(data.second)
    var power by powerProperty.delegation()
}

class Model {

    private val gson = Gson()

    private val slots: List<Slot>
    val weapons: List<Slot>
    val armor: List<Slot>

    val powerLevelProperty = SimpleDoubleProperty()
    private var powerLevel by powerLevelProperty.delegation()

    val missingPowerProperty = SimpleIntegerProperty()
    private var missingPower by missingPowerProperty.delegation()

    init {
        slots = gson.fromJson<List<Pair<String, Int>>>(PersistencyController.load()).map { Slot(it) }
        weapons = slots.subList(0, 3)
        armor = slots.subList(3, slots.size)
        slots.forEach { it.powerProperty.addListener { _, _, _ -> updatePowerLevel() } }
        updatePowerLevel()
    }

    private fun updatePowerLevel() {
        powerLevel = slots.map { it.power }.average()
        missingPower = ceil(powerLevel).toInt() * slots.size - slots.map { it.power }.sum()
        save()
    }

    private fun save() {
        val json = gson.toJson(slots.map { it.name to it.power })
        PersistencyController.save(json)
    }

    fun reset() {
        slots.forEach { it.power = BASE_POWER_LEVEL }
    }
}

inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object: TypeToken<T>() {}.type)
