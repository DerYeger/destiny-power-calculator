package eu.yeger.dplc

import eu.yeger.kotlin.javafx.delegation
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import javafx.util.converter.NumberStringConverter

class NumberField : TextField() {

    val valueProperty = SimpleIntegerProperty(0)
    var value by valueProperty.delegation()

    var maxValue = 1500
    var minValue = 0

    init {
        valueProperty.addListener { _, _, _ ->
            if (value !in minValue..maxValue) {
                value = value.coerceIn(minValue..maxValue)
                textProperty().value = value.toString()
            }
        }
        Bindings.bindBidirectional(
            textProperty(),
            valueProperty,
            NumberStringConverter()
        )
    }

    override fun replaceText(start: Int, end: Int, value: String?) {
        if (value?.isNumeric == true) {
            super.replaceText(start, end, value)
        }
    }

    private val String.isNumeric: Boolean
        get() = matches("[0-9]*".toRegex())
}