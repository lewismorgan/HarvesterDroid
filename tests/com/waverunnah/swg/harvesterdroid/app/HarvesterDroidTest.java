package com.waverunnah.swg.harvesterdroid.app;

import com.waverunnah.swg.harvesterdroid.downloaders.GalaxyHarvesterDownloader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HarvesterDroidTest {
	HarvesterDroid app;

	public static String ROOT_DIR = System.getProperty("user.home").replace("\\", "/") + "/.harvesterdroid";
	private static String XML_SCHEMATICS = ROOT_DIR + "/schematics.xml";
	private static String XML_INVENTORY = ROOT_DIR + "/inventory.xml";

	@Before
	public void setUp() throws Exception {
		app = new HarvesterDroid(XML_SCHEMATICS, XML_INVENTORY, new GalaxyHarvesterDownloader());
	}

	@Test
	public void addSchematic() throws Exception {
		assertTrue(app.getFilteredSchematics().size() == 1);
	}

}