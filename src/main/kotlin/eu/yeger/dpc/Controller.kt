package eu.yeger.dpc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val GOOD = "text-green"
const val NOTE = "text-orange"
const val WARNING = "text-red"

const val TAB_NOTE = "tab-orange"

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
        updateCharacterState(character)
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
                        if (it.power == lowestPower) WARNING else NOTE
                    }
                    it.power >= powerLevel -> GOOD
                    else -> null
                }
            }
        }
    }

    private fun updateInfo(character: Class) {
        with(character) {
            info = when {
                slots.any { it.state in listOf(NOTE, WARNING) } -> "Tip: Upgrade any marked (orange or red) item"
                missingPower > 4 -> "Tip: Don't use a powerful reward right now"
                else -> null
            }
        }
    }

    private fun updateCharacterState(character: Class) {
        with(character) {
            state = when {
                slots.any { it.state in listOf(NOTE, WARNING) } -> TAB_NOTE
                else -> null
            }
        }
    }
}
