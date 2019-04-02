package com.bateleur.app.controller;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.list.BListTab;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MusicListController {
	private SettingsModel settings;
	private LibraryModel  library ;
	private PlaybackModel playback;
	private QueueModel    queue   ;
    
    @FXML
    private TabPane listTabPane;
	
    public MusicListController(SettingsModel settings, LibraryModel library, PlaybackModel playback, QueueModel queue) {
    	this.settings = settings;
    	this.library  = library ;
    	this.playback = playback;
    	this.queue    = queue   ;
    }
    
    @FXML
    public void initialize() {
        listTabPane.getTabs().add(new BListTab(library, playback, settings));
    }
    
    public void onAudioSelected(BAudio audio) {
    	queue.setQueue(library, audio);
    	playback.loadAudio(audio, settings.get(settings.FADE_TIME_USER));
    	playback.play(settings.get(settings.FADE_TIME_USER));
    }
}
