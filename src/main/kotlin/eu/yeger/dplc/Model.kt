package eu.yeger.dplc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.yeger.kotlin.javafx.delegation
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import kotlin.math.ceil
import kotlin.math.floor

class Slot(data: Pair<String, Int>) {
    val name = data.first

    val powerProperty = SimpleIntegerProperty(data.second)
    var power by powerProperty.delegation()

    val warningProperty = SimpleBooleanProperty(false)
    var warning by warningProperty.delegation()
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

    val warningProperty = SimpleBooleanProperty(false)
    var warning by warningProperty.delegation()

    init {
        slots = gson.fromJson<List<Pair<String, Int>>>(PersistencyController.load()).map { Slot(it) }
        weapons = slots.subList(0, 3)
        armor = slots.subList(3, slots.size)
        slots.forEach { it.powerProperty.addListener { _, _, _ -> update() } }
        update()
    }

    private fun update() {
        powerLevel = slots.map { it.power }.average()
        missingPower = if (powerLevel.isWholeNumber)
            slots.size
        else
            ceil(powerLevel).toInt() * slots.size - slots.map { it.power }.sum()
        updateHighlighting()
        updateWarning()
        save()
    }

    private fun updateHighlighting() {
        slots.forEach { it.warning = floor(powerLevel) - it.power >= missingPower }
    }

    private fun updateWarning() {
        warning = slots.any { it.warning } || missingPower > 4
    }

    private fun save() {
        val json = gson.toJson(slots.map { it.name to it.power })
        PersistencyController.save(json)
    }
}

private val Double.isWholeNumber
    get() = ceil(this) == floor(this)

inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
