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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Waverunner on 4/10/2017
 */
public class DatabaseManager {
    private HashMap<String, EntityManager> entityManagers = new HashMap<>();
    private final String root;

    public DatabaseManager(String root) {
        this.root = root;
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
    }

    public EntityManager createDatabase(String database) {
        Map<String, String> properties = getDatabaseProperties(database);
        properties.put("hibernate.hbm2ddl.auto", "create");

        EntityManagerFactory emf = createFactory(properties);

        EntityManager entityManager = emf.createEntityManager();
        entityManagers.put(database, entityManager);

        return entityManager;
    }

    public EntityManager loadDatabase(String database) {
        if (entityManagers.containsKey(database))
            closeDatabase(database);

        EntityManagerFactory emf = createFactory(getDatabaseProperties(database));

        EntityManager entityManager = emf.createEntityManager();
        entityManagers.put(database, entityManager);

        return entityManager;
    }

    public void closeDatabase(String database) {
        EntityManager entityManager = getEntityManager(database);
        entityManager.close();
        entityManagers.remove(database);
    }

    public void shutdown() {
        entityManagers.forEach((key, value) -> closeDatabase(key));
    }

    public EntityManager getEntityManager(String database) {
        return entityManagers.get(database);
    }

    public static <T> List<T> getList(EntityManager entityManager, Class<T> tClass) {
        List<T> resultList = entityManager.createQuery("from " + tClass.getSimpleName()).getResultList();

        return new ArrayList<>(resultList);
    }

    private Map<String, String> getDatabaseProperties(String database) {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:" + root + database);
        return properties;
    }

    private EntityManagerFactory createFactory(Map<String, String> properties) {
        try {
            return Persistence.createEntityManagerFactory("GalaxyResources", properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
