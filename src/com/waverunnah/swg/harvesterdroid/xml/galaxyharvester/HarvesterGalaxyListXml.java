package com.waverunnah.swg.harvesterdroid.xml.galaxyharvester;

import com.waverunnah.swg.harvesterdroid.xml.BaseXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Waverunner on 4/1/17
 */
public class HarvesterGalaxyListXml extends BaseXml {
    private Map<String, String> galaxyList = new HashMap<>();

    public HarvesterGalaxyListXml(DocumentBuilder documentBuilder) {
        super(documentBuilder);
    }

    @Override
    protected void read(Element root) throws IOException, ParserConfigurationException, SAXException {
        // <list_data>
        List<String> galaxyIds = new ArrayList<>();
        List<String> galaxyNames = new ArrayList<>();
        List<String> galaxyActive = new ArrayList<>();

        processElement(root, node -> {
            switch(node.getNodeName()) {
                case "galaxy_values":
                    processElement(node, galaxyValue -> galaxyIds.add(galaxyValue.getNodeValue()));
                    break;
                case "galaxy_names":
                    processElement(node, galaxyName -> galaxyNames.add(galaxyName.getNodeValue()));

                    break;
                case "galaxy_prop1":
                    processElement(node, galaxyProp -> galaxyActive.add(galaxyProp.getNodeValue()));
                    break;
            }
        });

        if (galaxyIds.size() != galaxyNames.size() || galaxyIds.size() != galaxyActive.size())
            return;

        for (int i = 0; i < galaxyIds.size(); i++) {
            if (!galaxyActive.get(i).equals("Active"))
                continue;
            galaxyList.put(galaxyIds.get(i), galaxyNames.get(i));
        }
        // </list_data>
    }

    @Override
    protected void write(Document document) {
        throw new UnsupportedOperationException();
    }

    public Map<String, String> getGalaxyList() {
        return galaxyList;
    }
}
