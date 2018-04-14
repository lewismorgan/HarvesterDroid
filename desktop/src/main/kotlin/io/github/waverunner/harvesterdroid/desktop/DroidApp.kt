package io.github.waverunner.harvesterdroid.desktop

import io.github.waverunner.harvesterdroid.desktop.views.MainView
import javafx.application.Application
import tornadofx.*

class DroidApp : App(MainView::class)


fun main(args: Array<String>) {
  Application.launch(DroidApp::class.java, *args)
}