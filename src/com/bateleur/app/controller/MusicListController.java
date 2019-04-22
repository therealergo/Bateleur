package com.bateleur.app.controller;

import java.util.Set;
import java.util.function.Predicate;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.view.list.BListOption;
import com.bateleur.app.view.list.BListOptionAudio;
import com.bateleur.app.view.list.BListOptionFolder;
import com.bateleur.app.view.list.BListOptionFolder_ByPath;
import com.bateleur.app.view.list.BListOptionFolder_ByType.BListOptionFolder_ByAlbum;
import com.bateleur.app.view.list.BListOptionFolder_ByType.BListOptionFolder_ByArtist;
import com.bateleur.app.view.list.BListOptionFolder_Queue;
import com.bateleur.app.view.list.BListOptionFolder_Tracks;
import com.bateleur.app.view.list.BListTab;
import com.therealergo.main.NilEvent;

import javafx.animation.KeyValue;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MusicListController {
	/** FXML-injected component references. */
	@FXML private TabPane listTabPane;
	@FXML private AnchorPane musicListPane;
	@FXML private Button goUpButton;
	@FXML private Button settingsButton;
	@FXML private Button updateButton;
	@FXML private Button searchButton;
	@FXML private TextField searchBar;
	
	/** Reference to this MusicListController's MasterController. */
	public MasterController master;
	
	/** Boolean used to ensure that the listTabPane is only initialized once. */
	private boolean listTabPaneHasSetup;
	
	/** */
	public final NilEvent searchChangeEvent = new NilEvent();
	
	/**
	 * Perform any initialization required by this MusicListController.
	 * Should only be called by the MasterController when the master is ready and this MusicListController is to be initialized.
	 * @param master This MusicListController's master controller.
	 */
	public void initialize(MasterController master) {
		this.master = master;
		
		// Create each of the BListTabs that present music options to the user
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolder_Tracks  .class));
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolder_Queue   .class));
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolder_ByArtist.class));
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolder_ByAlbum .class));
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolder_ByPath  .class));
		
		// We can only get the scene after initialization finishes, so we wait until then
		listTabPane.sceneProperty().addListener((ObservableValue<? extends Scene> ov, Scene old_val, Scene new_val) -> {
			
			// Add a key listener to detect the 'backspace' key being pressed
			new_val.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override public void handle(KeyEvent evt) {
					if (evt.getCode().equals(KeyCode.BACK_SPACE)) {
						
						// Tell the currently-selected tab to go to up a directory to its parent folder
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
		
		// Colorize 'goUpButton' based on the FG and BO color
		goUpButton.setEffect(master.playbackColorAnimation.lightingBO);
		goUpButton.setOnMouseEntered((MouseEvent event) -> {
			goUpButton.setEffect(master.playbackColorAnimation.lightingFG);
		});
		goUpButton.setOnMouseExited((MouseEvent event) -> {
			goUpButton.setEffect(master.playbackColorAnimation.lightingBO);
		});
		
		// Setup the actual action of 'goUpButton'
		goUpButton.setOnAction((ActionEvent event) -> {
			// Tell the currently-selected tab to go to up a directory to its parent folder
			((BListTab)listTabPane.getSelectionModel().getSelectedItem()).selectParent();
		});
		
		// Colorize 'settingsButton' based on the FG and BO color
		settingsButton.setEffect(master.playbackColorAnimation.lightingBO);
		settingsButton.setOnMouseEntered((MouseEvent event) -> {
			settingsButton.setEffect(master.playbackColorAnimation.lightingFG);
		});
		settingsButton.setOnMouseExited((MouseEvent event) -> {
			settingsButton.setEffect(master.playbackColorAnimation.lightingBO);
		});
		
		// Setup the actual action of 'settingsButton'
		settingsButton.setOnAction((ActionEvent event) -> {
		});
		
		// Colorize 'updateButton' based on the FG and BO color
		updateButton.setEffect(master.playbackColorAnimation.lightingBO);
		updateButton.setOnMouseEntered((MouseEvent event) -> {
			updateButton.setEffect(master.playbackColorAnimation.lightingFG);
		});
		updateButton.setOnMouseExited((MouseEvent event) -> {
			updateButton.setEffect(master.playbackColorAnimation.lightingBO);
		});
		
		// Setup the actual action of 'updateButton'
		updateButton.setOnAction((ActionEvent event) -> {
			master.library.update();
		});
		
		// Colorize 'searchButton' and 'searchBar' together based on the FG and BO color
		// They are colored FG when filtering the current tab OR hovered, and BO otherwise
		{
			// Start out at BO color
			searchButton.setEffect(master.playbackColorAnimation.lightingBO);
			searchBar   .setEffect(master.playbackColorAnimation.lightingBO);
			
			// When mouse enters, always go to FG color
			searchButton.setOnMouseEntered((MouseEvent event) -> {
				searchButton.setEffect(master.playbackColorAnimation.lightingFG);
				searchBar   .setEffect(master.playbackColorAnimation.lightingFG);
			});
			searchBar.setOnMouseEntered((MouseEvent event) -> {
				searchButton.setEffect(master.playbackColorAnimation.lightingFG);
				searchBar   .setEffect(master.playbackColorAnimation.lightingFG);
			});
			
			// When mouse exits, only go to BO color if the current tab is not filtered
			searchButton.setOnMouseExited((MouseEvent event) -> {
				if (!((BListTab)listTabPane.getSelectionModel().getSelectedItem()).isFiltered.get()) {
					searchButton.setEffect(master.playbackColorAnimation.lightingBO);
					searchBar   .setEffect(master.playbackColorAnimation.lightingBO);
				}
			});
			searchBar.setOnMouseExited((MouseEvent event) -> {
				if (!((BListTab)listTabPane.getSelectionModel().getSelectedItem()).isFiltered.get()) {
					searchButton.setEffect(master.playbackColorAnimation.lightingBO);
					searchBar   .setEffect(master.playbackColorAnimation.lightingBO);
				}
			});
			
			// When tab is switched, if the new tab is filtered go to BO color, otherwise go to FG color
			listTabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> ov, Tab old_val, Tab new_val) -> {
				if (((BListTab)new_val).isFiltered.get()) {
					searchButton.setEffect(master.playbackColorAnimation.lightingFG);
					searchBar   .setEffect(master.playbackColorAnimation.lightingFG);
				} else {
					searchButton.setEffect(master.playbackColorAnimation.lightingBO);
					searchBar   .setEffect(master.playbackColorAnimation.lightingBO);
				}
			});
			
			// When a selected tab is unfiltered (e.g. when a folder is selected), go to BO color
			for (Tab tab : listTabPane.getTabs()) {
				((BListTab)tab).isFiltered.addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
					if (tab.isSelected() && (!new_val)) {
						searchButton.setEffect(master.playbackColorAnimation.lightingBO);
						searchBar   .setEffect(master.playbackColorAnimation.lightingBO);
					}
				});
			}
		}
		
		// Setup the actual action of 'searchButton'
		searchButton.setOnAction((ActionEvent event) -> {
			searchBar.setText("");
			searchBar.requestFocus();
		});
		
		// Trigger 'searchChangeEvent' whenever the search filter is enabled or changed
		searchBar.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
			Platform.runLater(() -> {
				searchChangeEvent.accept();
			});
		});
		searchBar.focusedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			if (new_val) {
				searchChangeEvent.accept();
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
	
	/**
	 * 
	 */
	public Predicate<BListOption> getSearchBarFilter() {
		return (BListOption option) -> {
			if (option instanceof BListOptionAudio) {
				return ((BListOptionAudio) option).audio.get(master.settings.AUDIO_PROP_TITLE ).toLowerCase().contains(searchBar.getText().toLowerCase()) || 
				       ((BListOptionAudio) option).audio.get(master.settings.AUDIO_PROP_ALBUM ).toLowerCase().contains(searchBar.getText().toLowerCase()) || 
				       ((BListOptionAudio) option).audio.get(master.settings.AUDIO_PROP_ARTIST).toLowerCase().contains(searchBar.getText().toLowerCase());
			} else if (option instanceof BListOptionFolder) {
				return ((BListOptionFolder) option).getText().toLowerCase().contains(searchBar.getText().toLowerCase());
			} else {
				return true;
			}
		};
	}
}
