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
        options.add(new BListOptionSetting_SectionLabel(bListTab, "User Interface"));
        options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_MOTION_BLUR    ,  0.0 ,  4.0));
        options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_ANIMATION_SPEED,  2.0 , 20.0));
        options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_ART_SCALING    ,  0.7 ,  1.0));
        options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_LIST_ENTRY_SIZE, 20.0 , 40.0));
        options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_ART_START_SIZE ,  0.2 ,  4.0));
        options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_ART_END_SIZE   ,  0.2 ,  4.0));
		return options;
	}
}
