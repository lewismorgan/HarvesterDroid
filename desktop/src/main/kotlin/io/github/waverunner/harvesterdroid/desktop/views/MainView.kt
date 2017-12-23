package io.github.waverunner.harvesterdroid.desktop.views

import tornadofx.View
import tornadofx.ViewModel
import tornadofx.action
import tornadofx.button
import tornadofx.label
import tornadofx.vbox

class MainView : View() {
  private val viewModel: MainViewModel by inject()

  override val root = vbox {
    label("Hello World")
    button("Do Something") {
      action {
        viewModel.doSomething()
      }
    }
  }
}

class MainViewModel : ViewModel() {
  fun doSomething() = println("Do Something")
}