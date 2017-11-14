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

package io.github.waverunner.harvesterdroid.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * Created by Waverunner on 3/31/2017.
 */
public class DroidProperties {

  public static final String TRACKER = "tracker";
  public static final String GALAXY = "activegalaxy";
  public static final String DOWNLOAD_BUFFER = "download.buffer";
  public static final String THEME = "theme";
  public static final String WIDTH = "width";
  public static final String HEIGHT = "height";
  public static final String FULLSCREEN = "fullscreen";
  public static final String SAVE_NAG = "save.nag";
  public static final String AUTOSAVE = "autosave";
  public static final String LAST_DIRECTORY = "last.directory";
  public static final String LAST_UPDATE = "last.update";
  public static final String DEBUG = "debug";
  private static Properties properties;

  public static void save(OutputStream outputStream) {
    try {
      properties.store(new OutputStreamWriter(outputStream), "User Preferences for HarvesterDroid");
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void load(InputStream inputStream) throws IOException {
    properties = new Properties(createDefaultProperties());
    properties.load(inputStream);
    inputStream.close();
  }

  public static void set(String property, Object value) {
    properties.setProperty(property, String.valueOf(value));
  }


  public static String getString(String property) {
    return properties.getProperty(property);
  }

  public static boolean getBoolean(String property) {
    return Boolean.valueOf(getString(property));
  }

  public static double getDouble(String property) {
    return Double.valueOf(getString(property));
  }

  private static Properties createDefaultProperties() {
    Properties defaults = new Properties();
    try (InputStream inputStream = DroidProperties.class
        .getResourceAsStream("/harvesterdroid.properties")) {
      defaults.load(inputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return defaults;
  }

  public static Properties getProperties() {
    return properties;
  }

  public static void setProperties(Properties properties) {
    DroidProperties.properties = properties;
  }
}
