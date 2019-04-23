package com.bateleur.app.datatype;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import com.therealergo.main.resource.ResourceFile;

public abstract class BFile {
	/**
	 * @brief The file used to store BFile Entries on disk.
	 */
	private final ResourceFile file;
	
	/**
	 * @brief Map used to relate String keys to Serializable values for each BFile Entry.
	 */
	private final HashMap<String, Serializable> valueMap;
	
	/** 
	 * @brief Constructor for BFile, a generic file type that stores Entries consisting of arbitrary key-value mapped values.
	 * @param file The local file into which the mapped values are to be stored
	 * @exception Raises an exception if the file exists but is not correctly-formatted or is corrupted.
	 * @exception Raises an exception if an I/O exception occurs while reading the file.
	 */
	@SuppressWarnings("unchecked")
	public BFile(ResourceFile file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("BFile file cannot be null!");
		}
		
		this.file = file;
		
		if (file.exists()) {
			// Read valueMap from the file
			try (ObjectInputStream ois = new ObjectInputStream(file.getInputStream())) {
				this.valueMap = (HashMap<String, Serializable>) ois.readObject();
			} catch (ClassNotFoundException e) {
				throw new IOException("Given BFile file corrupt or improperly formatted!");
			}
		} else {
			// Create default empty file
			file.create();
			try (ObjectOutputStream oos = new ObjectOutputStream(file.getOutputStream())) {
				this.valueMap = new HashMap<String, Serializable>();
				oos.writeObject(valueMap);
			}
		}
	}

	/**
	 * @brief   Returns the value corresponding to the given Entry.
	 * @details Performs a map lookup to find the value corresponding to to 'Entry.key'.
	 * @param entry The Entry key-value pair for which this BFile's will perform its lookup.
	 * @return  The value corresponding to the given Entry, if one exists.
	 *          This returned value CANNOT be null.
	 *          'Entry.val' is returned as a default if the value corresponding to 'Entry.key' is not set.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T get(Entry<T> entry) {
		Object storedValue = valueMap.get(entry.key);
		return storedValue == null ? entry.val : (T) storedValue;
	}
	
	/**
	 * @brief   Sets the value corresponding to the given Entry's key.
	 * @details Sets the value within the value map corresponding to to 'Entry.key'.
	 *          Note that BFile Entries CANNOT be set to a null value.
	 * @param entry The Entry key-value pair to which this BFile's stored value is to be set.
	 */
	public <T extends Serializable> void set(Entry<T> entry) {
		// Add/update Entry's key-value pair in valueMap
		valueMap.put(entry.key, entry.val);
		
	}
	
	/**
	 * @brief   Saves all values stored in this BFile to disk.
	 * @details Until this is called, all values that have been changed in this BFile may be lost.
	 * @throws IOException If there is an issue writing the BFile to disk.
	 */
	public void save() throws IOException {
		// Write updated valueMap to the on-disk file
		try (ObjectOutputStream oos = new ObjectOutputStream(file.getOutputStream())) {
			oos.writeObject(valueMap);
		}
	}
	
	/**
	 * @brief   Remove the value corresponding to the given Entry's key, if one exists.
	 * @details Removes any value within the value map corresponding to to 'Entry.key'.
	 * @param entry The Entry for which this BFile's stored value is to be removed.
	 * @return  true if there was a value corresponding to the given Entry's key.
	 *          false if there was not a value corresponding to the given Entry's key.
	 */
	public <T extends Serializable> boolean remove(Entry<T> entry) {
		return valueMap.remove(entry.key) != null;
	}

	/**
	 * @brief   Deletes this BFile.
	 * @details After this call, all values corresponding to any Entry's key are removed.
	 *          The on-disk save file is also deleted, so all values are deleted even after a reload.
	 * @return  A reference to the BFile on which this is invoked, for method chaining.
	 */
	public BFile delete() {
		file.delete();
		valueMap.clear();
		
		return this;
	}
	
	/**
	 * @brief   Entry class, which specifies a specific key-value relationship in a BFile.
	 * @details This class is most commonly used as a parameter to BFile.set(...) and BFile.get(...).
	 *          When used in BFile.set(...), the value corresponding to this Entry's 'key' field is set to this Entry's 'val' field.
	 *          When used in BFile.get(...), the value corresponding to this Entry's 'key' field is returned, 
	 *          and this Entry's 'val' field is returned if no BFile value is set.
	 */
	public static final class Entry<T extends Serializable> {
		/**
		 * @brief The key part of this Entry's key-value pair.
		 */
		public final String key;
		
		/**
		 * @brief The value part of this Entry's key-value pair.
		 */
		public final T val;

		/**
		 * @brief   Entry constructor.
		 * @details Each Entry instance is immutable, representing some pairing of key and value.
		 * @param key The key to be used for this Entry's key-value pair.
		 * @param val The value to be used for this Entry's key-value pair.
		 * @exception Raises an exception if the given 'key' String is not a non-null string of nonzero length.
		 * @exception Raises an exception if the given 'val' parameter is null, as BFiles cannot store null values.
		 */
		public Entry(String key, T val) {
			if (key == null) {
				throw new IllegalArgumentException("BFile.Entry key cannot be null!");
			}
			if (key.length() < 1) {
				throw new IllegalArgumentException("BFile.Entry key must be at least 1 character long!");
			}
			if (val == null) {
				throw new IllegalArgumentException("BFile.Entry val cannot be null!");
			}
			
			this.key = key;
			this.val = val;
		}

		/**
		 * @brief   Returns a clone of this Entry with the given value instead of this Entry's value.
		 * @details This is typically used in BFile's 'set(...)' method.
		 *          The syntax for this usage is bFile.set(entry.to(newValue)).
		 *          Since the Entry supplied to the set(...) function has the given value, 
		 *          this will set the value with the same key as 'entry' to 'newValue' in 'bFile'.
		 * @param val The value to be used for the cloned Entry's key-value pair.
		 * @return  The cloned Entry instance.
		 */
		public Entry<T> to(T val) {
			return new Entry<T>(key, val);
		}
	}
}
