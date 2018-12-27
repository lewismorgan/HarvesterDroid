package com.lewisjmorgan.harvesterdroid.app2

import com.lewisjmorgan.harvesterdroid.app2.view.MainView
import tornadofx.*

class HarvesterDroid: App(MainView::class)

fun main(args: Array<String>) {
  System.setProperty("javafx.preloader", "com.lewisjmorgan.harvesterdroid.loader.LauncherPreloader")
  launch<HarvesterDroid>(args)
}