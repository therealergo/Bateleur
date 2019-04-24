package com.bateleur.app.view.list;

import com.bateleur.app.datatype.BAudio;

import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public abstract class BListOption {
	protected final BListTab bListTab;

	public BListOption(BListTab bListTab) {
		this.bListTab = bListTab;
	}
	
	public Node buildBackground(boolean isEvenIndex) {
		Pane background = new Pane();
		background.setMinHeight(bListTab.musicListController.master.settings.get(bListTab.musicListController.master.settings.UI_LIST_ENTRY_SIZE));
		background.setMaxHeight(bListTab.musicListController.master.settings.get(bListTab.musicListController.master.settings.UI_LIST_ENTRY_SIZE));
		background.setPrefWidth(10000);
		background.setPadding(Insets.EMPTY);
		background.setOpacity(0.6);
		background.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		background.setEffect(isEvenIndex ? bListTab.musicListController.master.playbackColorAnimation.lightingBG : bListTab.musicListController.master.playbackColorAnimation.lightingLI);
		background.setCache(true);
		background.setCacheHint(CacheHint.SPEED);
		return background;
	}
	
	public abstract Node buildForeground();
	
	public abstract void onSongChange(BAudio loadedAudio);
}
