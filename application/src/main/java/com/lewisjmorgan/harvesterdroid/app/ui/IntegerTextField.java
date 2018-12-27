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

package com.lewisjmorgan.harvesterdroid.app.ui;

import java.text.NumberFormat;
import java.text.ParseException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public final class IntegerTextField extends TextField {

  private final IntegerProperty value;
  private final float minValue;
  private final float maxValue;

  public IntegerTextField(int minValue, int maxValue, int initialValue) {
    if (minValue > maxValue) {
      throw new IllegalArgumentException(
          "IntegerTextField min value " + minValue + " greater than max value " + maxValue
      );
    }
    if (maxValue < minValue) {
      throw new IllegalArgumentException(
          "IntegerTextField max value " + minValue + " less than min value " + maxValue
      );
    }
    if (!((minValue <= initialValue) && (initialValue <= maxValue))) {
      throw new IllegalArgumentException(
          "IntegerTextField initialValue " + initialValue + " not between " + minValue + " and " + maxValue
      );
    }

    // initialize the field values.
    this.minValue = minValue;
    this.maxValue = maxValue;
    value = new SimpleIntegerProperty(initialValue);
    setText(String.valueOf(initialValue));

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

      int intValue = Integer.parseInt(newValue);
      if (integerTextField.minValue > intValue || intValue > integerTextField.maxValue) {
        textProperty().setValue(oldValue);
      }

      try {
        int finalValue = NumberFormat.getInstance().parse(newValue).intValue();
        value.set(finalValue);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    });
  }

  // expose an integer value property for the text field.
  public int getValue() {
    return value.getValue();
  }

  public void setValue(float newValue) {
    value.setValue(newValue);
  }

  public IntegerProperty valueProperty() {
    return value;
  }
}
