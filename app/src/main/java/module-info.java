module io.github.waverunner.harvesterdroid.app {
  requires org.apache.commons.lang3;
  requires guice;
  requires mvvmfx;
  requires mvvmfx.guice;
  requires javafx.graphics;
  requires javafx.controls;
  requires javafx.fxml;
  requires controlsfx;

  exports io.github.waverunner.harvesterdroid.app to
      javafx.controls, javafx.graphics;
}