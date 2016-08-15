package com.waverunnah.swg.harvesterdroid.xml.app;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.xml.BaseXml;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;

public abstract class ResourceXml extends BaseXml {
	private GalaxyResource galaxyResource;

	public ResourceXml(DocumentBuilder documentBuilder) {
		super(documentBuilder);
	}

	public GalaxyResource getGalaxyResource() {
		return galaxyResource;
	}

	@Override
	protected final void write(Document document) {
		throw new UnsupportedOperationException();
	}

	protected void setGalaxyResource(GalaxyResource galaxyResource) {
		this.galaxyResource = galaxyResource;
	}
}
