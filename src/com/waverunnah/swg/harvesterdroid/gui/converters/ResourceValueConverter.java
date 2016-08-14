package com.waverunnah.swg.harvesterdroid.gui.converters;

import javafx.util.StringConverter;

public class ResourceValueConverter extends StringConverter<Number> {
	@Override
	public String toString(Number object) {
		int value = object.intValue();
		return value == -1 ? "--" : String.valueOf(value);
	}

	@Override
	public Integer fromString(String string) {
		if (!"0123456789".contains(string))
			return -1;

		int value = Integer.valueOf(string);
		if (value > 1000 || value <= 0)
			return -1;

		return value;
	}
}
