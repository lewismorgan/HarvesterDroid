package com.lewisjmorgan.harvesterdroid.api;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by lewis on 8/11/17
 */
public class DataFactoryTest {

  private DataFactory dataFactory;

  @Before
  public void setup() {
    dataFactory = new DataFactory();
  }

  @Test
  public void save() throws Exception {
    Collection<GalaxyResource> resources = createResources(1);

    DataFactory.save(new FileOutputStream("data"), resources);

    assertTrue(Files.exists(Paths.get("data")));

    Files.deleteIfExists(Paths.get("data"));
  }

  @Test
  public void open() throws Exception {
    int size = 10;

    Collection<GalaxyResource> galaxyResources = createResources(size);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataFactory.save(baos, galaxyResources);

    Collection<GalaxyResource> loaded = DataFactory.openBinaryCollection(baos.toByteArray(),
        new TypeReference<Collection<GalaxyResource>>() {
        });

    assertTrue(String.format("Loaded %d resources, expected 10", loaded.size()), loaded.size() == 10);
  }

  private Collection<GalaxyResource> createResources(int amount) {
    Collection<GalaxyResource> resources = new ArrayList<>();

    for (int i = 0; i < amount; i++) {
      GalaxyResource resource = new GalaxyResource();
      resource.setName("Resource" + i);
      resources.add(resource);
    }
    return resources;
  }
}