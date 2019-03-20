package com.bateleur.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioFile;
import com.therealergo.main.resource.ResourceFile;
import com.therealergo.main.resource.ResourceFolder;

public class LibraryModel implements Iterable<BAudio> {
	private List<BAudio> listLibarary;
	private List<BAudio> listFiltered;
	
	public LibraryModel(SettingsModel settings, ResourceFolder data) throws IOException {
		listLibarary = new ArrayList<BAudio>();
		listFiltered = new ArrayList<BAudio>();
		
		ResourceFile[] audioFileList = data.listFileChildren();
		for (int i = 0; i<audioFileList.length; i++) {
			listLibarary.add(new BAudioFile(settings, audioFileList[i]));
		}
		
		reset();
	}
	
	/**
	 * TODO: Not yet implemented
	 * @param settings
	 */
	public void update(SettingsModel settings) {
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
