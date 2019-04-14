package com.bateleur.app.view.list;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BFile;
import com.bateleur.app.datatype.BFile.Entry;
import com.bateleur.app.model.LibraryModel;

public abstract class BListOptionFolderByType extends BListOptionFolder {
	public BListOptionFolderByType(BListTab bListTab) {
		super(bListTab);
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
				options.add(new BListOptionFolderByTypeItem(bListTab, artistName));
			}
		});
		return options;
	}
	
	private class BListOptionFolderByTypeItem extends BListOptionFolder {
		private String artistName;
		
		public BListOptionFolderByTypeItem(BListTab bListTab, String artistName) {
			super(bListTab);
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
	
	public static class BListOptionFolderByAlbum extends BListOptionFolderByType {
		public BListOptionFolderByAlbum(BListTab bListTab) {
			super(bListTab);
		}

		@Override public String getText() {
			return "Albums";
		}
		
		@Override public Entry<String> getType() {
			return bListTab.musicListController.master.settings.AUDIO_PROP_ALBUM;
		}
	}
	
	public static class BListOptionFolderByArtist extends BListOptionFolderByType {
		public BListOptionFolderByArtist(BListTab bListTab) {
			super(bListTab);
		}

		@Override public String getText() {
			return "Artists";
		}
		
		@Override public Entry<String> getType() {
			return bListTab.musicListController.master.settings.AUDIO_PROP_ARTIST;
		}
	}
}