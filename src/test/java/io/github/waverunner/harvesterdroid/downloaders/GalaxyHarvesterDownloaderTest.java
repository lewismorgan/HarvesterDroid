/*
 * HarvesterDroid - A Resource Tracker for Star Wars Galaxies
 * Copyright (C) 2017  Waverunner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.waverunner.harvesterdroid.downloaders;

import io.github.waverunner.harvesterdroid.api.Downloader;
import io.github.waverunner.harvesterdroid.app.HarvesterDroidData;
import io.github.waverunner.harvesterdroid.api.resource.GalaxyResource;
import io.github.waverunner.harvesterdroid.trackers.galaxyharvester.GalaxyHarvesterDownloader;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by Waverunner on 4/3/2017.
 */
class GalaxyHarvesterDownloaderTest {
  private GalaxyHarvesterDownloader downloader;
  private HarvesterDroidData data;

  @BeforeEach
  void setup() {
    downloader = new GalaxyHarvesterDownloader("test", "48");
    data = new HarvesterDroidData();
  }

  @Test
  void parseCurrentResources() {
    try {
      assert (downloader.downloadCurrentResources() != Downloader.DownloadResult.FAILED);
      downloader.getCurrentResources().forEach(this::checkGalaxyResource);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void parseGalaxyList() {
  }

  @Test
  void parseGalaxyResource() {
    checkGalaxyResource(downloader.downloadGalaxyResource("enabo"));
  }

  private void checkGalaxyResource(GalaxyResource galaxyResource) {
    assert (galaxyResource != null);
    assert (galaxyResource.getName() != null);
    assert (galaxyResource.getResourceType() != null);
    assert (galaxyResource.getAttributes() != null);
  }
}