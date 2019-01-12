package com.lewisjmorgan.harvesterdroid.app2.events

import tornadofx.*

class AppStateEvent(val type: AppStateEventType): FXEvent()

enum class AppStateEventType {
  SAVE, LOAD
}