package com.bateleur.app.view.list;

import com.bateleur.app.controller.MusicListController;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

public class BListTab extends Tab {
	public final MusicListController musicListController;
	
	private LibraryModel library;
	private PlaybackModel playback;
	private SettingsModel settings;
	
	private GridPane innerGrid;

	public BListTab(MusicListController musicListController, LibraryModel library, PlaybackModel playback, SettingsModel settings) {
		this.musicListController = musicListController;
		
		this.library = library;
		this.playback = playback;
		this.settings = settings;
		
		ScrollPane innerScroll = new ScrollPane();
		innerScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setContent(innerScroll);
		innerScroll.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		
		innerGrid = new GridPane();
		innerGrid.prefWidthProperty().bind(innerScroll.widthProperty());
		innerScroll.setContent(innerGrid);
		
		innerScroll.widthProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
			// This must be called in a 'runLater(...)' to ensure that the list does not flicker while resizing
			Platform.runLater( () -> rebuildList(new_val.doubleValue()) );
		});
		
		playback.addSongChangeHandler(() -> {
			rebuildList(innerScroll.getWidth());
		});
	}
	
	public void rebuildList(double areaWidth) {
		/*
		int numCols = Math.max((int)Math.floor(areaWidth / 107.0), 1);
		innerGrid.getChildren().clear();
		library.forEach((BAudio audio) -> {
			int index = innerGrid.getChildren().size();
			innerGrid.add(new BListOption(audio, playback, settings), index%numCols, index/numCols);
		});
		innerGrid.getColumnConstraints().clear();
		for (int i = 0; i<numCols; i++) {
			innerGrid.getColumnConstraints().add(new ColumnConstraints(areaWidth/numCols));
		}
		innerGrid.getRowConstraints().clear();
		for (int i = 0; i<Math.floorDiv(innerGrid.getChildren().size(), numCols)+1; i++) {
			innerGrid.getRowConstraints().add(new RowConstraints(areaWidth/numCols));
		}
		*/
		innerGrid.getChildren().clear();
		library.forEach((BAudio audio) -> {
			int index = innerGrid.getChildren().size();
			innerGrid.add(new BListOption(this, index%2==0, audio, playback, settings), 0, index);
		});
		innerGrid.getColumnConstraints().clear();
		innerGrid.getColumnConstraints().add(new ColumnConstraints(areaWidth));
		innerGrid.getRowConstraints().clear();
		for (int i = 0; i<innerGrid.getChildren().size(); i++) {
			innerGrid.getRowConstraints().add(new RowConstraints(30.0));
		}
	}
	
	public void onOptionSelected(BListOption bListOption) {
		musicListController.master.queue.setQueue(library, bListOption.audio);
		playback.loadAudio(bListOption.audio, settings.get(settings.FADE_TIME_USER));
		playback.play(settings.get(settings.FADE_TIME_USER));
	}
}
