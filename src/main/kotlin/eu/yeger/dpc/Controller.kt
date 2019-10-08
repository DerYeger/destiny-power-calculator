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
            characters.forEach { character ->
                character.armor.forEach {
                    it.powerProperty.addListener { _, _, _ -> update(character) }
                }
            }
        }
        updateAll(save = false)
    }

    private fun updateAll(save: Boolean = true) {
        model.characters.forEach { update(it, save = save) }
    }

    private fun update(character: Character, save: Boolean = true) {
        updatePowerLevel(character)
        updateMissingPower(character)
        updateSlotStates(character)
        updateInfo(character)
        updateCharacterState(character)
        if (save) {
            GlobalScope.launch(Dispatchers.IO) { PersistencyController.save(model) }
        }
    }

    private fun updatePowerLevel(character: Character) {
        with(character) {
            power = slots.map { it.power }.average().toInt()
        }
    }

    private fun updateMissingPower(character: Character) {
        with(character) {
            missingPower = (power + 1) * slots.size - slots.sumBy { it.power }
        }
    }

    private fun updateSlotStates(character: Character) {
        with(character) {
            val lowestPower = slots.map { it.power }.min() ?: -1
            slots.forEach {
                it.state = when {
                    power - it.power >= missingPower -> {
                        if (it.power == lowestPower) WARNING else NOTE
                    }
                    it.power >= power -> GOOD
                    else -> null
                }
            }
        }
    }

    private fun updateInfo(character: Character) {
        with(character) {
            info = when {
                slots.any { it.state in listOf(NOTE, WARNING) } -> "Tip: Upgrade any orange or red item"
                missingPower > 4 -> "Tip: Don't use a powerful reward right now"
                else -> null
            }
        }
    }

    private fun updateCharacterState(character: Character) {
        with(character) {
            state = when {
                slots.any { it.state in listOf(NOTE, WARNING) } -> TAB_NOTE
                else -> null
            }
        }
    }
}
