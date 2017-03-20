package com.waverunnah.swg.harvesterdroid.xml.app;

import com.waverunnah.swg.harvesterdroid.xml.BaseXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InventoryXml extends BaseXml {
	public List<String> inventory = new ArrayList<>();

	public InventoryXml(DocumentBuilder documentBuilder) {
		super(documentBuilder);
	}

	@Override
	protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
		if (!root.getNodeName().equals("inventory"))
			return;

		processElement(root, node -> {
			if (!node.getNodeName().equals("resource") || node.getAttributes().getLength() != 1)
				return;

			Node name = node.getAttributes().getNamedItem("name");
			if (name == null || name.getTextContent().isEmpty())
				return;

			inventory.add(name.getTextContent());
		});
	}

	@Override
	protected void write(Document document) {
		Element root = document.createElement("inventory");

		inventory.forEach(resource -> {
			Element node = document.createElement("resource");
			node.setAttribute("name", resource);
			root.appendChild(node);
		});

		document.appendChild(root);
	}

	public List<String> getInventory() {
		return inventory;
	}

	public void setInventory(Collection<String> newInventory) {
		inventory.clear();
		inventory.addAll(newInventory);
	}
}
