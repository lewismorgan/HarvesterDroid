package com.waverunnah.swg.harvesterdroid.gui.dialog;


import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.pegdown.PegDownProcessor;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Scanner;

public class AboutDialog extends Dialog {

	public AboutDialog() {
		super();
		try {
			init();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void init() throws FileNotFoundException {
		setTitle("About");
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

		PegDownProcessor processor = new PegDownProcessor();

		StringBuilder builder = new StringBuilder();

		File file = new File("README.md");
		Scanner scanner = new Scanner(file);

		builder.append(scanner.nextLine());
		while (scanner.hasNextLine())
			builder.append("\n").append(scanner.nextLine());

		htmlEditor.setHtmlText(processor.markdownToHtml(builder.toString()));

		getDialogPane().setContent(new AnchorPane(htmlEditor));
		getDialogPane().getButtonTypes().add(ButtonType.OK);
	}
}
