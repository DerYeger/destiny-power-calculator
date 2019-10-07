package eu.yeger.dplc

import eu.yeger.kotlin.javafx.*
import javafx.application.Application
import javafx.beans.property.IntegerProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.stage.Stage
import javafx.stage.StageStyle

class MainApp : Application() {

    private val model by lazy {
        Model()
    }

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

    private fun buildScene() = with(model) {
        scene {
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
                                    label(powerLevelProperty.asString("%4.0f Power")),
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

fun numberField(observable: IntegerProperty, init: NumberField.() -> Unit) = Fragment {
    NumberField().apply {
        valueProperty.bindBidirectional(observable)
        init()
    }
}
