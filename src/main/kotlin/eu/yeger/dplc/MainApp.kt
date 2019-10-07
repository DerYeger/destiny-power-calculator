package eu.yeger.dplc

import eu.yeger.kotlin.javafx.*
import javafx.application.Application
import javafx.beans.property.IntegerProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.Stage
import javafx.stage.StageStyle

class MainApp : Application() {

    private val model = Model()

    private val controller = Controller(model)

    override fun start(primaryStage: Stage) {
        primaryStage.scene = buildScene()
        primaryStage.apply {
            title = "Power Calculator"
            initStyle(StageStyle.UTILITY)
            isAlwaysOnTop = true
            sizeToScene()
            isResizable = false
            show()
        }
    }

    private fun buildScene() = scene {
        tabPane {
            tab {
                isClosable = false
                text = "Hunter"
                content {
                    buildTab()
                }
            }
            tab {
                isClosable = false
                text = "Titan"
                content {
                    buildTab()
                }
            }
            tab {
                isClosable = false
                text = "Warlock"
                content {
                    buildTab()
                }
            }
        }
    }

    private fun buildTab() = with(model) {
        vBox {
            alignment = Pos.CENTER
            styleClasses("container")
            styleSheets("main.css")
            child {
                hBox {
                    children(
                        vBox {
                            children(*(weapons.map { pair -> slot(pair) }.toTypedArray()))
                        },
                        vBox {
                            children(*(armor.map { pair -> slot(pair) }.toTypedArray()))
                        },
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
                    styleClasses("note")
                }
            }
        }
    }

    private fun slot(slot: Slot) =
        vBox {
            alignment = Pos.CENTER
            bindStyleClass(slot.stateProperty)
            child { label(slot.name) }
            child {
                numberField(slot.powerProperty) {
                    alignment = Pos.CENTER
                }
            }
        }
}

fun Parent.bindStyleClass(observable: ObservableValue<String?>) {
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

fun <T : Node> Tab.content(block: @FXMarker Child.() -> Fragment<T>) {
    content = block(Child).instance()
}
