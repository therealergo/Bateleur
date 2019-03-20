package com.bateleur.app.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import com.therealergo.main.resource.ResourceFile;

public class SettingsModel {
	/**
	 * @brief Object holding all of the static (i.e. built-in and not saved/loaded) settings.
	 */
	public final StaticSettings stat;
	
	/**
	 * @brief Map used to relate setting keys to Serializable setting values.
	 */
	private final HashMap<String, Serializable> settingsMap;
	
	/**
	 * @brief The file used to locally store settings values.
	 */
	private final ResourceFile settingsFile;
	
	/** 
	 * @brief Constructor for SettingsModel, which stores all settings for the Bateleur application.
	 * @param settingsFile The local file into which the settings are to be stored.
	 * @exception Raises an exception if the given file exists and does not contain a correctly-formatted settings file.
	 * @exception Raises an exception if an I/O exception occurs while reading the given file.
	 */
	public SettingsModel(ResourceFile settingsFile) throws IOException {
		if (settingsFile == null) {
			throw new IllegalArgumentException("SettingsModel store file cannot be null!");
		}
		
		this.stat = new StaticSettings();
		this.settingsMap = new HashMap<String, Serializable>();
		this.settingsFile = settingsFile;
		
		if (settingsFile.exists()) {
			// Read settingsMap from the settings file
			try (ObjectInputStream ois = new ObjectInputStream(settingsFile.getInputStream())) {
				while (ois.available() > 0) {
					try {
						String keyName = ois.readUTF();
						Serializable newObj = (Serializable) ois.readObject();
						settingsMap.put(keyName, newObj);
					} catch (ClassNotFoundException e) {
						throw new IOException("Given settings file corrupt or improperly formatted!");
					}
				}
			}
		} else {
			// Create default empty settings file
			settingsFile.create();
			try (ObjectOutputStream oos = new ObjectOutputStream(settingsFile.getOutputStream())) {
			}
		}
	}

	/**
	 * @brief   Returns the setting corresponding to the given String 'key'. 
	 * @details Returns 'defaultValue' if the setting corresponding to 'key' is not set. 
	 * @exception Raises an exception if 'key' is either null or an empty String.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getSetting(String key, T defaultValue) {
		if (key == null) {
			throw new IllegalArgumentException("Setting key cannot be null!");
		}
		if (key.length() < 1) {
			throw new IllegalArgumentException("Setting key must be at least 1 character long!");
		}
		
		// Return the setting for the given key
		Object storedSetting = settingsMap.get(key);
		return storedSetting == null ? defaultValue : (T) storedSetting;
	}
	
	/**
	 * @brief   Sets the setting corresponding to the given String 'key'.
	 * @details Providing null for 'value' will cause the setting corresponding to 'key' to be deleted.
	 * @exception Raises an exception if 'key' is either null or an empty String.
	 * @exception Raises an exception if setting storage file I/O fails.
	 */
	public <T extends Serializable> void setSetting(String key, T value) throws IOException {
		if (key == null) {
			throw new IllegalArgumentException("Setting key cannot be null!");
		}
		if (key.length() < 1) {
			throw new IllegalArgumentException("Setting key must be at least 1 character long!");
		}
		
		// Remove setting if key is null, otherwise add setting
		if (value == null) {
			settingsMap.remove(key);
		} else {
			settingsMap.put(key, value);
		}
		
		// Write updated settingsMap to the settings file
		try (ObjectOutputStream oos = new ObjectOutputStream(settingsFile.getOutputStream())) {
			Iterator<String> keyIterator = settingsMap.keySet().iterator();
			while (keyIterator.hasNext()) {
				String writeKey = keyIterator.next();
				oos.writeUTF(writeKey);
				oos.writeObject(settingsMap.get(writeKey));
			}
		}
	}
	
	public class StaticSettings {
		public final String KEY_PLAYBACK_URI = "__playbackSourceURI";
		
		public final String KEY_FADE_TIME_USER = "__fadeTimeUser";
		public final int    DEF_FADE_TIME_USER = 0;
		public final String KEY_FADE_TIME_AUTO = "__fadeTimeAuto";
		public final int    DEF_FADE_TIME_AUTO = 0;
	}
}
