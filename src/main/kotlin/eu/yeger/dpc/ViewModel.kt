package eu.yeger.dpc

import eu.yeger.kofx.property.delegate
import eu.yeger.kofx.property.integerProperty
import eu.yeger.kofx.property.stringProperty
import javafx.beans.property.IntegerProperty

class Slot(data: SlotData, val powerProperty: IntegerProperty = integerProperty(data.power)) {
    constructor(slot: Slot) : this(SlotData.fromSlot(slot), slot.powerProperty)

    val name = data.name
    var power by powerProperty.delegate()

    val stateProperty = stringProperty(null)
    var state: String? by stateProperty.delegate()
}

class Character(characterData: CharacterData, weaponData: List<Slot>) {
    val name = characterData.name

    val weapons = weaponData.map { Slot(it) }
    val armor = characterData.armor.map { Slot(it) }

    val slots: List<Slot> = listOf(*weapons.toTypedArray(), *armor.toTypedArray())

    val powerProperty = integerProperty(0)
    var power by powerProperty.delegate()

    val missingPowerProperty = integerProperty(0)
    var missingPower by missingPowerProperty.delegate()

    val infoProperty = stringProperty(null)
    var info: String? by infoProperty.delegate()

    val stateProperty = stringProperty(null)
    var state: String? by stateProperty.delegate()
}

class Model(modelData: ModelData) {
    val weapons = modelData.weapons.map { Slot(it) }
    val characters: List<Character> = modelData.characters.map { Character(it, weapons) }
}
