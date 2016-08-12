package com.waverunnah.swg.harvesterdroid.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BaseXml {

	protected BaseXml instance;
	protected DocumentBuilder documentBuilder;
	protected Document document;

	public BaseXml(DocumentBuilder documentBuilder) {
		this.documentBuilder = documentBuilder;
	}

	public final void load(InputStream xmlStream) throws IOException, SAXException, ParserConfigurationException {
		document = documentBuilder.parse(xmlStream);
		xmlStream.close();
		if (document != null)
			read(document.getDocumentElement());
	}

	// TODO XML Writing
	public OutputStream getOutputStream() {
		return null;
	}

	protected abstract void read(Element root) throws IOException, ParserConfigurationException, SAXException;
	protected abstract void write(OutputStream outputStream);


	public final void processElement(Node node, Processor processor) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (!(child instanceof Element))
				continue;
			processor.process(child);
		}
	}

	public Element getRoot() {
		return document.getDocumentElement();
	}

	protected interface Processor {
		void process(Node node);
	}
}
