package com.waverunnah.swg.harvesterdroid.gui;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public final class FloatTextField extends TextField {
	final private FloatProperty value;
	final private float minValue;
	final private float maxValue;

	// expose an integer value property for the text field.
	public float  getValue()                 { return value.getValue(); }
	public void setValue(float newValue)     { value.setValue(newValue); }
	public FloatProperty valueProperty() { return value; }

	public FloatTextField(float minValue, float maxValue, float initialValue) {
		if (minValue > maxValue)
			throw new IllegalArgumentException(
					"FloatTextField min value " + minValue + " greater than max value " + maxValue
			);
		if (maxValue < minValue)
			throw new IllegalArgumentException(
					"FloatTextField max value " + minValue + " less than min value " + maxValue
			);
		if (!((minValue <= initialValue) && (initialValue <= maxValue)))
			throw new IllegalArgumentException(
					"FloatTextField initialValue " + initialValue + " not between " + minValue + " and " + maxValue
			);

		// initialize the field values.
		this.minValue = minValue;
		this.maxValue = maxValue;
		value = new SimpleFloatProperty(initialValue);
		setText(initialValue + "");

		final FloatTextField floatTextField = this;

		// make sure the value property is clamped to the required range
		// and update the field's text to be in sync with the value.
		value.addListener((observableValue, oldValue, newValue) -> {
			if (newValue == null) {
				floatTextField.setText("");
			} else {
				if (newValue.intValue() < floatTextField.minValue) {
					value.setValue(floatTextField.minValue);
					return;
				}

				if (newValue.intValue() > floatTextField.maxValue) {
					value.setValue(floatTextField.maxValue);
					return;
				}

				if (!(newValue.intValue() == 0f && (textProperty().get() == null || "".equals(textProperty().get())))) {
					floatTextField.setText(newValue.toString());
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

			if (floatTextField.minValue > floatValue || floatValue > floatTextField.maxValue) {
				textProperty().setValue(oldValue);
			}

			value.set(Float.parseFloat(textProperty().get()));
		});
	}
}
