package com.bateleur.app.view.list;

import com.bateleur.app.datatype.BAudio;

import javafx.scene.Node;

public abstract class BListOption {
	protected final BListTab bListTab;

	public BListOption(BListTab bListTab) {
		this.bListTab = bListTab;
	}
	
	public abstract Node buildBackground();
	
	public abstract Node buildForeground();
	
	public abstract void onSongChange(BAudio loadedAudio);
}
