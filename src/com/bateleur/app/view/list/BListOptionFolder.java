package com.bateleur.app.view.list;

import java.util.List;

import com.bateleur.app.datatype.BAudio;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public abstract class BListOptionFolder extends BListOption {
	public final BListOptionFolder parentFolder;

	public BListOptionFolder(BListTab bListTab, BListOptionFolder parentFolder) {
		super(bListTab);
		
		this.parentFolder = parentFolder;
	}
	
	public abstract String getText();
	
	public abstract List<BListOption> listOptions();
	
	@Override public Button buildForeground() {
		Button foreground = new Button();
		foreground.setMinHeight(bListTab.musicListController.master.settings.get(bListTab.musicListController.master.settings.UI_LISTOPT_VSIZE));
		foreground.setMaxHeight(bListTab.musicListController.master.settings.get(bListTab.musicListController.master.settings.UI_LISTOPT_VSIZE));
		foreground.setPrefWidth(10000);
		foreground.setPadding(new Insets(0, 0, 0, 30));
		foreground.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		foreground.setCache(true);
		foreground.setCacheHint(CacheHint.SPEED);
		
		Label innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(getText());
		innerLabel.prefHeightProperty().bind(foreground.heightProperty());
		foreground.setAlignment(Pos.CENTER_LEFT);
		foreground.setGraphic(innerLabel);
		
		foreground.setOnMouseClicked((MouseEvent mce) -> {
			bListTab.rebuildList(this);
		});
		
		return foreground;
	}
	
	@Override public void onSongChange(BAudio loadedAudio) {
	}
}
