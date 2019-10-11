package eu.yeger.dpc

import eu.yeger.kofx.property.IntegerPropertyDelegate
import eu.yeger.kofx.property.integerProperty
import eu.yeger.kofx.property.stringProperty

class Slot(data: SlotData, val powerProperty: IntegerPropertyDelegate = integerProperty(data.power)) {
    constructor(slot: Slot) : this(SlotData.fromSlot(slot), slot.powerProperty)

    val name = data.name
    var power by powerProperty

    val stateProperty = stringProperty(null)
    var state: String? by stateProperty
}

class Character(characterData: CharacterData, weaponData: List<Slot>) {
    val name = characterData.name

    val weapons = weaponData.map { Slot(it) }
    val armor = characterData.armor.map { Slot(it) }

    val slots: List<Slot> = listOf(*weapons.toTypedArray(), *armor.toTypedArray())

    val powerProperty = integerProperty(0)
    var power by powerProperty

    val missingPowerProperty = integerProperty(0)
    var missingPower by missingPowerProperty

    val infoProperty = stringProperty(null)
    var info: String? by infoProperty

    val stateProperty = stringProperty(null)
    var state: String? by stateProperty
}

class Model(modelData: ModelData) {
    val weapons = modelData.weapons.map { Slot(it) }
    val characters: List<Character> = modelData.characters.map { Character(it, weapons) }
}
