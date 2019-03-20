package com.bateleur.app.model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;

import com.bateleur.app.datatype.BAudio;
import com.therealergo.main.resource.ResourceFile;
import com.therealergo.main.resource.ResourceFolder;

public class LibraryModel implements Iterable<BAudio> {
	public LibraryModel (ResourceFile file, ResourceFolder data) {

	}

	public void update (SettingsModel settings) {

	}

	public void sortBy(Comparator<BAudio> comparator) {

	}

	public void filterBy(Function<BAudio, Boolean> filter) {

	}

	public int size() {

	}

	public void reset() {

	}

	@Override
	public Iterator<BAudio> iterator() {
		return null;
	}
}
