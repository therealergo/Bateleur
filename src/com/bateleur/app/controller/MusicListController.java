package com.bateleur.app.controller;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;

public class MusicListController {
	private SettingsModel settings;
	private LibraryModel  library ;
	private PlaybackModel playback;
	private QueueModel    queue   ;
	
    public MusicListController(SettingsModel settings, LibraryModel library, PlaybackModel playback, QueueModel queue) {
    	this.settings = settings;
    	this.library  = library ;
    	this.playback = playback;
    	this.queue    = queue   ;
    }
    
    public void onAudioSelected(BAudio audio) {
    	queue.setQueue(library, audio);
    	playback.loadAudio(audio, settings.get(settings.FADE_TIME_USER));
    	playback.play(settings.get(settings.FADE_TIME_USER));
    }
}
