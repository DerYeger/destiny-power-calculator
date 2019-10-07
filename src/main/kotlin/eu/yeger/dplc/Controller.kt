package eu.yeger.dplc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor

class Controller(private val model: Model) {

    init {
        model.slots.forEach { it.powerProperty.addListener { _, _, _ -> update() } }
        update()
    }

    private fun update() {
        updatePowerLevel()
        updateMissingPower()
        updateSlotStates()
        updateInfo()
        GlobalScope.launch(Dispatchers.IO) { model.save() }
    }

    private fun updatePowerLevel() {
        with(model) {
            powerLevel = slots.map { it.power }.average().toInt()
        }
    }

    private fun updateMissingPower() {
        with(model) {
            missingPower = (powerLevel + 1) * slots.size - slots.sumBy { it.power }
        }
    }

    private fun updateSlotStates() {
        with(model) {
            val lowestPower = slots.map { it.power }.min() ?: -1
            slots.forEach {
                it.state = when {
                    powerLevel - it.power >= missingPower -> {
                        if (it.power == lowestPower) "warning" else "note"
                    }
                    it.power >= powerLevel -> "good"
                    else -> null
                }
            }
        }
    }

    private fun updateInfo() {
        with(model) {
            info = when {
                slots.any { it.state in listOf("note", "warning") } -> "Tip: Upgrade any marked (orange or red) item"
                missingPower > 4 -> "Tip: Don't use a powerful reward right now"
                else -> null
            }
        }
    }

    private val Double.isWholeNumber
        get() = ceil(this) == floor(this)
}