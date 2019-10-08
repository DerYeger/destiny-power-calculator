package eu.yeger.dpc

import eu.yeger.kotlin.javafx.delegation
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class Slot(data: SlotData, val powerProperty: IntegerProperty = SimpleIntegerProperty(data.power)) {
    constructor(slot: Slot) : this(SlotData.fromSlot(slot), slot.powerProperty)

    val name = data.name
    var power by powerProperty.delegation()

    val stateProperty = SimpleStringProperty(null)
    var state: String? by stateProperty.delegation()
}

class Character(characterData: CharacterData, weaponData: List<Slot>) {
    val name = characterData.name

    val weapons = weaponData.map { Slot(it) }
    val armor = characterData.armor.map { Slot(it) }

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

class Model(modelData: ModelData) {
    val weapons = modelData.weapons.map { Slot(it) }
    val characters: List<Character> = modelData.characters.map { Character(it, weapons) }
}
