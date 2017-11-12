package io.github.waverunner.harvesterdroid.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.waverunner.harvesterdroid.api.resource.GalaxyResource;
import io.github.waverunner.harvesterdroid.api.tracker.DataFactory;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Created by lewis on 8/11/17
 */
class DataFactoryTest {
  private static DataFactory dataFactory;

  @BeforeAll
  static void setup() {
    dataFactory = new DataFactory();
  }

  @Test
  void save() throws Exception {
    Collection<GalaxyResource> resources = createResources(1);

    DataFactory.save(new FileOutputStream("data"), resources);

    assertTrue(Files.exists(Paths.get("data")));

    Files.deleteIfExists(Paths.get("data"));
  }

  @Test
  void open() throws Exception {
    int size = 10;

    Collection<GalaxyResource> galaxyResources = createResources(size);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataFactory.save(baos, galaxyResources);

    Collection<GalaxyResource> loaded = DataFactory.openBinaryCollection(baos.toByteArray(),
        new TypeReference<Collection<GalaxyResource>>() {});

    assertTrue(loaded.size() == 10, String.format("Loaded %d resources, expected 10", loaded.size()));
  }

  private static Collection<GalaxyResource> createResources(int amount) {
    Collection<GalaxyResource> resources = new ArrayList<>();

    for (int i = 0; i < amount; i++) {
      GalaxyResource resource = new GalaxyResource();
      resource.setName("Resource" + i);
      resources.add(resource);
    }
    return resources;
  }
}