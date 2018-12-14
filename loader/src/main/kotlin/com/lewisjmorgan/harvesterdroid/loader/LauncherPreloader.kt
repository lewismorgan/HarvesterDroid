package com.lewisjmorgan.harvesterdroid.loader

import javafx.application.Preloader
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle

class PreloaderStatusNotification(val status: String, value: Double): Preloader.ProgressNotification(value)

class LauncherPreloader: Preloader() {
  private val progressBar = ProgressBar()
  private val statusLabel = Label()
  private var stage: Stage? = null

  private fun createPreloaderScene(): Scene {
    val container = VBox()
    container.alignment = Pos.CENTER
    container.background = Background(BackgroundFill(Color.WHITE, null, null))

    val header = ImageView(Image(javaClass.getResourceAsStream("/images/HarvesterDroid_Header.png")))
    header.fitHeight = 100.0
    header.fitWidth = 350.0

    statusLabel.text = "Loading..."
    statusLabel.padding = Insets(5.0, 0.0, 5.0, 0.0)

    progressBar.padding = Insets(0.0, 5.0, 0.0, 5.0)

    AnchorPane.setBottomAnchor(progressBar, 0.0)
    AnchorPane.setTopAnchor(progressBar, 0.0)
    AnchorPane.setLeftAnchor(progressBar, 0.0)
    AnchorPane.setRightAnchor(progressBar, 0.0)

    container.children.addAll(header, statusLabel, AnchorPane(progressBar))

    return Scene(container)
  }

  @Throws(Exception::class)
  override fun start(stage: Stage) {
    this.stage = stage
    stage.initStyle(StageStyle.UNDECORATED)
    stage.isAlwaysOnTop = true
    stage.scene = createPreloaderScene()

    stage.show()
    val primScreenBounds = Screen.getPrimary().visualBounds
    stage.x = (primScreenBounds.width - stage.width) / 2
    stage.y = (primScreenBounds.height - stage.height) / 2
  }

  override fun handleApplicationNotification(info: Preloader.PreloaderNotification?) {
    if (info !is PreloaderStatusNotification) {
      progressBar.progress = -1.0
      statusLabel.text = "Loading..."
    } else {
      progressBar.progress = info.progress
      statusLabel.text = info.status
    }
  }

  override fun handleStateChangeNotification(evt: Preloader.StateChangeNotification?) {
    if (evt != null && evt.type == Preloader.StateChangeNotification.Type.BEFORE_START) {
      stage?.hide()
    }
  }
}