package io.github.waverunner.harvesterdroid.app.models;

import com.google.inject.Singleton;
import de.saxsys.mvvmfx.ViewModel;

/**
 * Created by lewis on 11/14/17.
 */
@Singleton
public class MainViewModel implements ViewModel {

  private boolean debugMode;

  public boolean isDebugMode() {
    return debugMode;
  }

  public void setDebugMode(boolean debugMode) {
    this.debugMode = debugMode;
  }
}
