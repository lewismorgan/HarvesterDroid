package com.waverunnah.swg.harvesterdroid.app;


import com.waverunnah.swg.harvesterdroid.Launcher;
import com.waverunnah.swg.harvesterdroid.data.resources.ResourceType;
import com.waverunnah.swg.harvesterdroid.gui.dialog.ExceptionDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Waverunner on 3/23/2017
 */
public class HarvesterDroidData {

    private Map<String, List<String>> legacy_resourceGroups;
    private Map<String, ResourceType> resourceTypeMap;

    public HarvesterDroidData() {
        this.resourceTypeMap = new HashMap<>();
        this.legacy_resourceGroups = new HashMap<>();
        init();
    }

    private void init() {
        loadResourceTypes();
        legacy_loadResourceGroups();
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

    private void legacy_loadResourceGroups() {
        // This only needs to be done for the resources that do not follow the proper hierarchy naming convention
        try {
            CodeSource src = Launcher.class.getProtectionDomain().getCodeSource();
            String path = "com/waverunnah/swg/harvesterdroid/data/raw/groups/";
            if (src != null) {
                ZipInputStream zip = new ZipInputStream(src.getLocation().openStream());
                ZipEntry entry = zip.getNextEntry();

                if (entry == null) {
                    File dir = new File(src.getLocation().getPath() + path);
                    File[] files = dir.listFiles();
                    for (File file : files != null ? files : new File[0]) {
                        populateResourceGroup(file.getAbsolutePath());
                    }
                } else {
                    while (entry != null) {
                        if (entry.getName() != null)
                            populateResourceGroup(entry.getName());
                        entry = zip.getNextEntry();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateResourceGroup(String file) {
        List<String> resourceGroup = new ArrayList<>();
        resourceGroup.add(file.substring(file.lastIndexOf("\\") + 1));
        try {
            try (Stream<String> stream = Files.lines(Paths.get(file))) {
                stream.forEach(resourceGroup::add);
            }
        } catch (IOException e) {
            ExceptionDialog.display(e);
        }
        legacy_resourceGroups.put(file.substring(file.lastIndexOf("\\") + 1), resourceGroup);
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

    public List<String> legacy_getResourceGroups(String group) {
        return legacy_resourceGroups.get(group);
    }

    private interface CsvParser {
        void parse(String[] line);
    }

    @SuppressWarnings("unused")
    private void _formatResourceTreeTable() {
        // Helper method for formating resources table from iff
        // Crmin	Crmax	Cdmin	Cdmax	Drmin	Drmax	Flmin	Flmax	Hrmin	Hrmax	Mamin	Mamax	Pemin	Pemax	Oqmin	Oqmax	Srmin	Srmax	Utmin	Utmax	Ermin	Ermax
        List<String> updatedLines = new ArrayList<>();

        readCsv("../data/raw/resource_tree.txt", line -> {
            String[] updatedLine = new String[33];
            updatedLine[32] = line[43];

            for (int i = 0; i < 10; i++) {
                updatedLine[i] = line[i];
            }

            int attr = 0;
            for (int i = 10; i < 21; i++) {
                attr++;
                String key = line[i];
                switch(key) {
                    case "res_cold_resist":
                        _doAttr(updatedLine, line, 10, attr, i);
                        break;
                    case "res_conductivity":
                        _doAttr(updatedLine, line, 12, attr, i);
                        break;
                    case "res_decay_resist":
                        _doAttr(updatedLine,line,14,attr, i);
                        break;
                    case "res_flavor":
                        _doAttr(updatedLine, line, 16, attr, i);
                        break;
                    case "res_heat_resist":
                        _doAttr(updatedLine, line, 18, attr, i);
                        break;
                    case "res_malleability":
                        _doAttr(updatedLine, line, 20, attr, i);
                        break;
                    case "res_potential_energy":
                        _doAttr(updatedLine, line, 22, attr, i);
                        break;
                    case "res_quality":
                        _doAttr(updatedLine, line, 24, attr, i);
                        break;
                    case "res_shock_resistance":
                        _doAttr(updatedLine, line, 26, attr, i);
                        break;
                    case "res_toughness":
                        _doAttr(updatedLine, line, 28, attr, i);
                        break;
                    case "entangle_resistance":
                        _doAttr(updatedLine, line, 30, attr, i);
                        break;
                    default:
                        break;
                }
            }

            for (int i = 10; i < 32; i++) {
                if (updatedLine[i] == null)
                    updatedLine[i] = "0";
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < updatedLine.length; i++) {
                builder.append(updatedLine[i]);
                if (i + 1 != updatedLine.length)
                    builder.append("\t");
            }
            builder.append("\n");
            updatedLines.add(builder.toString());
        });

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("updated_tree.txt"));
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void _doAttr(String[] updatedLine, String[] line, int index, int attr, int pos) {
        updatedLine[index] = line[pos + 10 + attr];
        updatedLine[index+1] = line[pos + 11 + attr];
    }
}
