package com.waverunnah.swg.harvesterdroid.xml;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import jdk.internal.util.xml.impl.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

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

	public final void save(File file) throws TransformerException, FileNotFoundException {
		Document saveDoc = documentBuilder.newDocument();

		write(saveDoc);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		DOMSource source = new DOMSource(saveDoc);
		StreamResult result = new StreamResult(file);
		//StreamResult result =  new StreamResult(System.out);

		transformer.transform(source, result);
	}

	protected abstract void read(Element root) throws IOException, ParserConfigurationException, SAXException;
	protected abstract void write(Document document);

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