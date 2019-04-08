package com.bateleur.app.controller;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.view.list.BListTab;

import javafx.animation.KeyValue;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class MusicListController {
	/** FXML-injected component references. */
    @FXML private TabPane listTabPane;
	@FXML private AnchorPane musicListPane;
	
	/** Reference to this MusicListController's MasterController. */
    public MasterController master;
    
	/**
	 * Perform any initialization required by this MusicListController.
	 * Should only be called by the MasterController when the master is ready and this MusicListController is to be initialized.
     * @param master This MusicListController's master controller.
	 */
    public void initialize(MasterController master) {
		this.master = master;
		
    	// Create each of the BListTabs that present music options to the user
        listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings));
        listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings));

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
    
    /**
     * Callback called from each of the BListTab instances when an audio option is clicked/selected.
     * This will update the Queue according to the current state of master.library, 
     * and set the supplied BAudio to begin playback.
     * @param audio The BAudio instance that was selected.
     */
    public void onAudioSelected(BAudio audio) {
    	master.queue.setQueue(master.library, audio);
    	master.playback.loadAudio(audio, master.settings.get(master.settings.FADE_TIME_USER));
    	master.playback.play(master.settings.get(master.settings.FADE_TIME_USER));
    }
}
