package eu.yeger.dpc

import eu.yeger.kofx.extension.bindStyleClass
import eu.yeger.kofx.extension.styleClasses
import eu.yeger.kofx.extension.styleSheets
import eu.yeger.kofx.fragment.*
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.Stage
import javafx.stage.StageStyle

class MainApp : Application() {

    override fun start(stage: Stage) {
        val model = Model(PersistencyController.load()).also { Controller(it) }
        stage.scene = scene(model)
        stage.apply {
            isAlwaysOnTop = true
            isResizable = false
            title = "Destiny Power Calculator"
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
            tabs(model.characters.map { tab(it) })
        }
    }

    private fun tab(character: Character): Tab.() -> Unit = {
        isClosable = false
        text = character.name
        bindStyleClass(character.stateProperty)
        content {
            tabContent(character)
        }
    }

    private fun tabContent(character: Character) = with(character) {
        vBox {
            alignment = Pos.CENTER
            styleClasses("container")
            child {
                hBox {
                    alignment = Pos.CENTER
                    children(
                        slots(weapons),
                        characterInfo(character),
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
            children(*slots.map { slot(it) }.toTypedArray())
        }

    private fun slot(slot: Slot) = vBox {
        alignment = Pos.CENTER
        bindStyleClass(slot.stateProperty)
        child { label(slot.name) }
        child {
            integerField(slot.powerProperty) {
                alignment = Pos.CENTER
                maxValue = 9999
                styleClasses("number-input")
            }
        }
    }

    private fun characterInfo(character: Character) = vBox {
        alignment = Pos.TOP_CENTER
        children(
            label(character.powerProperty.asString("%d Power")),
            label(character.missingPowerProperty.asString("%d points required"))
        )
    }
}
