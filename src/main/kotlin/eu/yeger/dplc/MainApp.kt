package eu.yeger.dplc

import eu.yeger.kotlin.javafx.*
import javafx.application.Application
import javafx.geometry.Pos
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
            hBox {
                styleClasses("container")
                styleSheets("main.css")
                children(
                    vBox {
                        children(*(model.weapons.map { pair -> slot(pair) }.toTypedArray()))
                    },
                    vBox {
                        children(*(model.armor.map { pair -> slot(pair) }.toTypedArray()))
                    },
                    vBox {
                        alignment = Pos.TOP_RIGHT
                        children(
                            label(powerLevelProperty.asString("Power: %4.2f")),
                            label(missingPowerProperty.asString("Missing: %d")),
                            label("Do not use powerful drops") {
                                bindVisible(model.warningProperty)
                                styleClasses("warning")
                            }
                        )
                    }
                )
            }
        }
    }

    private fun slot(slot: Slot) =
        vBox {
            slot.warningProperty.addListener { _, _, newValue ->
                if (newValue) {
                    children.forEach { it.styleClass.add("warning") }
                } else {
                    children.forEach { it.styleClass.remove("warning") }
                }
            }
            child { label(slot.name) }
            child {
                IntegerField().apply {
                    alignment = Pos.CENTER
                    valueProperty.bindBidirectional(slot.powerProperty)
                }.asSingletonFragment()
            }
        }
}
