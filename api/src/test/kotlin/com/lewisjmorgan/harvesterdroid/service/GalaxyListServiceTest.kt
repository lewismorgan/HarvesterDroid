package com.lewisjmorgan.harvesterdroid.service

import com.lewisjmorgan.harvesterdroid.Galaxy
import com.lewisjmorgan.harvesterdroid.Tracker
import com.lewisjmorgan.harvesterdroid.repository.GalaxyListRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Flowable
import io.reactivex.subscribers.TestSubscriber
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.todo

class GalaxyListServiceTest : Spek({
  describe("GalaxyListService") {
    val testGalaxies = listOf(Galaxy("0", "Testing Galaxy"))

    val repository by memoized { mock<GalaxyListRepository> {
      on { getAll() } doReturn Flowable.fromIterable(testGalaxies)
    }}

    val tracker by memoized { mock<Tracker>() }
    val service by memoized { GalaxyListService(repository, tracker) }

    describe("getGalaxies()") {
      val testSubscriber by memoized { TestSubscriber<Galaxy>() }

      it("emits galaxies from a tracker") {
        val galaxies = service.getGalaxies()
        galaxies.subscribe(testSubscriber)
        testSubscriber.assertResult(testGalaxies[0])
      }
      it("returns repository galaxies after update") {
        todo { TODO("Add test") }
      }
    }
  }
})