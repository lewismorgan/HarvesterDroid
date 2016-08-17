package com.waverunnah.swg.harvesterdroid.gui;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public final class IntegerTextField extends TextField {
	final private IntegerProperty value;
	final private float minValue;
	final private float maxValue;

	// expose an integer value property for the text field.
	public float  getValue()                 { return value.getValue(); }
	public void setValue(float newValue)     { value.setValue(newValue); }
	public IntegerProperty valueProperty() { return value; }

	public IntegerTextField(int minValue, int maxValue, int initialValue) {
		if (minValue > maxValue)
			throw new IllegalArgumentException(
					"IntegerTextField min value " + minValue + " greater than max value " + maxValue
			);
		if (maxValue < minValue)
			throw new IllegalArgumentException(
					"IntegerTextField max value " + minValue + " less than min value " + maxValue
			);
		if (!((minValue <= initialValue) && (initialValue <= maxValue)))
			throw new IllegalArgumentException(
					"IntegerTextField initialValue " + initialValue + " not between " + minValue + " and " + maxValue
			);

		// initialize the field values.
		this.minValue = minValue;
		this.maxValue = maxValue;
		value = new SimpleIntegerProperty(initialValue);
		setText(initialValue + "");

		final IntegerTextField integerTextField = this;

		// make sure the value property is clamped to the required range
		// and update the field's text to be in sync with the value.
		value.addListener((observableValue, oldValue, newValue) -> {
			if (newValue == null) {
				integerTextField.setText("");
			} else {
				if (newValue.intValue() < integerTextField.minValue) {
					value.setValue(integerTextField.minValue);
					return;
				}

				if (newValue.intValue() > integerTextField.maxValue) {
					value.setValue(integerTextField.maxValue);
					return;
				}

				if (!(newValue.intValue() == 0f && (textProperty().get() == null || "".equals(textProperty().get())))) {
					integerTextField.setText(newValue.toString());
				}
			}
		});

		// restrict key input to numerals.
		this.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
			if (!"0123456789".contains(keyEvent.getCharacter())) {
				keyEvent.consume();
			}
		});

		// ensure any entered values lie inside the required range.
		this.textProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue == null || "".equals(newValue)) {
				value.setValue(0f);
				return;
			}

			final float floatValue = Float.parseFloat(newValue);

			if (integerTextField.minValue > floatValue || floatValue > integerTextField.maxValue) {
				textProperty().setValue(oldValue);
			}

			value.set(Integer.parseInt(newValue));
		});
	}
}
