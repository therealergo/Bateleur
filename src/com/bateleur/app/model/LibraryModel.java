package com.bateleur.app.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioLocal;
import com.therealergo.main.Main;
import com.therealergo.main.resource.ResourceFile;
import com.therealergo.main.resource.ResourceFolder;

public class LibraryModel implements Iterable<BAudio> {
	private SettingsModel settings;
	
	private List<BAudio> listLibarary;
	private List<BAudio> listFiltered;
	private ResourceFolder data;
	
	public LibraryModel(SettingsModel settings, ResourceFolder data) {
		this.settings = settings;
		
		this.listLibarary = new ArrayList<BAudio>();
		this.listFiltered = new ArrayList<BAudio>();
		this.data = data;
		
		data.create();
		ResourceFile[] audioFileList = data.listFileChildren();
		for (int i = 0; i<audioFileList.length; i++) {
			try {
				listLibarary.add(new BAudioLocal(settings, audioFileList[i]));
			} catch (Exception e) {
				Main.log.logErr("Error adding audio store file to library! Corrupted audio store file will be deleted.");
				Main.log.logErr(e);
				audioFileList[i].delete();
			}
		}
		
		reset();
	}
	
	private void updateFromFolder(ResourceFolder folder) throws Exception {
		ResourceFile[] audioFileList = folder.listFileChildren();
		
		for (int i = 0; i<audioFileList.length; i++) {
			if (settings.get(settings.LIBRARY_OKAY_TYPES).contains(audioFileList[i].getExtension())) {
				ResourceFile searchFile = audioFileList[i];
				
				reset();
				filterBy((BAudio audio) -> audio.get(settings.PLAYBACK_FILE).equals(searchFile));
				
				while (listFiltered.size() > 1) {
					listLibarary.remove(listFiltered.remove(0).delete());
				}
				
				if (listFiltered.size() < 1) {
					long nameVal = settings.get(settings.LIBRARY_NEXT_VAL);
					listLibarary.add(new BAudioLocal(settings, data.getChildFile(nameVal + ".ser"), searchFile));
					settings.set(settings.LIBRARY_NEXT_VAL.to(nameVal + 1));
				}
			}
		}
		
		reset();
		
		ResourceFolder[] audioFolderList = folder.listFolderChildren();
		for (int i = 0; i<audioFolderList.length; i++) {
			updateFromFolder(audioFolderList[i]);
		}
	}
	
	public void update() throws Exception {
		List<ResourceFolder> folders = settings.get(settings.LIBRARY_PATH);
		for (ResourceFolder folder : folders) {
			updateFromFolder(folder);
		}
	}
	
	public void sortBy(Comparator<BAudio> comparator) {
		listFiltered.sort(comparator);
	}

	public void filterBy(Predicate<BAudio> filter) {
		listFiltered.removeIf(filter.negate());
	}

	public void reset() {
		listFiltered.clear();
		listFiltered.addAll(listLibarary);
	}

	public int size() {
		return listFiltered.size();
	}

	@Override
	public Iterator<BAudio> iterator() {
		return listFiltered.iterator();
	}
}
