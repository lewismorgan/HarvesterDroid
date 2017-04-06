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

package com.waverunnah.swg.harvesterdroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * Created by Waverunner on 3/31/2017
 */
public class DroidProperties {
    private static Properties properties;
    public static String TRACKER = "tracker";
    public static String GALAXY = "activegalaxy";
    public static String DOWNLOAD_BUFFER = "download.buffer";

    public static String THEME = "theme";
    public static String WIDTH = "width";
    public static String HEIGHT = "height";
    public static String FULLSCREEN = "fullscreen";

    public static String SAVE_NAG = "save.nag";
    public static String AUTOSAVE = "autosave";

    public static String DEBUG = "debug";

    public static void save(OutputStream outputStream) {
        try {
            properties.store(new OutputStreamWriter(outputStream), "User Preferences for HarvesterDroid");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(InputStream inputStream) throws IOException {
        properties = new Properties(createDefaultProperties());
        properties.load(inputStream);
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
        defaults.setProperty(TRACKER, "galaxyharvester");
        defaults.setProperty(GALAXY, "48");
        defaults.setProperty(DOWNLOAD_BUFFER, "12");
        defaults.setProperty(WIDTH, "800");
        defaults.setProperty(HEIGHT, "600");
        defaults.setProperty(SAVE_NAG, "true");
        defaults.setProperty(AUTOSAVE, "true");
        defaults.setProperty(DEBUG, "false");
        defaults.setProperty(THEME, "Flat Droid");
        return defaults;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        DroidProperties.properties = properties;
    }
}
