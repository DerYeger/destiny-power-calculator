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
                            label(powerLevelProperty.asString()),
                            button("Reset") { setOnAction { reset() } }
                        )
                    }
                )
            }
        }
    }

    private fun slot(slot: Slot) =
        vBox {
            child { label(slot.name) }
            child {
                IntegerField().apply {
                    valueProperty.bindBidirectional(slot.powerProperty)
                }.asSingletonFragment()
            }
        }
}
