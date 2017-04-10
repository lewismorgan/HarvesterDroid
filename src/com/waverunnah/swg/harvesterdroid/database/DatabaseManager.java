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

/**
 * Created by Waverunner on 4/10/2017
 */
public class DatabaseManager {
    private HashMap<String, EntityManagerFactory> entityFactories = new HashMap<>();

    public DatabaseManager() {
    }

    public EntityManager createDatabase(String database) {
        EntityManagerFactory emf = createFactory(database);

        EntityManager entityManager = emf.createEntityManager();
        entityFactories.put(database, emf);

        return entityManager;
    }

    public EntityManager loadDatabase(String database) {
        if (entityFactories.containsKey(database))
            closeDatabase(database);

        EntityManagerFactory emf = createFactory(database);

        EntityManager entityManager = emf.createEntityManager();
        entityFactories.put(database, emf);

        return entityManager;
    }

    public void closeDatabase(String database) {
        EntityManagerFactory factory = getFactory(database);
        factory.close();
        entityFactories.remove(database);
    }

    public void shutdown() {
        entityFactories.forEach((key, factory) -> factory.close());
        entityFactories.clear();
    }

    public EntityManagerFactory getFactory(String database) {
        return entityFactories.get(database);
    }

    public static <T> List<T> getList(EntityManager entityManager, Class<T> tClass) {
        List<T> query = entityManager.createQuery("Select o from " + tClass.getSimpleName() + " o", tClass).getResultList();

        return new ArrayList<>(query);
    }

    private EntityManagerFactory createFactory(String database) {
        try {
            return Persistence.createEntityManagerFactory(database);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
