package com.waverunnah.swg.harvesterdroid.app;


import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Waverunner on 3/23/2017
 */
public class HarvesterDroidData {

    private Map<String, List<String>> resourceGroups;
    private Map<String, ResourceType> resourceTypeMap;

    public HarvesterDroidData() {
        this.resourceTypeMap = new HashMap<>();
        this.resourceGroups = new HashMap<>();
        init();
    }

    private void init() {
        loadResourceTypes();
        loadResourceGroups();
    }

    private void loadResourceGroups() {
        readCsv("/data/resource_groups.txt", line -> {
            if (resourceGroups.containsKey(line[1]))
                resourceGroups.get(line[1]).add(line[0]);
            else resourceGroups.put(line[1], new ArrayList<>(Collections.singletonList(line[0])));
        });

    }

    private void loadResourceTypes() {
        readCsv("/data/resource_tree.txt", line -> {
            ResourceType type = new ResourceType();
            type.setId(line[0]);
            type.setName(line[1]);
            type.setRecylced(Boolean.valueOf(line[2]));
            Map<String, Integer> minMax = new HashMap<>();
            minMax.put("CRmin", Integer.valueOf(line[3]));
            minMax.put("CRmax", Integer.valueOf(line[4]));
            minMax.put("CDmin", Integer.valueOf(line[5]));
            minMax.put("CDmax", Integer.valueOf(line[6]));
            minMax.put("DRmin", Integer.valueOf(line[7]));
            minMax.put("DRmax", Integer.valueOf(line[8]));
            minMax.put("FLmin", Integer.valueOf(line[9]));
            minMax.put("FLmax", Integer.valueOf(line[10]));
            minMax.put("HRmin", Integer.valueOf(line[11]));
            minMax.put("HRmax", Integer.valueOf(line[12]));
            minMax.put("MAmin", Integer.valueOf(line[13]));
            minMax.put("MAmax", Integer.valueOf(line[14]));
            minMax.put("PEmin", Integer.valueOf(line[15]));
            minMax.put("PEmax", Integer.valueOf(line[16]));
            minMax.put("OQmin", Integer.valueOf(line[17]));
            minMax.put("OQmax", Integer.valueOf(line[18]));
            minMax.put("SRmin", Integer.valueOf(line[19]));
            minMax.put("SRmax", Integer.valueOf(line[20]));
            minMax.put("UTmin", Integer.valueOf(line[21]));
            minMax.put("UTmax", Integer.valueOf(line[22]));
            minMax.put("ERmin", Integer.valueOf(line[23]));
            minMax.put("ERmax", Integer.valueOf(line[24]));
            type.setMinMaxMap(minMax);
            type.setContainer(line[25]);

            resourceTypeMap.put(type.getId(), type);
        });
    }

    private void readCsv(String file, CsvParser parser) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\t";

        try {
            br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file)));
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] row = line.split(cvsSplitBy);
                if (row.length > 1)
                    parser.parse(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, ResourceType> getResourceTypeMap() {
        return resourceTypeMap;
    }

    public List<String> getResourceGroups(String group) {
        return resourceGroups.get(group);
    }

    private interface CsvParser {
        void parse(String[] line);
    }
}
