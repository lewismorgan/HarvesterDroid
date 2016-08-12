package com.waverunnah.swg.harvesterdroid;

import com.waverunnah.swg.harvesterdroid.data.resources.GalaxyResource;
import com.waverunnah.swg.harvesterdroid.data.schematics.Schematic;
import com.waverunnah.swg.harvesterdroid.xml.app.SchematicsXml;
import com.waverunnah.swg.harvesterdroid.utils.Downloader;
import com.waverunnah.swg.harvesterdroid.xml.galacticharvester.HarvesterResourcesXml;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.util.List;

public class HarvesterDroid extends Application {
	private static HarvesterDroid instance;

	private static Stage stage;

	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private HarvesterResourcesXml harvesterResourcesXml;
	private SchematicsXml schematicsXml;

	@Override
	public void init() throws Exception {
		// TODO Loading Screen w/ proper initializations
		instance = this;

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		Downloader.downloadXmls();

		harvesterResourcesXml = new HarvesterResourcesXml(documentBuilder);
		harvesterResourcesXml.load(new FileInputStream("./data/current48.xml"));

		schematicsXml = new SchematicsXml(documentBuilder);
		schematicsXml.load(new FileInputStream("./data/user/schematics.xml"));
	}

	@Override
    public void start(Stage primaryStage) throws Exception {
	    stage = primaryStage;


        Parent root = FXMLLoader.load(getClass().getResource("gui/main.fxml"));
        primaryStage.setTitle("Harvester Droid");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

	public static Stage getStage() {
		return stage;
	}

	public static List<GalaxyResource> getGalaxyResources() {
		// TODO Abstraction for other data sources
		return instance.harvesterResourcesXml.getGalaxyResourceList();
	}

	public static List<Schematic> getSchematics() {
		// this xml is specific to HarvesterDroid, schema will never change
		return instance.schematicsXml.getSchematicsList();
	}

	public static List<String> getProfessions() {
		return instance.schematicsXml.getProfessionList();
	}
}
