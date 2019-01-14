package com.lewisjmorgan.harvesterdroid.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import de.undercouch.bson4jackson.BsonFactory
import de.undercouch.bson4jackson.BsonGenerator
import java.io.InputStream
import java.io.OutputStream

class DataFactory {
  private val bsonFactory by lazy { createBsonFactory() }
  val modules = mutableListOf<Module>()

  fun serialize(outputStream: OutputStream, toSerialize: Any, outputAs: MappingType): OutputStream {
    val mapper = createObjectMapper(outputAs)
    mapper.writeValue(outputStream, toSerialize)

    return outputStream
  }

  inline fun <reified T> deserialize(inputStream: InputStream, mappingType: MappingType): T {
    val mapper = createObjectMapper(mappingType)
    return mapper.readValue(inputStream)
  }

  fun <T> deserialize(inputStream: InputStream, mappingType: MappingType, typeReference: TypeReference<T>): T {
    val mapper = createObjectMapper(mappingType)
    return mapper.readValue<T>(inputStream, typeReference)
  }

  fun createObjectMapper(type: MappingType): ObjectMapper {
    val mapper = when(type) {
      MappingType.XML -> XmlMapper()
      MappingType.JSON -> createJsonObjectMapper()
      MappingType.BSON -> createBsonObjectMapper()
    }
    mapper.registerModule(KotlinModule())
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    modules.forEach { mapper.registerModule(it) }
    return mapper
  }

  private fun createBsonObjectMapper(): ObjectMapper {
    return ObjectMapper(bsonFactory)
  }

  private fun createBsonFactory(): BsonFactory {
    val factory = BsonFactory()
    factory.enable(BsonGenerator.Feature.ENABLE_STREAMING)
    return factory
  }

  private fun createJsonObjectMapper(): ObjectMapper {
    val objectMapper = ObjectMapper()
    return objectMapper
  }
}
