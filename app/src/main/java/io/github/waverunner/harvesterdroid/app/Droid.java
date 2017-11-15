package io.github.waverunner.harvesterdroid.app;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import de.saxsys.mvvmfx.internal.viewloader.FxmlViewLoader;
import io.github.waverunner.harvesterdroid.app.models.MainViewModel;
import io.github.waverunner.harvesterdroid.app.views.MainView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Droid extends MvvmfxGuiceApplication {
  private static boolean debugArg = false;

  public static void main(String[] args) {
    if (args.length >= 1 && args[0].equals("d")) {
      debugArg = true;
    }

    launch(args);
  }

  @Override
  public void initMvvmfx() {
    System.out.println(debugArg);
  }

  @Override
  public void startMvvmfx(Stage stage) {
    ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();
    viewTuple.getViewModel().setDebugMode(debugArg);

    // TODO Setup View Model's

    Parent view = viewTuple.getView();
    Scene scene = view.getScene();

    stage.setScene(scene);
    stage.setTitle("Harvester Droid");
    stage.show();
  }

  @Override
  public void stopMvvmfx() {

  }
}
