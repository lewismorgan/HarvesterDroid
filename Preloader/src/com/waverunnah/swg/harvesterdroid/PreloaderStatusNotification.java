package com.waverunnah.swg.harvesterdroid;

import javafx.application.Preloader;

public class PreloaderStatusNotification extends Preloader.ProgressNotification {
	private String status;

	public PreloaderStatusNotification(String status, double value) {
		super(value);
		this.status = status;
	}

	public String getStatus() {
		return status;
	}


}
