package com.bateleur.app.view.list;

import java.util.ArrayList;

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
	private final ArrayList<BListOption> options;
	
	public final MusicListController musicListController;
	
	private LibraryModel library;
	private PlaybackModel playback;
	private SettingsModel settings;

	public BListTab(MusicListController musicListController, LibraryModel library, PlaybackModel playback, SettingsModel settings) {
		this.musicListController = musicListController;
		
		this.library = library;
		this.playback = playback;
		this.settings = settings;
		
		setText("Tracks");
		
		StackPane innerStack = new StackPane();
		this.setContent(innerStack);
		
		ScrollPane innerScrollBackground = new ScrollPane();
		innerScrollBackground.setHbarPolicy(ScrollBarPolicy.NEVER);
		innerScrollBackground.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		innerStack.getChildren().add(innerScrollBackground);
		
		Pane innerBorderBack = new Pane();
		innerBorderBack.getStyleClass().add("scroll-pane-border");
		innerBorderBack.getStyleClass().add("scroll-pane-border-back");
		innerBorderBack.setMouseTransparent(true);
		innerBorderBack.setEffect(musicListController.master.playbackColorAnimation.lightingBG);
		innerStack.getChildren().add(innerBorderBack);
		
		ScrollPane innerScrollForeground = new ScrollPane();
		innerScrollForeground.setHbarPolicy(ScrollBarPolicy.NEVER);
		innerScrollForeground.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		innerStack.getChildren().add(innerScrollForeground);
		innerScrollForeground.setEffect(musicListController.master.playbackColorAnimation.lightingFG);
		
		Pane innerBorderFore = new Pane();
		innerBorderFore.getStyleClass().add("scroll-pane-border");
		innerBorderFore.getStyleClass().add("scroll-pane-border-fore");
		innerBorderFore.setMouseTransparent(true);
		innerBorderFore.setEffect(musicListController.master.playbackColorAnimation.lightingBG);
		innerStack.getChildren().add(innerBorderFore);
		
		innerScrollBackground.vvalueProperty().bind(innerScrollForeground.vvalueProperty());
		
		GridPane innerGridBackground = new GridPane();
		innerGridBackground.prefWidthProperty().bind(innerScrollBackground.widthProperty());
		innerScrollBackground.setContent(innerGridBackground);
		
		GridPane innerGridForeground = new GridPane();
		innerGridForeground.prefWidthProperty().bind(innerScrollForeground.widthProperty());
		innerScrollForeground.setContent(innerGridForeground);
		
		options = new ArrayList<BListOption>();
		library.reset();
		library.sortBy((BAudio a0, BAudio a1) -> {
			return a0.get(settings.AUDIO_PROP_TITLE).compareTo(a1.get(settings.AUDIO_PROP_TITLE));
		});
		library.forEach((BAudio audio) -> {
			int index = options.size();
			options.add(new BListOptionFile(this, index%2==0, audio, playback, settings));
		});
		
		innerGridBackground.getChildren().clear();
		for (int i = 0; i<options.size(); i++) {
			innerGridBackground.add(options.get(i).buildBackground(), 0, i);
		}
		innerGridBackground.getColumnConstraints().clear();
		innerGridBackground.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		innerGridBackground.getRowConstraints().clear();
		for (int i = 0; i<innerGridBackground.getChildren().size(); i++) {
			innerGridBackground.getRowConstraints().add(new RowConstraints(30.0));
		}
		
		innerGridForeground.getChildren().clear();
		for (int i = 0; i<options.size(); i++) {
			innerGridForeground.add(options.get(i).buildForeground(), 0, i);
		}
		innerGridForeground.getColumnConstraints().clear();
		innerGridForeground.getColumnConstraints().add(new ColumnConstraints(0, 10000, 10000));
		innerGridForeground.getRowConstraints().clear();
		for (int i = 0; i<innerGridForeground.getChildren().size(); i++) {
			innerGridForeground.getRowConstraints().add(new RowConstraints(30.0));
		}
		
		playback.addSongChangeHandler(() -> {
			for (int i = 0; i<options.size(); i++) {
				options.get(i).onSongChange(playback.getLoadedAudio());
			}
		});
	}
	
	public void onOptionSelected(BListOptionFile bListOption) {
		library.reset();
		library.sortBy((BAudio a0, BAudio a1) -> {
			return a0.get(settings.AUDIO_PROP_TITLE).compareTo(a1.get(settings.AUDIO_PROP_TITLE));
		});
		musicListController.master.queue.setQueue(library, bListOption.audio);
		playback.loadAudio(bListOption.audio, settings.get(settings.FADE_TIME_USER));
		playback.play(settings.get(settings.FADE_TIME_USER));
	}
}
