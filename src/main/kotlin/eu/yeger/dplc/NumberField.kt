package eu.yeger.dplc

import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.util.converter.NumberStringConverter

class NumberField(numberProperty: Property<Number>) : TextField() {

    init {
        textFormatter = TextFormatter<String> { change ->
            if (change?.text?.matches("[0-9]*".toRegex()) == true)
                change
            else
                null
        }
        Bindings.bindBidirectional(
            textProperty(),
            numberProperty,
            NumberStringConverter()
        )
    }
}