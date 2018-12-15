package com.lewisjmorgan.harvesterdroid.repository

import com.lewisjmorgan.harvesterdroid.Galaxy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

interface GalaxyListRepository {
  fun getAll(): Flowable<Galaxy>
  fun add(galaxy: Galaxy)
}
