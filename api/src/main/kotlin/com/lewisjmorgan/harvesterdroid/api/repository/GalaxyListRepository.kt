package com.lewisjmorgan.harvesterdroid.api.repository

import com.lewisjmorgan.harvesterdroid.api.Galaxy
import io.reactivex.Flowable
import java.io.InputStream

interface GalaxyListRepository {
  fun getAll(): Flowable<Galaxy>
  fun add(galaxy: Galaxy)
}
