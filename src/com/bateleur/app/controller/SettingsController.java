package com.bateleur.app.controller;

import java.io.Serializable;

import com.bateleur.app.datatype.BFile;

public class SettingsController {
	/** Reference to this SettingsController's MasterController. */
	public MasterController master;

	/**
	 * Perform any initialization required by this SettingsController.
	 * Should only be called by the MasterController when the master is ready and this SettingsController is to be initialized.
	 * @param master This SettingsController's master controller.
	 */
	public void initialize(MasterController master) {
		this.master = master;
	}

	/**
	 * Callback triggered whenever a setting's value is adjusted by the user.
	 * @param setting The setting whose value to adjust.
	 * @param value   The value to which the setting is to be set.
	 */
	public <T extends Serializable> void onSettingChanged(BFile.Entry<T> setting, T value) {
	}
}
