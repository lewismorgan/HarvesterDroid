package io.github.waverunner.harvesterdroid.api.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * Helper utility for using Jackson.
 */
public class JacksonFactory {

  /**
   * Attempts to deserialize the provided content using an ObjectMapper implementation.
   * @param mapper to use
   * @param content to map to a POJO
   * @param type of the POJO
   * @param <T> result type
   * @return a mapped POJO
   */
  public static <T> T deserialize(ObjectMapper mapper, String content, Class<T> type) {
    try {
      return mapper.readValue(content, type);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
