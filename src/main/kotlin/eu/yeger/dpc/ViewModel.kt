package eu.yeger.dpc

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

    val data
        get() = name to power
}

class Character(val name: String, armorData: List<Pair<String, Int>>, weaponData: List<Slot>) {
    val weapons = weaponData.map { Slot(it) }
    val armor = armorData.map { Slot(it) }

    val slots: List<Slot> = listOf(*weapons.toTypedArray(), *armor.toTypedArray())

    val powerProperty = SimpleIntegerProperty()
    var power by powerProperty.delegation()

    val missingPowerProperty = SimpleIntegerProperty()
    var missingPower by missingPowerProperty.delegation()

    val infoProperty = SimpleStringProperty(null)
    var info: String? by infoProperty.delegation()

    val stateProperty = SimpleStringProperty(null)
    var state: String? by stateProperty.delegation()

}

class Model(saveData: SaveData) {
    val weapons = saveData.weaponData.map { Slot(it) }
    val characters: List<Character> = saveData.characters.map { Character(it.name, it.armorData, weapons) }
}
