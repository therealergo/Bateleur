package com.bateleur.app.view.list;

import com.bateleur.app.controller.MusicListController;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

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
		
		setText("Tracks");

		StackPane innerStack = new StackPane();
		this.setContent(innerStack);
		
		ScrollPane innerScroll = new ScrollPane();
		innerScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		innerScroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		innerStack.getChildren().add(innerScroll);
		
		Pane innerBorder = new Pane();
		innerBorder.getStyleClass().add("scroll-pane-border");
		innerBorder.setPickOnBounds(false);
		innerStack.getChildren().add(innerBorder);
		
		innerGrid = new GridPane();
		innerGrid.prefWidthProperty().bind(innerScroll.widthProperty());
		innerScroll.setContent(innerGrid);
		
		playback.addSongChangeHandler(() -> {
			innerGrid.getChildren().clear();
			library.forEach((BAudio audio) -> {
				int index = innerGrid.getChildren().size();
				innerGrid.add(new BListOption(this, index%2==0, audio, playback, settings), 0, index);
			});
			innerGrid.getColumnConstraints().clear();
			innerGrid.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
			innerGrid.getRowConstraints().clear();
			for (int i = 0; i<innerGrid.getChildren().size(); i++) {
				innerGrid.getRowConstraints().add(new RowConstraints(30.0));
			}
		});
	}
	
	public void onOptionSelected(BListOption bListOption) {
		musicListController.master.queue.setQueue(library, bListOption.audio);
		playback.loadAudio(bListOption.audio, settings.get(settings.FADE_TIME_USER));
		playback.play(settings.get(settings.FADE_TIME_USER));
	}
}
