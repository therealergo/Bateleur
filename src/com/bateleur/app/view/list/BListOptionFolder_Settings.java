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
		options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_ANIM_TIME_MUL  ,  2.0 , 20.0));
		options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_ART_SCALING    ,  0.7 ,  1.0));
		options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_LISTOPT_VSIZE  , 20.0 , 40.0));
		options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_BLUR_RADIUS    ,  0.02,  0.5));
		options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_SONG_ANIM_ISIZE,  0.2 ,  4.0));
		options.add(new BListOptionSetting_DoubleSlider(bListTab, bListTab.musicListController.master.settings.UI_SONG_ANIM_OSIZE,  0.2 ,  4.0));
		return options;
	}
}
