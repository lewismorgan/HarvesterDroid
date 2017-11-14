/**
 * Created by lewis on 11/12/17
 */
module io.github.waverunner.harvesterdroid.app {
  requires io.github.waverunner.harvesterdroid.api;
  requires io.github.waverunner.harvesterdroid.launcher;
  requires javafx.graphics;
  requires mvvmfx.easydi;
  requires log4j.api;
  requires mvvmfx;
  requires easy.di;
  requires io.github.waverunner.harvesterdroid.trackers.galaxyharvester;
  requires javafx.controls;
  requires log4j.core;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires javafx.fxml;
  requires javafx.web;
  requires com.fasterxml.jackson.annotation;
  requires controlsfx;
  requires java.xml.bind;
  exports io.github.waverunner.harvesterdroid.app
      to javafx.graphics, javafx.fxml;
}