package com.bateleur.app.view.list;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;
import com.therealergo.main.NilConsumer;

import javafx.stage.DirectoryChooser;

public class BListOptionFolder_Settings extends BListOptionFolder {
	public BListOptionFolder_Settings(BListTab bListTab, BListOptionFolder parentFolder) {
		super(bListTab, parentFolder);
	}
	
	public String getText() {
		return "";
	}
	
	public List<BListOption> listOptions() {
		SettingsModel settings = bListTab.musicListController.master.settings;
		List<BListOption> options = new LinkedList<BListOption>();
        
		{ // Add User Interface Settings
			options.add(new BListOptionSetting_SectionLabel(bListTab, "User Interface"                        ));
	        options.add(new BListOptionSetting_DoubleSlider(bListTab, settings.UI_MOTION_BLUR    ,  0.0 ,  4.0));
	        options.add(new BListOptionSetting_DoubleSlider(bListTab, settings.UI_ANIMATION_SPEED,  2.0 , 20.0));
	        options.add(new BListOptionSetting_DoubleSlider(bListTab, settings.UI_ART_SCALING    ,  0.7 ,  1.0));
	        options.add(new BListOptionSetting_DoubleSlider(bListTab, settings.UI_LIST_ENTRY_SIZE, 20.0 , 40.0));
	        options.add(new BListOptionSetting_DoubleSlider(bListTab, settings.UI_ART_START_SIZE ,  0.2 ,  4.0));
	        options.add(new BListOptionSetting_DoubleSlider(bListTab, settings.UI_ART_END_SIZE   ,  0.2 ,  4.0));
		}
		
		{ // Add Library Settings
			options.add(new BListOptionSetting_SectionLabel(bListTab, "Library"                               ));
	        { // Add selector for Search Directories
	        	
	        	// Create action that adds a new search folder
		        NilConsumer addFolderAction = () -> {
		        	DirectoryChooser folderChooser = new DirectoryChooser();
		        	folderChooser.setTitle("Add Folder");
		        	File chosenDirectory = folderChooser.showDialog(bListTab.getContent().getScene().getWindow());
		        	if (chosenDirectory != null) {
		        		settings.get(settings.LIBRARY_PATH).add(Main.resource.getResourceFolderPath(chosenDirectory.toPath()));
		        	}
		        	
		        	settings.set(settings.LIBRARY_PATH.to( settings.get(settings.LIBRARY_PATH) )); //TODO: This is a hack to fix a list not being saved bug. The more general design problem here needs fixed. Eventually.
		        	
		        	bListTab.rebuildList(this);
		        };
		        
		        // Create action that removes the folder at the given index
		        Consumer<Integer> removeFolderAction = (Integer folderIndex) -> {
		        	settings.get(settings.LIBRARY_PATH).remove(folderIndex.intValue());
		        	
		        	settings.set(settings.LIBRARY_PATH.to( settings.get(settings.LIBRARY_PATH) )); //TODO: This is a hack to fix a list not being saved bug. The more general design problem here needs fixed. Eventually.
		        	
		        	bListTab.rebuildList(this);
		        };
		        
		        // Add a setting button to add a new folder
		        options.add(new BListOptionSetting_Button(bListTab, 2, "Search Folders", "Add Folder", addFolderAction));
		        
		        // Add a setting button to remove each existing folder
		        for (int i = 0; i<settings.get(settings.LIBRARY_PATH).size(); i++) {
		        	Integer folderIndex = new Integer(i);
		            options.add(new BListOptionSetting_Button(bListTab, 3, settings.get(settings.LIBRARY_PATH).get(i).toPath().toString(), "Remove Folder", () -> { removeFolderAction.accept(folderIndex); } ));
		        }
	        }
		}
        
		return options;
	}
}
