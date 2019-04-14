package com.bateleur.app.view.list;

import java.util.LinkedList;
import java.util.List;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;

public class BListOptionFolder_Tracks extends BListOptionFolder {
	public BListOptionFolder_Tracks(BListTab bListTab, BListOptionFolder parentFolder) {
		super(bListTab, parentFolder);
	}
	
	public String getText() {
		return "Tracks";
	}
	
	public List<BListOption> listOptions() {
		LibraryModel library = bListTab.musicListController.master.library;
		
		List<BListOption> options = new LinkedList<BListOption>();
		library.reset();
		library.forEach((BAudio audio) -> {
			options.add(new BListOptionFile(bListTab, audio));
		});
		return options;
	}
}
