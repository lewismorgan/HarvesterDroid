module io.github.waverunner.harvesterdroid.app {
  requires guice;
  requires mvvmfx;
  requires mvvmfx.guice;
  requires javafx.graphics;

  exports io.github.waverunner.harvesterdroid.app to
      javafx.controls, javafx.graphics;
}