package com.bateleur.app.view.list;

import com.therealergo.main.MainException;

import javafx.scene.layout.Region;

public class BListOptionSetting_SectionLabel extends BListOptionSetting {
	private String name;
	
	public BListOptionSetting_SectionLabel(BListTab bListTab, String name) {
		super(bListTab);
		
		if (name == null) {
			throw new MainException(BListOptionSetting_SectionLabel.class, "Supplied name cannot be null!");
		}
		
		this.name = name;
	}
	
	@Override public int getTabbing() {
		return 1;
	}
	
	@Override public String getName() {
		return name;
	}

	@Override public Region buildControl() {
		return null;
	}
}
