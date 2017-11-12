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

package io.github.waverunner.harvesterdroid.app.ui.dialog.about;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

/**
 * Created by Waverunner on 3/27/2017.
 */
public class AboutDialogController extends VBox implements Initializable {
  @FXML
  private WebView webView;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      renderReadme();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void renderReadme() throws IOException {
/*    MutableDataSet options = new MutableDataSet();
    options.setFrom(ParserEmulationProfile.GITHUB_DOC);

    Parser parser = Parser.builder(options).build();
    HtmlRenderer renderer = HtmlRenderer.builder(options).build();

    com.vladsch.flexmark.ast.Node document = parser.parseReader(new InputStreamReader(getClass().getResourceAsStream("/about.md")));

    webView.getEngine().loadContent(renderer.render(document));*/
  }
}
