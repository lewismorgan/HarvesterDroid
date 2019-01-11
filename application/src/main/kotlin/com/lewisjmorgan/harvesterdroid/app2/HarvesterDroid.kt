package com.lewisjmorgan.harvesterdroid.app2

import com.lewisjmorgan.harvesterdroid.app2.provider.InventoryFileProvider
import com.lewisjmorgan.harvesterdroid.app2.view.MainView
import io.reactivex.Single
import tornadofx.*
import java.io.File

class HarvesterDroid: App(MainView::class)

class HarvesterDroidProvider: InventoryFileProvider {
  override fun getInventoryFile(): Single<File> {
    // TODO Change to user's home directory
    return Single.just(File("inventory.json"))
  }
}

fun main(args: Array<String>) {
  System.setProperty("javafx.preloader", "com.lewisjmorgan.harvesterdroid.loader.LauncherPreloader")
  launch<HarvesterDroid>(args)
}