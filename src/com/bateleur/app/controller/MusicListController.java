package com.bateleur.app.controller;

import java.util.Set;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.view.list.BListOptionFolderByType.BListOptionFolderByAlbum;
import com.bateleur.app.view.list.BListOptionFolderByType.BListOptionFolderByArtist;
import com.bateleur.app.view.list.BListOptionFolderQueue;
import com.bateleur.app.view.list.BListOptionFolderTracks;
import com.bateleur.app.view.list.BListOptionFolder_ByPath;
import com.bateleur.app.view.list.BListTab;

import javafx.animation.KeyValue;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MusicListController {
	/** FXML-injected component references. */
	@FXML private TabPane listTabPane;
	@FXML private AnchorPane musicListPane;
	
	/** Reference to this MusicListController's MasterController. */
	public MasterController master;
	
	/** Boolean used to ensure that the listTabPane is only initialized once. */
	private boolean listTabPaneHasSetup;
	
	/**
	 * Perform any initialization required by this MusicListController.
	 * Should only be called by the MasterController when the master is ready and this MusicListController is to be initialized.
	 * @param master This MusicListController's master controller.
	 */
	public void initialize(MasterController master) {
		this.master = master;
		
		// Create each of the BListTabs that present music options to the user
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolderTracks  .class));
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolderQueue   .class));
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolderByArtist.class));
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolderByAlbum .class));
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolder_ByPath .class));
		
		listTabPane.sceneProperty().addListener((ObservableValue<? extends Scene> ov, Scene old_val, Scene new_val) -> {
			new_val.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override public void handle(KeyEvent evt) {
					if (evt.getCode().equals(KeyCode.BACK_SPACE)) {
						((BListTab)listTabPane.getSelectionModel().getSelectedItem()).selectParent();
					}
				}
			});
		});
		
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
		
		// Colorize 'listTabPane' based on the FG and BG colors
		// Components can only be looked up once layout has been applied, so we have to wrap everything in this listener
		listTabPane.needsLayoutProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			if (!listTabPaneHasSetup) {
				listTabPaneHasSetup = true;
				
				// Make the background of the tab header BG colored
				listTabPane.lookup(".tab-header-background").setEffect(master.playbackColorAnimation.lightingBG);
				
				// Color each of the tab labels in the header
				Set<Node> tabs = listTabPane.lookupAll(".tab");
				for (Node tab : tabs) {
					
					// Each tab defaults to BO colors
					tab.setEffect(master.playbackColorAnimation.lightingBO);
					
					// Highlight the tab with FG colors when it is hovered over
					tab.setOnMouseEntered((MouseEvent evt) -> {
						tab.setEffect(master.playbackColorAnimation.lightingFG);
					});
					
					// Set the 'selected' user property when the tab is clicked
					// This property prevents the selected tab being reset to BO colors when it stops being hovered
					EventHandler<? super MouseEvent> tabPressHandler = tab.getOnMousePressed();
					tab.setOnMousePressed((MouseEvent evt) -> {
						// Reset all other tabs to BO colors
						for (Node tab2 : tabs) {
							tab2.getProperties().put("selected", false);
							tab2.setEffect(master.playbackColorAnimation.lightingBO);
						}
						
						// Set this tab to FG colors when it is selected
						tab.setEffect(master.playbackColorAnimation.lightingFG);
						tab.getProperties().put("selected", true);
						
						// Actually perform the original action of the tab click
						tabPressHandler.handle(evt);
					});
					
					// Un-highlight the tab back to BO colors when it stops being hovered over
					tab.setOnMouseExited((MouseEvent evt) -> {
						if (Boolean.TRUE.equals(tab.getProperties().get("selected"))) {
							tab.setEffect(master.playbackColorAnimation.lightingFG);
						} else {
							tab.setEffect(master.playbackColorAnimation.lightingBO);
						}
					});
				}
			}
		});
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
