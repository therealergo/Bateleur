package com.bateleur.app.view.list;

import com.bateleur.app.datatype.BAudio;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class BListOptionFile extends BListOption {
	public final BAudio audio;
	
	private Label isPlayingLabel;

	public BListOptionFile(BListTab bListTab, BAudio audio) {
		super(bListTab);
		
		this.audio = audio;
	}
	
	@Override public Node buildForeground() {
		Button foreground = new Button();
		foreground.setMinHeight(bListTab.musicListController.master.settings.get(bListTab.musicListController.master.settings.UI_LISTOPT_VSIZE));
		foreground.setMaxHeight(bListTab.musicListController.master.settings.get(bListTab.musicListController.master.settings.UI_LISTOPT_VSIZE));
		foreground.setPrefWidth(10000);
		foreground.setPadding(Insets.EMPTY);
		foreground.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		
		GridPane innerGrid = new GridPane();
		innerGrid.getStyleClass().add("listOptionGrid");
		innerGrid.prefWidthProperty ().bind(foreground.widthProperty ());
		innerGrid.prefHeightProperty().bind(foreground.heightProperty());
		foreground.setGraphic(innerGrid);
		
		isPlayingLabel = new Label();
		isPlayingLabel.getStyleClass().add("listOptionLabel");
		isPlayingLabel.prefHeightProperty().bind(foreground.heightProperty());
		isPlayingLabel.setText(audio.equals(bListTab.musicListController.master.playback.getLoadedAudio()) ? ">>" : "");
		innerGrid.add(isPlayingLabel, 0, 0);
		
		Label innerLabel;
		
		innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(audio.get(bListTab.musicListController.master.settings.AUDIO_PROP_TITLE));
		if (innerLabel.getText().equals(bListTab.musicListController.master.settings.AUDIO_PROP_TITLE.val)) {
			innerLabel.setOpacity(0.3);
		}
		innerLabel.prefHeightProperty().bind(foreground.heightProperty());
		innerGrid.add(innerLabel, 1, 0);
		
		innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(audio.get(bListTab.musicListController.master.settings.AUDIO_PROP_ARTIST));
		if (innerLabel.getText().equals(bListTab.musicListController.master.settings.AUDIO_PROP_ARTIST.val)) {
			innerLabel.setOpacity(0.3);
		}
		innerLabel.prefHeightProperty().bind(foreground.heightProperty());
		innerGrid.add(innerLabel, 2, 0);
		
		innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(audio.get(bListTab.musicListController.master.settings.AUDIO_PROP_ALBUM));
		if (innerLabel.getText().equals(bListTab.musicListController.master.settings.AUDIO_PROP_ALBUM.val)) {
			innerLabel.setOpacity(0.3);
		}
		innerLabel.prefHeightProperty().bind(foreground.heightProperty());
		innerGrid.add(innerLabel, 3, 0);
		
		innerGrid.getColumnConstraints().add(new ColumnConstraints(30             ));
		innerGrid.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		innerGrid.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		innerGrid.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		
		foreground.setOnMouseClicked((MouseEvent mce) -> {
			bListTab.onOptionSelected(this);
		});
		
		return foreground;
	}
	
	@Override public void onSongChange(BAudio loadedAudio) {
		isPlayingLabel.setText(audio.equals(loadedAudio) ? ">>" : "");
	}
}
