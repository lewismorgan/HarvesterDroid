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

package io.github.waverunner.harvesterdroid.app;

import io.github.waverunner.harvesterdroid.api.resource.ResourceType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Waverunner on 3/23/2017.
 */
public class HarvesterDroidData {
  public static final String ROOT_DIR = System.getProperty("user.home") + "/.harvesterdroid";
  public static final String JSON_SCHEMATICS = ROOT_DIR + "/schematics.json";
  public static final String XML_INVENTORY = ROOT_DIR + "/inventory.json";
  public static final String XML_THEMES = ROOT_DIR + "/themes.xml";

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
    // TODO Refactor resource group handling, this should be handled by the tracker with HD just have a general knowledge
    readCsv("/data/resource_groups.txt", line -> {
      if (resourceGroups.containsKey(line[1])) {
        resourceGroups.get(line[1]).add(line[0]);
      } else {
        resourceGroups.put(line[1], new ArrayList<>(Collections.singletonList(line[0])));
      }
    });

  }

  public void populateMinMax(ResourceType resourceType) {
    if (!resourceTypeMap.containsKey(resourceType.getId())) {
      return;
    }

    resourceType.setMinMaxMap(resourceTypeMap.get(resourceType.getId()).getMinMaxMap());
  }

  private void loadResourceTypes() {
    readCsv("/data/resource_tree.txt", line -> {
      ResourceType type = new ResourceType();
      type.setId(line[0]);
      type.setName(line[1]);
      /*type.setRecylced(Boolean.valueOf(line[2]));*/
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
        if (row.length > 1) {
          parser.parse(row);
        }
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

  @SuppressWarnings("unused")
  private void formatResourceTreeTable() {
    // Helper method for formating resources table from iff
    List<String> updatedLines = new ArrayList<>();
    updatedLines.add("Id\tName\tRecycled\tCr min\tCr max\tCd min\tCd max\tDr min\tDr max\tFl min\tFl max\tHr min\t"
        + "Hr max\tMa min\tMa max\tPe min\tPe max\tOq min\tOq max\tSr min\tSr max\tUt min\tUt max\tEr min\tEr max\tContainer\n");
    AtomicInteger lineNum = new AtomicInteger(0);
    readCsv("resource_tree.txt", line -> {
      if (lineNum.getAndIncrement() < 2) {
        return;
      }

      if (line.length != 50) {
        String[] buffer = new String[50];
        for (int i = 0; i < buffer.length; i++) {
          if (i < line.length) {
            buffer[i] = line[i];
          } else {
            buffer[i] = "";
          }
        }
        line = buffer;
      }

      String[] updatedLine = new String[26];
      updatedLine[0] = line[1]; // id
      updatedLine[2] = line[14]; // recycled
      if (line.length == 50) {
        updatedLine[25] = line[49]; // container
      } else {
        updatedLine[25] = "";
      }

      for (int i = 2; i < 10; i++) {
        if (!line[i].isEmpty()) {
          updatedLine[1] = line[i]; // name
          break;
        }
      }

      int attr = 0;
      for (int i = 16; i < 27; i++) {
        attr++;
        String key = line[i];
        switch (key) {
          case "res_cold_resist":
            doAttr(updatedLine, line, 3, attr, i);
            break;
          case "res_conductivity":
            doAttr(updatedLine, line, 5, attr, i);
            break;
          case "res_decay_resist":
            doAttr(updatedLine, line, 7, attr, i);
            break;
          case "res_flavor":
            doAttr(updatedLine, line, 9, attr, i);
            break;
          case "res_heat_resist":
            doAttr(updatedLine, line, 11, attr, i);
            break;
          case "res_malleability":
            doAttr(updatedLine, line, 13, attr, i);
            break;
          case "res_potential_energy":
            doAttr(updatedLine, line, 15, attr, i);
            break;
          case "res_quality":
            doAttr(updatedLine, line, 17, attr, i);
            break;
          case "res_shock_resistance":
            doAttr(updatedLine, line, 19, attr, i);
            break;
          case "res_toughness":
            doAttr(updatedLine, line, 21, attr, i);
            break;
          case "entangle_resistance":
            doAttr(updatedLine, line, 23, attr, i);
            break;
          default:
            if (!key.isEmpty()) {
              System.out.println("Unknown key " + key);
            }
            break;
        }
      }

      for (int i = 3; i < 25; i++) {
        if (updatedLine[i] == null || updatedLine[i].isEmpty()) {
          updatedLine[i] = "0";
        }
      }

      StringBuilder builder = getStringBuilder(updatedLine);

      if (updatedLine[0].equals("crystalline_mustafar_1")) {
        updatedLine[0] = "crystalline_mustafar";
        updatedLine[1] = "Mustafarian Crystalline Gemstone";
        updatedLines.add(getStringBuilder(updatedLine).toString());
      } else if (updatedLine[0].equals("armophous_mustafar_1")) {
        updatedLine[0] = "armophous_mustafar";
        updatedLine[1] = "Mustafarian Amorphous Gemstone";
        updatedLines.add(getStringBuilder(updatedLine).toString());
      } else if (updatedLine[0].startsWith("meat_reptilian_")) {
        updatedLine[0] = updatedLine[0].replace("meat_reptilian_", "meat_reptillian_");
        builder = getStringBuilder(updatedLine);
      }

      updatedLines.add(builder.toString());
    });

    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("resources/data/resource_tree.txt"));
      for (String updatedLine : updatedLines) {
        writer.write(updatedLine);
      }
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private StringBuilder getStringBuilder(String[] updatedLine) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < updatedLine.length; i++) {
      builder.append(updatedLine[i]);
      if (i + 1 != updatedLine.length) {
        builder.append("\t");
      }
    }
    builder.append("\n");
    return builder;
  }

  private void doAttr(String[] updatedLine, String[] line, int index, int attr, int pos) {
    updatedLine[index] = line[pos + 10 + attr];
    updatedLine[index + 1] = line[pos + 11 + attr];
  }

  private interface CsvParser {
    void parse(String[] line);
  }
}
