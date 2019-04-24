package com.bateleur.app.view.list;

import com.bateleur.app.datatype.BAudio;

import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public abstract class BListOptionSetting extends BListOption {
	public BListOptionSetting(BListTab bListTab) {
		super(bListTab);
	}
	
	public abstract String getName();
	
	public abstract Region buildControl();
	
	@Override public Node buildForeground() {
		Button foreground = new Button();
		foreground.setMinHeight(bListTab.musicListController.master.settings.get(bListTab.musicListController.master.settings.UI_LISTOPT_VSIZE));
		foreground.setMaxHeight(bListTab.musicListController.master.settings.get(bListTab.musicListController.master.settings.UI_LISTOPT_VSIZE));
		foreground.setPrefWidth(10000);
		foreground.setPadding(Insets.EMPTY);
		foreground.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		foreground.setCache(true);
		foreground.setCacheHint(CacheHint.SPEED);
		
		GridPane innerGrid = new GridPane();
		innerGrid.getStyleClass().add("listOptionGrid");
		innerGrid.prefWidthProperty ().bind(foreground.widthProperty ());
		innerGrid.prefHeightProperty().bind(foreground.heightProperty());
		foreground.setGraphic(innerGrid);
		
		Label innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(getName());
		if (innerLabel.getText().equals(bListTab.musicListController.master.settings.AUDIO_META_TITLE.val)) {
			innerLabel.setOpacity(0.3);
		}
		innerLabel.prefHeightProperty().bind(foreground.heightProperty());
		innerGrid.add(innerLabel, 1, 0);
		
		Region innerControl = buildControl();
		innerControl.prefHeightProperty().bind(foreground.heightProperty());
		innerGrid.add(innerControl, 2, 0);
		
		innerGrid.getColumnConstraints().add(new ColumnConstraints(30             ));
		innerGrid.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		innerGrid.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		innerGrid.getColumnConstraints().add(new ColumnConstraints(30             ));
		
		return foreground;
	}
	
	@Override public void onSongChange(BAudio loadedAudio) {
	}
}
