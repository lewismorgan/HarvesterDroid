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

package com.waverunnah.swg.harvesterdroid.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseXml {

    protected DocumentBuilder documentBuilder;
    protected Document document;

    public BaseXml(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    public final void load(InputStream xmlStream) throws IOException, SAXException, ParserConfigurationException {
        document = documentBuilder.parse(xmlStream);
        if (document != null)
            read(document.getDocumentElement());
    }

    public final void save(File file) throws TransformerException, IOException {
        if (!file.exists()) {
            String sub = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/"));
            new File(sub).mkdirs();
            if (!file.createNewFile())
                throw new IOException("Could not create a new xml file");
        }
        Document saveDoc = documentBuilder.newDocument();

        write(saveDoc);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
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
