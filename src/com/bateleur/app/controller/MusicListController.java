package com.bateleur.app.controller;

import java.util.Collection;
import java.util.HashMap;
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
import com.bateleur.app.view.list.BListOptionFolder_Settings;
import com.bateleur.app.view.list.BListOptionFolder_Tracks;
import com.bateleur.app.view.list.BListOptionSetting;
import com.bateleur.app.view.list.BListTab;
import com.therealergo.main.NilEvent;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class MusicListController {
	/** FXML-injected component references. */
	@FXML private TabPane listTabPane;
	@FXML private AnchorPane musicListPane;
	@FXML private Button settingsButton;
	@FXML private Button goUpButton;
	@FXML private Button updateButton;
	@FXML private Button searchButton;
	@FXML private TextField searchBar;
	
	/** Animation played on the update button when the library update is currently running. */
	private Timeline updateButtonAnimation;
	
	/** Reference to this MusicListController's MasterController. */
	public MasterController master;
	
	/** Boolean used to ensure that the listTabPane is only initialized once. */
	private boolean listTabPaneHasSetup;
	
	/** Event that fires every time the search settings change. */
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
		listTabPane.getTabs().add(new BListTab(this, master.library, master.playback, master.settings, BListOptionFolder_Settings.class));
		
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
				
				// Generate a HashMap mapping tab names to actual tab elements
				Set<Node> tabLabelList = listTabPane.lookupAll(".tab-label");
				HashMap<String, Node> tabLabelSet = new HashMap<String, Node>();
				for (Node tabLabel : tabLabelList) {
					tabLabelSet.put( ((Label)tabLabel).getText(), tabLabel );
				}

				// The settings button sort of functions as a tab, with tab name ""
				// This disables the default tab button and replaces it with the setting button
				tabLabelSet.get("").getParent().getParent().setMouseTransparent(true);
				tabLabelSet.put("", settingsButton);

				// Colorize each tab label based on the FG and BO color
				// They are colored FG when selected or hovered over, and BO otherwise
				// This also adds a "tab-label-NAME" CSS class to each label to be used when looking up the components on selection
				Collection<String> fullLabelList = tabLabelSet.keySet();
				for (String tabText : fullLabelList) {
					Node tabNode = tabLabelSet.get(tabText);
					if (listTabPane.getSelectionModel().getSelectedItem().getText().equals( tabText )) {
						tabNode.setEffect(master.playbackColorAnimation.lightingFG);
					} else {
						tabNode.setEffect(master.playbackColorAnimation.lightingBO);
					}
					tabNode.getStyleClass().add( "tab-label-" + tabText );
					tabNode.setOnMouseEntered((MouseEvent event) -> {
						tabNode.setEffect(master.playbackColorAnimation.lightingFG);
					});
					tabNode.setOnMouseExited((MouseEvent event) -> {
						if (!listTabPane.getSelectionModel().getSelectedItem().getText().equals( tabText )) {
							tabNode.setEffect(master.playbackColorAnimation.lightingBO);
						}
					});
				}
				
				// Event to colorize the selected tab label with FG color when it is selected
				listTabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> otab, Tab old_tab, Tab new_tab) -> {
					musicListPane.lookupAll(".tab-label-" + old_tab.getText()).iterator().next().setEffect(master.playbackColorAnimation.lightingBO);
					musicListPane.lookupAll(".tab-label-" + new_tab.getText()).iterator().next().setEffect(master.playbackColorAnimation.lightingFG);
				});
			}
		});
		
		// Setup the actual action of 'settingsButton'
		settingsButton.setOnAction((ActionEvent event) -> {
			listTabPane.getSelectionModel().selectLast();
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
		
		// Create the animation used to indicate that the library is currently updating
		updateButtonAnimation = new Timeline(
			new KeyFrame(Duration.seconds(0.0), new KeyValue(updateButton.rotateProperty(),   0.0, Interpolator.LINEAR)),
			new KeyFrame(Duration.seconds(1.0), new KeyValue(updateButton.rotateProperty(), 360.0, Interpolator.LINEAR))
		);
		updateButtonAnimation.setCycleCount(Timeline.INDEFINITE);
		
		// Colorize 'updateButton' based on the FG and BO color
		// It is colored FG when the update is running or when hovered over, and BO otherwise
		// This also starts/stops the 'updateButtonAnimation' when updating starts/stops
		updateButton.setEffect(master.playbackColorAnimation.lightingBO);
		updateButton.setOnMouseEntered((MouseEvent event) -> {
			updateButton.setEffect(master.playbackColorAnimation.lightingFG);
		});
		updateButton.setOnMouseExited((MouseEvent event) -> {
			if (!master.library.isUpdating()) {
				updateButton.setEffect(master.playbackColorAnimation.lightingBO);
			}
		});
		master.library.updateStartEvent.addListener(() -> {
			updateButton.setEffect(master.playbackColorAnimation.lightingFG);
			updateButtonAnimation.play();
		});
		master.library.updateFinishEvent.addListener(() -> {
			updateButton.setEffect(master.playbackColorAnimation.lightingBO);
			updateButtonAnimation.jumpTo(Duration.ZERO);
			updateButtonAnimation.stop();
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
	 * Gets a filter to be used to filter list options by the current search state.
	 * @return A Predicate that filters a list of BListOptions based on the current search options.
	 *         This Predicate returns true if the given BListOption should be shown after the search, 
	 *         and returns false if the given BListOption should be hidden by the search.
	 */
	public Predicate<BListOption> getSearchBarFilter() {
		return (BListOption option) -> {
			if (option instanceof BListOptionAudio) {
				return ((BListOptionAudio) option).audio.get(master.settings.AUDIO_META_TITLE ).toLowerCase().contains(searchBar.getText().toLowerCase()) || 
				       ((BListOptionAudio) option).audio.get(master.settings.AUDIO_META_ALBUM ).toLowerCase().contains(searchBar.getText().toLowerCase()) || 
				       ((BListOptionAudio) option).audio.get(master.settings.AUDIO_META_ARTIST).toLowerCase().contains(searchBar.getText().toLowerCase());
			} else if (option instanceof BListOptionFolder) {
				return ((BListOptionFolder) option).getText().toLowerCase().contains(searchBar.getText().toLowerCase());
			} else if (option instanceof BListOptionSetting) {
				return ((BListOptionSetting) option).getName().toLowerCase().contains(searchBar.getText().toLowerCase());
			} else {
				return true;
			}
		};
	}
}
