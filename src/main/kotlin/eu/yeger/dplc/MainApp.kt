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
import javafx.geometry.Insets
import javafx.geometry.Side
import javafx.scene.layout.StackPane

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
            show()
        }

        (stage.scene.root as TabPane).apply {
            centerTabs()
            requestFocus()
        }
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
                    alignment = Pos.CENTER
                    children(
                        slots(weapons),
                        vBox {
                            alignment = Pos.TOP_CENTER
                            children(
                                label(powerLevelProperty.asString("%d Power")),
                                label(missingPowerProperty.asString("%d points required"))
                            )
                        },
                        slots(armor)
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

    private fun TabPane.centerTabs() {
        val region = lookup(".headers-region") as StackPane
        val regionTop = when (side!!) {
            Side.TOP -> lookup(".tab-pane:top *.tab-header-area")
            Side.RIGHT -> lookup(".tab-pane:right *.tab-header-area")
            Side.BOTTOM -> lookup(".tab-pane:bottom *.tab-header-area")
            Side.LEFT -> lookup(".tab-pane:left *.tab-header-area")
        } as StackPane
        val insets = regionTop.padding
        regionTop.padding = when (side!!) {
            Side.TOP, Side.BOTTOM -> Insets(
                insets.top,
                insets.right,
                insets.bottom,
                regionTop.width / 2 - region.width / 2
            )
            Side.LEFT, Side.RIGHT -> Insets(
                regionTop.height / 2 - region.height / 2,
                insets.right,
                insets.bottom,
                insets.left
            )
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
