package com.lewisjmorgan.harvesterdroid.app2.provider

import io.reactivex.Single
import java.io.File

interface InventoryFileProvider {
  fun getInventoryFile(): Single<File>
}
