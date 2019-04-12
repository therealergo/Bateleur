package com.bateleur.app.view.list;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;

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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BListOptionFile extends BListOption {
	private boolean isEven;
	
	public final BAudio audio;
	
	private Label isPlayingLabel;

	public BListOptionFile(BListTab bListTab, boolean isEven, BAudio audio, PlaybackModel playback, SettingsModel settings) {
		super(bListTab);
		
		this.isEven = isEven;
		
		this.audio = audio;
		
	}
	
	@Override public Node buildBackground() {
		Pane background = new Pane();
		background.setMaxHeight(30.0);
		background.setPrefWidth(10000);
		background.setPadding(Insets.EMPTY);
		background.setOpacity(0.7);
		background.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		background.setEffect(isEven ? bListTab.musicListController.master.playbackColorAnimation.lightingBG : bListTab.musicListController.master.playbackColorAnimation.lightingLI);
		
		return background;
	}
	
	@Override public Node buildForeground() {
		Button foreground = new Button();
		foreground.setMaxHeight(30.0);
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
		innerGrid.add(isPlayingLabel, 0, 0);
		
		Label innerLabel;
		
		innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(audio.get(bListTab.musicListController.master.settings.AUDIO_PROP_TITLE));
		innerLabel.prefHeightProperty().bind(foreground.heightProperty());
		innerGrid.add(innerLabel, 1, 0);
		
		innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(audio.get(bListTab.musicListController.master.settings.AUDIO_PROP_ARTIST));
		innerLabel.prefHeightProperty().bind(foreground.heightProperty());
		innerGrid.add(innerLabel, 2, 0);
		
		innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(audio.get(bListTab.musicListController.master.settings.AUDIO_PROP_ALBUM));
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
		isPlayingLabel.setText(loadedAudio.equals(audio) ? ">>" : "");
	}
}
