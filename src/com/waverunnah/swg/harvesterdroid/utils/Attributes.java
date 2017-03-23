package com.waverunnah.swg.harvesterdroid.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attributes {
	private static Map<String, String> primary = new HashMap<String, String>() {{
		put("entangle_resistance", "ER");
		put("cold_resistance", "CR");
		put("conductivity", "CD");
		put("decay_resistance", "DR");
		put("flavor", "FL");
		put("heat_resistance", "HR");
		put("malleability", "MA");
		put("potential_energy", "PE");
		put("overall_quality", "OQ");
		put("shock_resistance", "SR");
		put("unit_toughness", "UT");
	}};

	private static Map<String, String> secondary = new HashMap<String, String>() {{
		put("ER", "entangle_resistance");
		put("CR", "cold_resistance");
		put("CD", "conductivity");
		put("DR", "decay_resistance");
		put("FL", "flavor");
		put("HR", "heat_resistance");
		put("MA", "malleability");
		put("PE", "potential_energy");
		put("OQ", "overall_quality");
		put("SR", "shock_resistance");
		put("UT", "unit_toughness");
	}};

	public static void forEach(AttributeListCallback attributeListCallback) {
		primary.forEach(attributeListCallback::perform);
	}

	public static void forEachReverseLookup(AttributeListCallback attributeListCallback) {
		secondary.forEach((secondary, primary) -> attributeListCallback.perform(primary, secondary));
	}

	public static String getLocalizedName(String attribute) {
	    switch (attribute) {
            case "entangle_resistance": return "Entangle Resistance";
            case "cold_resistance": return "Cold Resistance";
            case "conductivity": return "Conductivity";
            case "decay_resistance": return "Decay Resistance";
            case "flavor": return "Flavor";
            case "heat_resistance": return "Heat Resistance";
            case "malleability": return "Malleability";
            case "potential_energy": return "Potential Energy";
            case "overall_quality": return "Overall Quality";
            case "shock_resistance": return "Shock Resistance";
            case "unit_toughness": return "Unit Toughness";
            default: return null;
        }
    }

	public static List<String> get() {
		return new ArrayList<>(primary.keySet());
	}

	public static int size() {
		return primary.size();
	}

	public static String getFullName(String abbreviation) {
		return secondary.get(abbreviation);
	}

	public static String getAbbreviation(String attribute) {
		return primary.get(attribute);
	}

	public interface  AttributeListCallback {

		void perform(String primary, String secondary);
	}
}
