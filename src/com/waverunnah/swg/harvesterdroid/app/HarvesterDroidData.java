package com.waverunnah.swg.harvesterdroid.app;


import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Waverunner on 3/23/2017
 */
public class HarvesterDroidData {

    private Map<String, ResourceType> resourceTypeMap;

    public HarvesterDroidData() {
        this.resourceTypeMap = new HashMap<>();
        init();
    }

    private void init() {
        loadResourceTypes();
    }

    private void loadResourceTypes() {
        readCsv("../data/raw/types.txt", line -> {
            ResourceType type = new ResourceType();
            type.setName(line[0]);
            type.setFullName(line[1]);
            type.setCategory(line[2]);
            type.setGroup(line[3]);
            Map<String, Integer> minMax = new HashMap<>();
            minMax.put("CRmin", Integer.valueOf(line[4]));
            minMax.put("CRmax", Integer.valueOf(line[5]));
            minMax.put("CDmin", Integer.valueOf(line[6]));
            minMax.put("CDmax", Integer.valueOf(line[7]));
            minMax.put("DRmin", Integer.valueOf(line[8]));
            minMax.put("DRmax", Integer.valueOf(line[9]));
            minMax.put("FLmin", Integer.valueOf(line[10]));
            minMax.put("FLmax", Integer.valueOf(line[11]));
            minMax.put("HRmin", Integer.valueOf(line[12]));
            minMax.put("HRmax", Integer.valueOf(line[13]));
            minMax.put("MAmin", Integer.valueOf(line[14]));
            minMax.put("MAmax", Integer.valueOf(line[15]));
            minMax.put("PEmin", Integer.valueOf(line[16]));
            minMax.put("PEmax", Integer.valueOf(line[17]));
            minMax.put("OQmin", Integer.valueOf(line[18]));
            minMax.put("OQmax", Integer.valueOf(line[19]));
            minMax.put("SRmin", Integer.valueOf(line[20]));
            minMax.put("SRmax", Integer.valueOf(line[21]));
            minMax.put("UTmin", Integer.valueOf(line[22]));
            minMax.put("UTmax", Integer.valueOf(line[23]));
            minMax.put("ERmin", Integer.valueOf(line[24]));
            minMax.put("ERmax", Integer.valueOf(line[25]));
            type.setMinMaxMap(minMax);
            type.setContainer(line[26]);
            type.setInventoryType(line[27]);

            resourceTypeMap.put(line[0], type);
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

    private interface CsvParser {
        void parse(String[] line);
    }
}
