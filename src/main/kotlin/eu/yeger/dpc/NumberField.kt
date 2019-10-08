package eu.yeger.dpc

import eu.yeger.kotlin.javafx.delegation
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.TextField
import javafx.util.converter.NumberStringConverter

class NumberField : TextField() {

    val valueProperty = SimpleIntegerProperty(0)
    var value by valueProperty.delegation()

    var maxValue = Int.MAX_VALUE
        set(value) {
            field = value.coerceAtLeast(0)
        }

    init {
        valueProperty.addListener { _, _, _ ->
            if (value !in 0..maxValue) {
                value = value.coerceIn(0..maxValue)
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
        if (value?.isNumeric == true && start <= end) {
            super.replaceText(start, end, value)
        }
    }

    private val String.isNumeric: Boolean
        get() = matches("[0-9]*".toRegex())
}