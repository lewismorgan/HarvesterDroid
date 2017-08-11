package io.github.waverunner.harvesterdroid.api;


import java.util.Date;

/**
 * Created by Waverunner on 8/11/17.
 */
public class GalaxyResource {
  private String name;
  private Date spawnDate;
  private Date despawnDate;

  public GalaxyResource() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getSpawnDate() {
    return spawnDate;
  }

  public void setSpawnDate(Date spawnDate) {
    this.spawnDate = spawnDate;
  }

  public Date getDespawnDate() {
    return despawnDate;
  }

  public void setDespawnDate(Date despawnDate) {
    this.despawnDate = despawnDate;
  }
}
