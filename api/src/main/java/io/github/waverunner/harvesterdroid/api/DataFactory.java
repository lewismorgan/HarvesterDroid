package io.github.waverunner.harvesterdroid.api;

import com.fasterxml.jackson.core.type.TypeReference;
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

  private final BsonFactory streamingFactory = new BsonFactory();

  public DataFactory() {
    streamingFactory.enable(BsonGenerator.Feature.ENABLE_STREAMING);
  }

  /**
   * Saves an object to the provided output stream as Binary JSON.
   * @param outputStream to save the BSON data to
   * @param object to be serialized
   * @throws IOException when an exception occurs writing to the output stream
   */
  public void save(OutputStream outputStream, Object object) throws IOException {
    ByteArrayOutputStream baos = serialize(object);
    baos.writeTo(outputStream);
  }

  public <T> T openCollection(byte[] data, TypeReference<T> typeReference) throws IOException {
    return openCollection(new ByteArrayInputStream(data), typeReference);
  }

  public <T> T openCollection(ByteArrayInputStream inputStream, TypeReference<T> typeReference) throws IOException {
    ObjectMapper mapper = getObjectMapper();

    return mapper.readValue(inputStream, typeReference);
  }

  private ByteArrayOutputStream serialize(Object data) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ObjectMapper mapper = getObjectMapper();
    mapper.writeValue(baos, data);

    return baos;
  }

  public ObjectMapper getObjectMapper() {
    return new ObjectMapper(streamingFactory);
  }

}
