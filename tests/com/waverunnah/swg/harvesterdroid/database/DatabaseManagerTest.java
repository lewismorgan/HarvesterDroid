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

import com.waverunnah.swg.harvesterdroid.app.HarvesterDroid;
import com.waverunnah.swg.harvesterdroid.app.HarvesterDroidData;
import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.downloaders.GalaxyHarvesterDownloader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by Waverunner on 4/10/2017
 */
class DatabaseManagerTest {
    private HarvesterDroid harvesterDroid;
    private DatabaseManager databaseManager;

    @BeforeEach
    void setUp() {
        databaseManager = new DatabaseManager();
        harvesterDroid = new HarvesterDroid(new GalaxyHarvesterDownloader(HarvesterDroidData.ROOT_DIR, "48"), databaseManager);
        harvesterDroid.updateResources();

    }

    @Test
    void getEntityManager() {
        EntityManager entityManager = databaseManager.createDatabase("test");

        entityManager.getTransaction().begin();
        harvesterDroid.getResources().forEach(entityManager::persist);
        entityManager.getTransaction().commit();


    }

    @Test
    void getList() {
        EntityManager entityManager = databaseManager.loadDatabase("test");

        List<GalaxyResource> resources = DatabaseManager.getList(entityManager, GalaxyResource.class);
        resources.forEach(System.out::println);
    }
}