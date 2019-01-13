package com.lewisjmorgan.harvesterdroid.app2.view

import com.lewisjmorgan.harvesterdroid.app2.viewmodel.InventoryItemModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javafx.scene.layout.VBox
import tornadofx.*

class InventoryItemEditor: View("Add Inventory Resource") {
  override val root = VBox()
  val model: InventoryItemModel by inject()
  private val commits: PublishSubject<InventoryItemModel> = PublishSubject.create()
  val commitsObserver = commits.toFlowable(BackpressureStrategy.BUFFER)

  init {
    with(root) {
      paddingAll = 5.0
      form {
        fieldset("Inventory Editor") {
          field("Resource") {
            textfield(model.resource).required()
          }
          field("Type") {
            // TODO Type updated if the name is changed in the text field by making a request to the Tracker for a resource
            textfield {}
          }
          buttonbar {
            button("Apply") {
              enableWhen { model.dirty }
              action {
                model.commit()
                commits.onNext(model)
                println("Saved $model")
              }
            }
            button("Reset") {
              action {
                model.rollback()
              }
            }
          }
        }
      }
    }
  }
}

fun inventoryItemEditor(): Observable<InventoryItemModel> {
  return Observable.create<InventoryItemModel> { emitter ->
    with(find(InventoryItemEditor::class)) {
      openWindow()
      commitsObserver.subscribe {
        emitter.onNext(it)
      }
      whenDeleted {
        emitter.onComplete()
      }
    }
  }
//  return Observable.fromCallable {
//    tornadofx.find<InventoryItemEditor>().apply {
//      openModal()
//
//    }
//  }
}