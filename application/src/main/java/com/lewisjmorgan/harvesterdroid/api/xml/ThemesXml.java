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

package com.lewisjmorgan.harvesterdroid.api.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Waverunner on 4/6/2017.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "themes")
public class ThemesXml {

  @XmlElement(name = "theme")
  private List<Theme> themes;

  public List<Theme> getThemes() {
    return themes;
  }

  public void setThemes(List<Theme> themes) {
    this.themes = themes;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = "theme")
  public static class Theme {

    @XmlAttribute(name = "name")
    public String name;
    @XmlAttribute(name = "path")
    public String path;

    public Theme() {
    }

    public Theme(String name, String path) {
      this.name = name;
      this.path = path;
    }
  }
}
