package eu.yeger.dplc

import eu.yeger.kotlin.javafx.*
import javafx.application.Application
import javafx.beans.property.IntegerProperty
import javafx.beans.value.ObservableValue
import javafx.css.Styleable
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.Stage
import javafx.stage.StageStyle

class MainApp : Application() {

    override fun start(stage: Stage) {
        val model = Model().also { Controller(it) }
        stage.scene = scene(model)
        stage.apply {
            isAlwaysOnTop = true
            isResizable = false
            title = "Power Calculator"
            initStyle(StageStyle.UTILITY)
            sizeToScene()
        }

        stage.show()
    }

    private fun scene(model: Model) = scene {
        tabPane {
            tabMinWidth = 80.0
            styleSheets("main.css")
            tabs(model.classes.map { tab(it) })
        }
    }

    private fun tab(character: Class): Tab.() -> Unit = {
        isClosable = false
        text = character.name
        bindStyleClass(character.stateProperty)
        content {
            tabContent(character)
        }
    }

    private fun tabContent(character: Class) = with(character) {
        vBox {
            alignment = Pos.CENTER
            styleClasses("container")
            child {
                hBox {
                    children(
                        slots(weapons),
                        slots(armor),
                        vBox {
                            alignment = Pos.TOP_RIGHT
                            children(
                                label(powerLevelProperty.asString("%d Power")),
                                label(missingPowerProperty.asString("%d points required"))
                            )
                        }
                    )
                }
            }
            child {
                label(infoProperty) {
                    styleClasses(NOTE)
                }
            }
        }
    }

    private fun slots(slots: List<Slot>) =
        vBox {
            styleClasses("slot-list")
            children(*(slots.map { slot(it) }.toTypedArray()))
        }

    private fun slot(slot: Slot) = vBox {
        alignment = Pos.CENTER
        bindStyleClass(slot.stateProperty)
        child { label(slot.name) }
        child {
            numberField(slot.powerProperty) {
                alignment = Pos.CENTER
                maxValue = 9999
                styleClasses("number-input")
            }
        }
    }
}

fun Styleable.bindStyleClass(observable: ObservableValue<String?>) {
    observable.value?.let { styleClass.add(it) }
    observable.addListener { _, oldValue, newValue ->
        styleClass.remove(oldValue)
        newValue?.let { styleClass.add(it) }
    }
}

fun numberField(observable: IntegerProperty, init: @FXMarker NumberField.() -> Unit) = Fragment {
    NumberField().apply {
        valueProperty.bindBidirectional(observable)
        init()
    }
}

fun tabPane(init: @FXMarker TabPane.() -> Unit) = Fragment {
    TabPane().apply(init)
}

fun TabPane.tab(init: @FXMarker Tab.() -> Unit) {
    tabs += Tab().apply(init)
}

fun TabPane.tabs(blocks: List<@FXMarker Tab.() -> Unit>) {
    blocks.map { tab(it) }
}

fun <T : Node> Tab.content(block: @FXMarker Child.() -> Fragment<T>) {
    content = block(Child).instance()
}
