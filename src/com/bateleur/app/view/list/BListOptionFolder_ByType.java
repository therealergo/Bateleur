package com.bateleur.app.view.list;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BFile;
import com.bateleur.app.datatype.BFile.Entry;
import com.bateleur.app.model.LibraryModel;

public abstract class BListOptionFolder_ByType extends BListOptionFolder {
	public BListOptionFolder_ByType(BListTab bListTab, BListOptionFolder parentFolder) {
		super(bListTab, parentFolder);
	}
	
	public abstract BFile.Entry<String> getType();
	
	@Override public List<BListOption> listOptions() {
		LibraryModel  library  = bListTab.musicListController.master.library ;
		
		List<BListOption> options = new LinkedList<BListOption>();
		library.reset();
		HashSet<String> generatedArtistFolders = new HashSet<String>();
		library.forEach((BAudio audio) -> {
			if (!generatedArtistFolders.contains(audio.get(getType()))) {
				String artistName = audio.get(getType());
				generatedArtistFolders.add(artistName);
				options.add(new BListOptionFolder_ByType_Item(bListTab, this, artistName));
			}
		});
		return options;
	}
	
	private class BListOptionFolder_ByType_Item extends BListOptionFolder {
		private String artistName;
		
		public BListOptionFolder_ByType_Item(BListTab bListTab, BListOptionFolder parentFolder, String artistName) {
			super(bListTab, parentFolder);
			this.artistName = artistName;
		}
		
		@Override public String getText() {
			return artistName;
		}
		
		@Override public List<BListOption> listOptions() {
			LibraryModel  library  = bListTab.musicListController.master.library ;
			
			List<BListOption> options = new LinkedList<BListOption>();
			library.reset();
			library.filterBy((BAudio audio) -> {
				return audio.get(getType()).equals(artistName);
			});
			library.forEach((BAudio audio) -> {
				options.add(new BListOptionFile(bListTab, audio));
			});
			return options;
		}
	}
	
	public static class BListOptionFolder_ByAlbum extends BListOptionFolder_ByType {
		public BListOptionFolder_ByAlbum(BListTab bListTab, BListOptionFolder parentFolder) {
			super(bListTab, parentFolder);
		}

		@Override public String getText() {
			return "Albums";
		}
		
		@Override public Entry<String> getType() {
			return bListTab.musicListController.master.settings.AUDIO_PROP_ALBUM;
		}
	}
	
	public static class BListOptionFolder_ByArtist extends BListOptionFolder_ByType {
		public BListOptionFolder_ByArtist(BListTab bListTab, BListOptionFolder parentFolder) {
			super(bListTab, parentFolder);
		}

		@Override public String getText() {
			return "Artists";
		}
		
		@Override public Entry<String> getType() {
			return bListTab.musicListController.master.settings.AUDIO_PROP_ARTIST;
		}
	}
}
