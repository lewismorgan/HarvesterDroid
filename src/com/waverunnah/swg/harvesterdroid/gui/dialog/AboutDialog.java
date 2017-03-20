package com.waverunnah.swg.harvesterdroid.gui.dialog;


import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;

import java.io.FileReader;
import java.io.IOException;

public class AboutDialog extends Dialog {

	private HTMLEditor htmlView;

	public AboutDialog() {
		super();
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() throws IOException {
		setTitle("About");
		htmlView = createHtmlView();

		renderReadme();

		getDialogPane().setContent(new AnchorPane(htmlView));
		getDialogPane().getButtonTypes().add(ButtonType.OK);
	}

	private void renderReadme() throws IOException {
		MutableDataSet options = new MutableDataSet();
		options.setFrom(ParserEmulationProfile.GITHUB_DOC);

		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();

		com.vladsch.flexmark.ast.Node document = parser.parseReader(new FileReader("README.md"));
		htmlView.setHtmlText(renderer.render(document));
	}

	private HTMLEditor createHtmlView() {
		HTMLEditor htmlEditor = new HTMLEditor();

		Node[] nodes = htmlEditor.lookupAll(".tool-bar").toArray(new Node[0]);
		for(Node node : nodes)
		{
			node.setVisible(false);
			node.setManaged(false);
		}

		AnchorPane.setRightAnchor(htmlEditor, 0d);
		AnchorPane.setLeftAnchor(htmlEditor, 0d);
		AnchorPane.setTopAnchor(htmlEditor, 0d);
		AnchorPane.setBottomAnchor(htmlEditor, 0d);
		return htmlEditor;
	}
}
