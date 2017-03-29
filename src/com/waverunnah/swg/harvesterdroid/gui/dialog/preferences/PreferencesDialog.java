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

package com.waverunnah.swg.harvesterdroid.gui.dialog.preferences;

import com.waverunnah.swg.harvesterdroid.gui.dialog.BaseDialog;
import javafx.scene.control.ButtonType;

import java.util.Properties;

/**
 * Created by Waverunner on 3/29/2017
 */
public class PreferencesDialog extends BaseDialog<Properties> {

    public PreferencesDialog() {
        super("HarvesterDroid Preferences");
    }

    @Override
    protected ButtonType[] getButtonTypes() {
        return new ButtonType[]{
                ButtonType.APPLY,
                ButtonType.CLOSE
        };
    }

    @Override
    protected void createDialog() {
        setResultConverter(buttonType -> {
            if (buttonType != ButtonType.APPLY)
                return null;

            return new Properties();
        });
    }
}
