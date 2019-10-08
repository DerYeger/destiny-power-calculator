package eu.yeger.dplc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Controller(private val model: Model) {

    init {
        with(model) {
            weapons.forEach { it.powerProperty.addListener { _, _, _ -> updateAll() } }
            hunter.armor.forEach { it.powerProperty.addListener { _, _, _ -> update(hunter) } }
            titan.armor.forEach { it.powerProperty.addListener { _, _, _ -> update(titan) } }
            warlock.armor.forEach { it.powerProperty.addListener { _, _, _ -> update(warlock) } }
        }
        updateAll()
    }

    private fun updateAll() {
        with(model) {
            listOf(hunter, titan, warlock).forEach { update(it) }
        }
    }

    private fun update(character: Class) {
        updatePowerLevel(character)
        updateMissingPower(character)
        updateSlotStates(character)
        updateInfo(character)
        GlobalScope.launch(Dispatchers.IO) { model.save() }
    }

    private fun updatePowerLevel(character: Class) {
        with(character) {
            powerLevel = slots.map { it.power }.average().toInt()
        }
    }

    private fun updateMissingPower(character: Class) {
        with(character) {
            missingPower = (powerLevel + 1) * slots.size - slots.sumBy { it.power }
        }
    }

    private fun updateSlotStates(character: Class) {
        with(character) {
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

    private fun updateInfo(character: Class) {
        with(character) {
            info = when {
                slots.any { it.state in listOf("note", "warning") } -> "Tip: Upgrade any marked (orange or red) item"
                missingPower > 4 -> "Tip: Don't use a powerful reward right now"
                else -> null
            }
        }
    }
}
