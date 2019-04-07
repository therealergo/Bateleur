package com.bateleur.app.view.list;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class BListOption extends Button {
	private BListTab bListTab;
	
	public final BAudio audio;

	public BListOption(BListTab bListTab, boolean isEven, BAudio audio, PlaybackModel playback, SettingsModel settings) {
		this.bListTab = bListTab;
		
		this.audio = audio;
		
		setMaxHeight(30.0);
		setPrefWidth(10000);
		setPadding(Insets.EMPTY);
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		
		GridPane innerGrid = new GridPane();
		innerGrid.getStyleClass().add("listOptionGrid");
		innerGrid.prefWidthProperty ().bind(widthProperty ());
		innerGrid.prefHeightProperty().bind(heightProperty());
		innerGrid.setStyle(isEven ? "-fx-background-color: colorPlaybackLI;" : "-fx-background-color: colorPlaybackBG;");
		innerGrid.setOpacity(0.8); //TODO: Make this opacity only affect the background, not the child text labels
		setGraphic(innerGrid);
		
		Label innerLabel;
		
		innerLabel= new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(playback.getLoadedAudio().equals(audio) ? ">>" : "");
		innerLabel.prefHeightProperty().bind(heightProperty());
		innerGrid.add(innerLabel, 0, 0);
		
		innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(audio.get(settings.AUDIO_PROP_TITLE));
		innerLabel.prefHeightProperty().bind(heightProperty());
		innerGrid.add(innerLabel, 1, 0);
		
		innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(audio.get(settings.AUDIO_PROP_ARTIST));
		innerLabel.prefHeightProperty().bind(heightProperty());
		innerGrid.add(innerLabel, 2, 0);
		
		innerLabel = new Label();
		innerLabel.getStyleClass().add("listOptionLabel");
		innerLabel.setText(audio.get(settings.AUDIO_PROP_ALBUM));
		innerLabel.prefHeightProperty().bind(heightProperty());
		innerGrid.add(innerLabel, 3, 0);
		
		innerGrid.getColumnConstraints().add(new ColumnConstraints(30             ));
		innerGrid.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		innerGrid.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		innerGrid.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		
		setOnMouseClicked((MouseEvent mce) -> {
			onClicked();
		});
	}
	
	public void onClicked() {
		bListTab.onOptionSelected(this);
	}
}
