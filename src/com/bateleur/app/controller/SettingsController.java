package com.bateleur.app.controller;

import com.bateleur.app.model.SettingsModel;

import java.io.Serializable;

public class SettingsController {
    public SettingsController(SettingsModel settings) {
    }

    public <T extends Serializable> void onSettingChanged (String key, Class<T> type, T value) {
    }
}
