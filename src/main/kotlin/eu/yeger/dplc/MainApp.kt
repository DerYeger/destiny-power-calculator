package eu.yeger.dplc

import eu.yeger.kotlin.javafx.*
import javafx.application.Application
import javafx.stage.Stage

fun main() {
    Application.launch(MainApp::class.java)
}

class MainApp : Application() {

    private val model by lazy {
        Model()
    }

    override fun start(primaryStage: Stage) {
        primaryStage.scene = buildScene()
        primaryStage.apply {
            sizeToScene()
            isResizable = false
            show()
        }
    }

    private fun buildScene() = with(model) {
        scene {
            hBox {
                children(
                    vBox {
                        children(*(model.weapons.map { pair -> slot(pair) }.toTypedArray()))
                    },
                    vBox {
                        children(*(model.armor.map { pair -> slot(pair) }.toTypedArray()))
                    },
                    vBox {
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
                NumberField(slot.powerProperty).asSingletonFragment()
            }
        }
}
