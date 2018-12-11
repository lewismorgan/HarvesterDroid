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

package com.lewisjmorgan.harvesterdroid;

import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LauncherPreloader extends Preloader {
  private ProgressBar bar;
  private Label statusLabel;
  private Stage stage;

  private Scene createPreloaderScene() {
    // TODO: Move to an FXML
    VBox container = new VBox();
    container.setAlignment(Pos.CENTER);
    container.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

    ImageView header = new ImageView(new Image(getClass().getResourceAsStream("/images/HarvesterDroid_Header.png")));
    header.setFitHeight(100);
    header.setFitWidth(350);

    statusLabel = new Label("Loading...");
    statusLabel.setPadding(new Insets(5, 0, 5, 0));

    bar = new ProgressBar();

    AnchorPane.setBottomAnchor(bar, 0d);
    AnchorPane.setTopAnchor(bar, 0d);
    AnchorPane.setLeftAnchor(bar, 0d);
    AnchorPane.setRightAnchor(bar, 0d);

    container.getChildren().addAll(header, statusLabel, new AnchorPane(bar));

    return new Scene(container);
  }

  public void start(Stage stage) throws Exception {
    this.stage = stage;
    stage.initStyle(StageStyle.UNDECORATED);
    stage.setAlwaysOnTop(true);
    stage.setScene(createPreloaderScene());

    stage.show();
    Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
    stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
    stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
  }

  @Override
  public void handleApplicationNotification(PreloaderNotification info) {
    if (!(info instanceof PreloaderStatusNotification)) {
      bar.setProgress(-1);
      statusLabel.setText("Loading...");
    } else {
      PreloaderStatusNotification notification = (PreloaderStatusNotification) info;
      bar.setProgress(notification.getProgress());
      statusLabel.setText(notification.getStatus());
    }
  }

  @Override
  public void handleStateChangeNotification(StateChangeNotification evt) {
    if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
      stage.hide();
    }
  }

}
