package eu.yeger.dplc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.yeger.kotlin.javafx.delegation
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.*
import kotlin.math.ceil
import kotlin.math.floor

class Slot(data: Pair<String, Int>) {
    val name = data.first

    val powerProperty = SimpleIntegerProperty(data.second)
    var power by powerProperty.delegation()

    val stateProperty = SimpleStringProperty(null)
    var state: String? by stateProperty.delegation()
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

    val infoProperty = SimpleStringProperty(null)
    var info: String? by infoProperty.delegation()

    init {
        slots = gson.fromJson<List<Pair<String, Int>>>(PersistencyController.load()).map { Slot(it) }
        weapons = slots.subList(0, 3)
        armor = slots.subList(3, slots.size)
        slots.forEach { it.powerProperty.addListener { _, _, _ -> update() } }
        update()
    }

    private fun update() {
        updatePowerLevel()
        updateMissingPower()
        updateSlotStates()
        updateInfo()
        GlobalScope.launch(Dispatchers.IO) { save() }
    }

    private fun updatePowerLevel() {
        powerLevel = slots.map { it.power }.average()
    }

    private fun updateMissingPower() {
        missingPower = if (powerLevel.isWholeNumber)
            slots.size
        else
            ceil(powerLevel).toInt() * slots.size - slots.map { it.power }.sum()
    }

    private fun updateSlotStates() {
        val lowestPower = slots.map { it.power }.min() ?: -1
        slots.forEach {
            it.state = when {
                floor(powerLevel) - it.power >= missingPower -> {
                    if (it.power == lowestPower) "warning" else "note"
                }
                it.power >= powerLevel.toInt() -> "good"
                else -> null
            }
        }
    }

    private fun updateInfo() {
        info = when {
            slots.any { it.state in listOf("note", "warning") } -> "Tip: Upgrade any marked (orange or red) item"
            missingPower > 4 -> "Tip: Don't use a powerful reward right now"
            else -> null
        }
    }

    private fun save() {
        val json = gson.toJson(slots.map { it.name to it.power })
        PersistencyController.save(json)
    }
}

private val Double.isWholeNumber
    get() = ceil(this) == floor(this)

inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
