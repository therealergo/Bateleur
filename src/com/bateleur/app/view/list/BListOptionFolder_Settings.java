package com.bateleur.app.view.list;

import java.util.LinkedList;
import java.util.List;

public class BListOptionFolder_Settings extends BListOptionFolder {
	public BListOptionFolder_Settings(BListTab bListTab, BListOptionFolder parentFolder) {
		super(bListTab, parentFolder);
	}
	
	public String getText() {
		return "";
	}
	
	public List<BListOption> listOptions() {
		List<BListOption> options = new LinkedList<BListOption>();
		options.add(new BListOptionSetting(bListTab));
		return options;
	}
}
