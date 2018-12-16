package com.lewisjmorgan.harvesterdroid.api

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import de.undercouch.bson4jackson.BsonFactory
import de.undercouch.bson4jackson.BsonGenerator
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

class JsonDataFactory {
  private val bsonFactory = BsonFactory()
  private val jsonFactory = JsonFactory()

  init {
    bsonFactory.enable(BsonGenerator.Feature.ENABLE_STREAMING)
  }

  /**
   * Saves an object to the provided output stream as Binary JSON.
   *
   * @param outputStream to save the BSON data to
   * @param object to be serialized
   * @throws IOException when an exception occurs writing to the output stream
   */
  @Throws(IOException::class)
  fun save(outputStream: OutputStream, `object`: Any) {
    val serializedObj = serialize(`object`)
    serializedObj.writeTo(outputStream)
  }

  @Suppress("unused")
  @Throws(IOException::class)
  fun <T> openBinaryCollection(data: ByteArray, typeReference: TypeReference<T>): T? {
    return openBinaryCollection(ByteArrayInputStream(data), typeReference)
  }

  @Throws(IOException::class)
  fun <T> openBinaryCollection(inputStream: ByteArrayInputStream, typeReference: TypeReference<T>): T? {
    val mapper = createBsonObjectMapper()
    return try {
      mapper.readValue<T>(inputStream, typeReference)
    } catch (e: JsonMappingException) {
      null
    }
  }

  @Throws(IOException::class)
  private fun serialize(data: Any): ByteArrayOutputStream {
    val outputStream = ByteArrayOutputStream()

    val mapper = createBsonObjectMapper()
    mapper.writeValue(outputStream, data)

    return outputStream
  }

  private fun createBsonObjectMapper(): ObjectMapper {
    return ObjectMapper(bsonFactory)
  }

  @Suppress("unused")
  fun createJsonObjectMapper(): ObjectMapper {
    val objectMapper = ObjectMapper(jsonFactory)
    objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    return objectMapper
  }
}