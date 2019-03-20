package com.bateleur.app.model;

import java.io.IOException;
import java.net.URI;
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
	
	public LibraryModel(SettingsModel settings, ResourceFolder data) throws IOException {
		this.settings = settings;
		
		this.listLibarary = new ArrayList<BAudio>();
		this.listFiltered = new ArrayList<BAudio>();
		this.data = data;
		
		data.create();
		ResourceFile[] audioFileList = data.listFileChildren();
		for (int i = 0; i<audioFileList.length; i++) {
			listLibarary.add(new BAudioLocal(settings, audioFileList[i]));
		}
		
		reset();
	}
	
	/**
	 * TODO: Not yet implemented
	 * @param settings
	 * @throws IOException 
	 */
	public void update() throws IOException {
		ResourceFolder libraryFolder = Main.resource.getResourceFolderGlobal(settings.get(settings.LIBRARY_PATH));
		ResourceFile[] audioFileList = libraryFolder.listFileChildren();
		
		for (int i = 0; i<audioFileList.length; i++) {
			URI searchURI = audioFileList[i].getPath().toUri();
			
			reset();
			filterBy((BAudio audio) -> audio.get(settings.PLAYBACK_URI).equals(searchURI));
			
			while (listFiltered.size() > 1) {
				listLibarary.remove(listFiltered.remove(0).delete());
			}
			
			if (listFiltered.size() < 1) {
				long nameVal = settings.get(settings.LIBRARY_NEXT_VAL);
				listLibarary.add(new BAudioLocal(settings, data.getChildFile(nameVal + ".ser"), searchURI));
				settings.set(settings.LIBRARY_NEXT_VAL.to(nameVal + 1));
			}
		}
		
		reset();
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
