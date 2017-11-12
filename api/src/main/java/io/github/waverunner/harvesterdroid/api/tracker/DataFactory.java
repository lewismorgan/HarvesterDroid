package io.github.waverunner.harvesterdroid.api.tracker;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

  private static final BsonFactory bsonFactory = new BsonFactory();
  private static final JsonFactory jsonFactory = new JsonFactory();

  public DataFactory() {
    bsonFactory.enable(BsonGenerator.Feature.ENABLE_STREAMING);
  }

  /**
   * Saves an object to the provided output stream as Binary JSON.
   *
   * @param outputStream to save the BSON data to
   * @param object to be serialized
   * @throws IOException when an exception occurs writing to the output stream
   */
  public static void save(OutputStream outputStream, Object object) throws IOException {
    ByteArrayOutputStream baos = serialize(object);
    baos.writeTo(outputStream);
  }

  public static <T> T openBinaryCollection(byte[] data, TypeReference<T> typeReference)
      throws IOException {
    return openBinaryCollection(new ByteArrayInputStream(data), typeReference);
  }

  public static <T> T openBinaryCollection(ByteArrayInputStream inputStream,
      TypeReference<T> typeReference) throws IOException {
    ObjectMapper mapper = createBsonObjectMapper();
    try {
      return mapper.readValue(inputStream, typeReference);
    } catch (JsonMappingException e) {
      return null;
    }
  }

  private static ByteArrayOutputStream serialize(Object data) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ObjectMapper mapper = createBsonObjectMapper();
    mapper.writeValue(baos, data);

    return baos;
  }

  public static ObjectMapper createBsonObjectMapper() {
    return new ObjectMapper(bsonFactory);
  }

  public static ObjectMapper createJsonObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
    objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    return objectMapper;
  }

}
