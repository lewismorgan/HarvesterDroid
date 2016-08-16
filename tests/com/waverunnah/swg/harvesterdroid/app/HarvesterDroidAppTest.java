package com.waverunnah.swg.harvesterdroid.app;

import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HarvesterDroidAppTest {
	HarvesterDroidApp app;

	@Before
	public void setUp() throws Exception {
		app = new HarvesterDroidApp();
	}

	@Test
	public void addSchematic() throws Exception {
		app.getSchematics().add(Schematic.getDefault());

		assertTrue(app.getFilteredSchematics().size() == 1);
	}

}