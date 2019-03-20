package com.bateleur.app.controller;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.PlaylistModel;
import com.bateleur.app.model.SettingsModel;

public class MusicListController {
	private SettingsModel settings;
	private PlaylistModel playlist;
	private LibraryModel  library ;
	private PlaybackModel playback;
	
    public MusicListController(SettingsModel settings, PlaylistModel playlist, LibraryModel library, PlaybackModel playback) {
    	this.settings = settings;
    	this.playlist = playlist;
    	this.library  = library ;
    	this.playback = playback;
    }
    
    public void onAudioSelected(BAudio audio) {
    	
    }
}
