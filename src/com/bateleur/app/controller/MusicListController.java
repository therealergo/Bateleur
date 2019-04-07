package com.bateleur.app.controller;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.list.BListTab;

import javafx.animation.KeyValue;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class MusicListController {
    @FXML private TabPane listTabPane;
	@FXML private AnchorPane musicListPane;

    public MasterController master;
    
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

	public void setMasterController(MasterController master) {
		this.master = master;
	}
    
    public void start() {
        listTabPane.getTabs().add(new BListTab(this, library, playback, settings));
        listTabPane.getTabs().add(new BListTab(this, library, playback, settings));

		// Build the vertical slide animation
		{
			// Slide the entire lower pane up/down (the height of the music list pane, to show/hide it) when doing the vertical slide animation
			// We don't need to worry about rebuilding for the music list pane's height because we already rebuild when the overall height changes
	    	master.verticalSlideAnimation.onRebuild(() -> {
	    		master.verticalSlideAnimation.addKeyValue(
	    			new KeyValue(
		    			master.lowerPane.translateYProperty(), 
		    			master.verticalSlideAnimation.rebuildIndex() * musicListPane.getHeight()
	    			)
	    		);
	    	});
		}
    }
    
    public void onAudioSelected(BAudio audio) {
    	queue.setQueue(library, audio);
    	playback.loadAudio(audio, settings.get(settings.FADE_TIME_USER));
    	playback.play(settings.get(settings.FADE_TIME_USER));
    }
}
