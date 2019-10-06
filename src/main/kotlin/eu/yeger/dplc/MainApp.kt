package eu.yeger.dplc

import eu.yeger.kotlin.javafx.*
import javafx.application.Application
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.text.TextAlignment
import javafx.stage.Stage

class MainApp : Application() {

    private val model by lazy {
        Model()
    }

    override fun start(primaryStage: Stage) {
        primaryStage.scene = buildScene()
        primaryStage.apply {
            title = "Power Calculator"
            sizeToScene()
            isResizable = false
            show()
        }
    }

    private fun buildScene() = with(model) {
        scene {
            vBox {
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
                                    label(powerLevelProperty.asString("Power: %4.2f")),
                                    label(missingPowerProperty.asString("Missing: %d"))
                                )
                            }
                        )
                    }
                }
                child {
                    label(infoProperty) {
                        bindStyleClass(infoStateProperty)
                        textAlignment = TextAlignment.CENTER
                    }
                }
            }
        }
    }

    private fun slot(slot: Slot) =
        vBox {
            bindStyleClass(slot.stateProperty)
            child { label(slot.name) }
            child {
                IntegerField().apply {
                    alignment = Pos.CENTER
                    valueProperty.bindBidirectional(slot.powerProperty)
                }.asSingletonFragment()
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
