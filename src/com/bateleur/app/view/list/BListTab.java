package com.bateleur.app.view.list;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bateleur.app.controller.MusicListController;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.MainException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class BListTab extends Tab {
	private final ArrayList<BListOption> options;
	private final GridPane innerGridBackground;
	private final GridPane innerGridForeground;
	
	public final MusicListController musicListController;
	
	private LibraryModel library;
	private PlaybackModel playback;
	private SettingsModel settings;

	public final BooleanProperty isFiltered;

	private BListOptionFolder currentFolder;
	private BListOptionFolder parentFolder;

	public BListTab(MusicListController musicListController, LibraryModel library, PlaybackModel playback, SettingsModel settings, Class<? extends BListOptionFolder> baseFolderClass) {
		this.musicListController = musicListController;
		
		this.library = library;
		this.playback = playback;
		this.settings = settings;
		
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
		
		options = new ArrayList<BListOption>();
		
		innerGridBackground = new GridPane();
		innerGridBackground.prefWidthProperty().bind(innerScrollBackground.widthProperty());
		innerScrollBackground.setContent(innerGridBackground);
		
		innerGridForeground = new GridPane();
		innerGridForeground.prefWidthProperty().bind(innerScrollForeground.widthProperty());
		innerScrollForeground.setContent(innerGridForeground);
		
		this.isFiltered = new SimpleBooleanProperty();
		
		try {
			currentFolder = baseFolderClass.getConstructor(BListTab.class, BListOptionFolder.class).newInstance(this, null);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new MainException(BListTab.class, "Cannot instantiate base folder class: " + baseFolderClass + "!", e);
		}
		setText(currentFolder.getText());
		rebuildList(currentFolder);
		library.updateFinishEvent.addListener(() -> {
			rebuildList(currentFolder);
		});
		musicListController.searchChangeEvent.addListener(() -> {
			if (isSelected() || isFiltered.get()) {
				isFiltered.set(isSelected());
				rebuildList(currentFolder);
			}
		});
		this.parentFolder = null;
		
		playback.addSongChangeHandler(() -> {
			for (int i = 0; i<options.size(); i++) {
				options.get(i).onSongChange(library.getByReference(playback.getLoadedAudio()));
			}
		});
	}

	public void onOptionSelected(BListOptionAudio bListOption) {
		library.reset();
		library.sortBy((BAudio a0, BAudio a1) -> {
			return a0.get(settings.AUDIO_PROP_TITLE).compareTo(a1.get(settings.AUDIO_PROP_TITLE));
		});
		
		List<BAudio> audioList = new LinkedList<BAudio>();
		options.forEach((BListOption option) -> {
			if (option instanceof BListOptionAudio) {
				audioList.add(((BListOptionAudio)option).audio);
			}
		});
		musicListController.master.queue.setQueue(audioList, bListOption.audio);
		
		playback.loadAudio(bListOption.audio, settings.get(settings.FADE_TIME_USER));
		playback.play(settings.get(settings.FADE_TIME_USER));
	}

	public void rebuildList(BListOptionFolder folder) {
		isFiltered.set(isFiltered.get() && folder.equals(currentFolder));
		
		currentFolder = folder;
		parentFolder = folder.parentFolder;
		
		options.clear();
		options.addAll(folder.listOptions());
		if (isFiltered.get()) {
			options.removeIf(musicListController.getSearchBarFilter().negate());
		}
		
		innerGridBackground.getChildren().clear();
		for (int i = 0; i<options.size(); i++) {
			innerGridBackground.add(options.get(i).buildBackground(i%2==0), 0, i);
		}
		
		innerGridForeground.getChildren().clear();
		for (int i = 0; i<options.size(); i++) {
			innerGridForeground.add(options.get(i).buildForeground(), 0, i);
		}
	}
	
	public void selectParent() {
		if (parentFolder != null) {
			rebuildList(parentFolder);
		}
	}
}
