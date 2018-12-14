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

package com.lewisjmorgan.harvesterdroid.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Simple JAXB utility class for easy reading and writing of classes.
 *
 * <p>Created by Waverunner on 3/31/2017.
 */
@SuppressWarnings("unchecked")
public class XmlFactory {

  public static <T> List<T> loadList(Class<T> classToRead, Reader reader) {
    try {
      XMLInputFactory xif = XMLInputFactory.newInstance();
      XMLStreamReader xsr = xif.createXMLStreamReader(reader);
      xsr.nextTag(); // Advance to statements element

      JAXBContext jc = JAXBContext.newInstance(classToRead);
      Unmarshaller unmarshaller = jc.createUnmarshaller();

      List<T> list = new ArrayList<>();
      while (xsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
        T element = (T) unmarshaller.unmarshal(xsr);
        list.add(element);
      }
      return list;
    } catch (XMLStreamException | JAXBException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static <T> T read(Class<T> classToLoad, InputStream inputStream) {
    JAXBContext context;
    try {
      context = JAXBContext.newInstance(classToLoad);
      Unmarshaller um = context.createUnmarshaller();
      return (T) um.unmarshal(inputStream);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void write(Object object, OutputStream outputStream) {
    JAXBContext context;
    try {
      context = JAXBContext.newInstance(object.getClass());

      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      m.marshal(object, outputStream);

    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }
}
