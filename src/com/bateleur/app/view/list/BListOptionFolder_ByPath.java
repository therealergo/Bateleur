package com.bateleur.app.view.list;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.resource.ResourceFolder;

public class BListOptionFolder_ByPath extends BListOptionFolder {
	public BListOptionFolder_ByPath(BListTab bListTab, BListOptionFolder parentFolder) {
		super(bListTab, parentFolder);
	}
	
	public String getText() {
		return "Folders";
	}
	
	public List<BListOption> listOptions() {
		SettingsModel settings = bListTab.musicListController.master.settings;
		
		List<BListOption> options = new LinkedList<BListOption>();
		
		Iterator<ResourceFolder> baseFolders = settings.get(settings.LIBRARY_PATH).iterator();
		
		while (baseFolders.hasNext()) {
			options.add(new BListOptionFolder_ByPath_Item(bListTab, this, baseFolders.next()));
		}
		
		return options;
	}
	
	private class BListOptionFolder_ByPath_Item extends BListOptionFolder {
		private ResourceFolder folder;
		
		public BListOptionFolder_ByPath_Item(BListTab bListTab, BListOptionFolder parentFolder, ResourceFolder folder) {
			super(bListTab, parentFolder);
			
			this.folder = folder;
		}
		
		@Override public String getText() {
			return folder.getFullName();
		}
		
		@Override public List<BListOption> listOptions() {
			LibraryModel  library  = bListTab.musicListController.master.library ;
			
			List<BListOption> options = new LinkedList<BListOption>();
			
			ResourceFolder[] folderChildren = folder.listFolderChildren();
			for (int i = 0; i<folderChildren.length; i++) {
				options.add(new BListOptionFolder_ByPath_Item(bListTab, this, folderChildren[i]));
			}
			
			library.reset();
			library.forEach((BAudio audio) -> {
				if (audio.get(bListTab.musicListController.master.settings.AUDIO_RESOURCEFILE).getParent().equals(folder)) {
					options.add(new BListOptionAudio(bListTab, audio));
				}
			});
			
			return options;
		}
	}
}
