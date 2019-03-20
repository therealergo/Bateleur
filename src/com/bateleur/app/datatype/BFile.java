package com.bateleur.app.datatype;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import com.therealergo.main.resource.ResourceFile;

public final class BFile {
	/**
	 * @brief The file used to locally store settings values.
	 */
	private final ResourceFile file;
	
	/**
	 * @brief Map used to relate setting keys to Serializable setting values.
	 */
	private final HashMap<String, Serializable> settingsMap;
	
	/** 
	 * @brief Constructor for SettingsModel, which stores all settings for the Bateleur application.
	 * @param settingsFile The local file into which the settings are to be stored.
	 * @exception Raises an exception if the given file exists and does not contain a correctly-formatted settings file.
	 * @exception Raises an exception if an I/O exception occurs while reading the given file.
	 */
	@SuppressWarnings("unchecked")
	public BFile(ResourceFile file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("SettingsModel store file cannot be null!");
		}
		
		this.file = file;
		
		if (file.exists()) {
			// Read settingsMap from the settings file
			try (ObjectInputStream ois = new ObjectInputStream(file.getInputStream())) {
				this.settingsMap = (HashMap<String, Serializable>) ois.readObject();
			} catch (ClassNotFoundException e) {
				throw new IOException("Given settings file corrupt or improperly formatted!");
			}
		} else {
			// Create default empty settings file
			file.create();
			try (ObjectOutputStream oos = new ObjectOutputStream(file.getOutputStream())) {
				this.settingsMap = new HashMap<String, Serializable>();
				oos.writeObject(settingsMap);
			}
		}
	}

	/**
	 * @brief   Returns the setting corresponding to the given String 'key'. 
	 * @details Returns 'defaultValue' if the setting corresponding to 'key' is not set. 
	 * @exception Raises an exception if 'key' is either null or an empty String.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T get(Entry<T> entry) {
		Object storedSetting = settingsMap.get(entry.key);
		return storedSetting == null ? entry.val : (T) storedSetting;
	}
	
	/**
	 * @brief   Sets the setting corresponding to the given String 'key'.
	 * @details Providing null for 'value' will cause the setting corresponding to 'key' to be deleted.
	 * @exception Raises an exception if 'key' is either null or an empty String.
	 * @exception Raises an exception if setting storage file I/O fails.
	 */
	public <T extends Serializable> void set(Entry<T> entry) throws IOException {
		// Remove setting if key is null, otherwise add setting
		if (entry.val == null) {
			settingsMap.remove(entry.key);
		} else {
			settingsMap.put(entry.key, entry.val);
		}
		
		// Write updated settingsMap to the settings file
		try (ObjectOutputStream oos = new ObjectOutputStream(file.getOutputStream())) {
			oos.writeObject(settingsMap);
		}
	}
	
	public static final class Entry<T extends Serializable> {
		public final String key;
		public final T val;
		
		public Entry(String key, T def) {
			if (key == null) {
				throw new IllegalArgumentException("BFile.Entry key cannot be null!");
			}
			if (key.length() < 1) {
				throw new IllegalArgumentException("BFile.Entry key must be at least 1 character long!");
			}
			
			this.key = key;
			this.val = def;
		}
		
		public Entry<T> to(T val) {
			return new Entry<T>(key, val);
		}
	}
}
