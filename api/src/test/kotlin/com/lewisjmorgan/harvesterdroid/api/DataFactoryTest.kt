package com.lewisjmorgan.harvesterdroid.api

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DataFactoryTest: Spek({
  describe("DataFactory") {
    val dataFactory by memoized { DataFactory() }
    describe("serialize an object") {
      val testObj by memoized { SerializeTestObj("Test", 1337)}

      MappingType.values().forEach {
        it("$it") {
          when(it) {
            MappingType.XML -> {
              val expected = "<SerializeTestObj><name>Test</name><id>1337</id></SerializeTestObj>"
              val outputStream = dataFactory.serialize(ByteArrayOutputStream(135), testObj, MappingType.XML)
              assertEquals(expected, outputStream.toString())
            }
            MappingType.JSON -> {
              val expected = """{"name":"Test","id":1337}"""
              val outputStream = dataFactory.serialize(ByteArrayOutputStream(51), testObj, MappingType.JSON)
              assertEquals(expected, outputStream.toString())
            }
            MappingType.BSON -> {
              val expected = listOf<Byte>(0, 0, 0, 0,2, 110, 97, 109, 101, 0, 5, 0, 0, 0, 84, 101, 115, 116, 0, 16, 105, 100, 0, 57, 5, 0, 0, 0)
              val outputStream = dataFactory.serialize(ByteArrayOutputStream(), testObj, MappingType.BSON) as ByteArrayOutputStream
              assertEquals(expected, outputStream.toByteArray().toList())
            }
            else -> assertFalse(true, "No serialize test for $it")
          }
        }
      }
    }
    describe("deserialize an object") {
      val testObj by memoized { SerializeTestObj("Test", 1337) }
      MappingType.values().forEach {
        it("$it") {
          when(it) {
            MappingType.XML -> {
              val str = "<SerializeTestObj><name>Test</name><id>1337</id></SerializeTestObj>"
              val obj = dataFactory.deserialize<SerializeTestObj>(ByteArrayInputStream(str.toByteArray()), it)
              assertEquals(testObj, obj)
            }
            MappingType.JSON -> {
              val str = """{"name":"Test","id":1337}"""
              val obj = dataFactory.deserialize<SerializeTestObj>(ByteArrayInputStream(str.toByteArray()), it)
              assertEquals(testObj, obj)
            }
            MappingType.BSON -> {
              val bytes = listOf<Byte>(0, 0, 0, 0,2, 110, 97, 109, 101, 0, 5, 0, 0, 0, 84, 101, 115, 116, 0, 16, 105, 100, 0, 57, 5, 0, 0, 0)
              val obj = dataFactory.deserialize<SerializeTestObj>(ByteArrayInputStream(bytes.toByteArray()), it)
              assertEquals(testObj, obj)
            }
            else -> assertFalse(true, "No deserialize test for $it")
          }
        }
      }
    }
  }
})

data class SerializeTestObj(val name: String, val id: Int)