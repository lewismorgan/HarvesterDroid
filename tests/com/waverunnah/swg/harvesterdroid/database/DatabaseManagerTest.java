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
import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;
import com.waverunnah.swg.harvesterdroid.downloaders.GalaxyHarvesterDownloader;
import com.waverunnah.swg.harvesterdroid.xml.XmlFactory;
import com.waverunnah.swg.harvesterdroid.xml.app.GalaxyResourcesXml;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Waverunner on 4/10/2017
 */
class DatabaseManagerTest {

    @Test
    void getEntityManager() {
        EntityManager entityManager = DatabaseManager.getEntityManager(HarvesterDroidData.ROOT_DIR + "/database");
        HarvesterDroid harvesterDroid = new HarvesterDroid(new GalaxyHarvesterDownloader(HarvesterDroidData.ROOT_DIR, "48"));
        harvesterDroid.updateResources();

        ResourceType resourceType = harvesterDroid.getResources().get(0).getResourceType();

        for (int i=0; i < 10000; i++) {
            GalaxyResource resource = new GalaxyResource();
            resource.setName("spam_resource_" + i);
            resource.setResourceType(resourceType);
            resource.setAttribute("OQ", 1000);
            resource.setContainer("water");
            resource.setDate(new Date(System.currentTimeMillis()));
            resource.setPlanets(Collections.singletonList("Tatooine"));
            harvesterDroid.getResources().add(resource);
        }

        long databaseStart = System.nanoTime();
        entityManager.getTransaction().begin();
        harvesterDroid.getResources().forEach(entityManager::persist);
        entityManager.getTransaction().commit();
        long databaseEnd = System.nanoTime();


        GalaxyResourcesXml resourcesXml = new GalaxyResourcesXml();
        resourcesXml.setGalaxyResources(harvesterDroid.getResources());

        long xmlStart = 0;
        long xmlEnd = 0;
        try {
            xmlStart = System.nanoTime();
            XmlFactory.write(resourcesXml, new FileOutputStream(new File(HarvesterDroidData.ROOT_DIR + "/massive_list.xml")));
            xmlEnd = System.nanoTime();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Wrote " + harvesterDroid.getResources().size() + " resources to database in " + (databaseEnd - databaseStart) + " and in xml in " + (xmlEnd - xmlStart));
    }

}