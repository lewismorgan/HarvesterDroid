package io.github.waverunner.harvesterdroid.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by Waverunner on 8/11/17.
 */
public class DataFactory {
  private static final BsonFactory streamingFactory = new BsonFactory();

  public DataFactory() {
    streamingFactory.enable(BsonGenerator.Feature.ENABLE_STREAMING);
  }

  /**
   * Saves an object to the provided output stream as Binary JSON.
   * @param outputStream to save the BSON data to
   * @param object to be serialized
   * @throws IOException when an exception occurs writing to the output stream
   */
  public static void save(OutputStream outputStream, Object object) throws IOException {
    ByteArrayOutputStream baos = serialize(object);
    baos.writeTo(outputStream);
  }

  public static <T> T openCollection(byte[] data, TypeReference<T> typeReference) throws IOException {
    return openCollection(new ByteArrayInputStream(data), typeReference);
  }

  public static <T> T openCollection(ByteArrayInputStream inputStream, TypeReference<T> typeReference) throws IOException {
    ObjectMapper mapper = getObjectMapper();
    try {
      return mapper.readValue(inputStream, typeReference);
    } catch (JsonMappingException e) {
      return null;
    }
  }

  private static ByteArrayOutputStream serialize(Object data) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ObjectMapper mapper = getObjectMapper();
    mapper.writeValue(baos, data);

    return baos;
  }

  public static ObjectMapper getObjectMapper() {
    return new ObjectMapper(streamingFactory);
  }

}
