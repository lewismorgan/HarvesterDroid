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

package com.waverunnah.swg.harvesterdroid.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Waverunner on 4/10/2017
 */
public class DatabaseManager {

    public static EntityManager getEntityManager(String database) {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        EntityManagerFactory emf = null;
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:" + database);
        try {
            emf = Persistence.createEntityManagerFactory("Resources", properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return emf.createEntityManager();
    }
}
