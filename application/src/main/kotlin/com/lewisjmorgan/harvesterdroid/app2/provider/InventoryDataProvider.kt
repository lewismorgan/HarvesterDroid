package com.lewisjmorgan.harvesterdroid.app2.provider

import io.reactivex.Single
import java.io.InputStream
import java.io.OutputStream

interface InventoryDataProvider {
  fun inventoryOutputStream(): Single<OutputStream>
  fun inventoryInputStream(): Single<InputStream>
}
