package com.waverunnah.swg.harvesterdroid.gui.dialog;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Waverunner on 3/27/2017
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
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.GITHUB_DOC);

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        com.vladsch.flexmark.ast.Node document = parser.parseReader(new InputStreamReader(getClass().getResourceAsStream("/about.md")));

        webView.getEngine().loadContent(renderer.render(document));
    }
}
