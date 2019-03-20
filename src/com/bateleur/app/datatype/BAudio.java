package com.bateleur.app.datatype;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import com.therealergo.main.resource.ResourceFile;

public abstract class BAudio {
	/**
	 * @brief   Map used to relate metadata keys to Serializable metadata values.
	 * @details This map stores metadata values that are created and stored internally to the application.
	 */
	private HashMap<String, Serializable> mapInternal;
	
	/**
	 * @brief   Map used to relate metadata keys to Serializable metadata values.
	 * @details This map stores metadata values that are created and stored externally from the application.
	 */
	private HashMap<String, Serializable> mapExternal;
	
	/**
	 * @brief The file used to locally store any metadata values that are meant to be stored internally to the application.
	 */
	private ResourceFile metadataFile;
	
	/** 
	 * @brief   Constructor for BAudio, the base class of any Bateleur audio file.
	 * @details A BAudioFile stores two types of metadata:
	 *          	External metadata is stored in the audio file or some other file separate from Bateleur, e.g. metadata values stored in an audio file's IDV3 tags.
	 *          	Internal metadata is stored within the Bateleur application.
	 *          Internal metadata takes precedence, so if both internal and external metadata are set for the same key internal metadata is returned.
	 *          External metadata should be loaded when the object is created by the subclass using the 'setMetadataExternal(...)' method.
	 *          This class provides getters and setters for this metadata, and automatically saves/loads internal metadata from a file.
	 *          The BAudio takes as a parameter a file which points NOT to an audio file but to a file into which internal metadata is to be stored.
	 *          If the given file does not exist, it is created and filled with default metadata.
	 * @param metadataFile The local file into which the internal metadata is to be stored.
	 * @exception Raises an exception if the given file exists and does not contain correct audio file metadata.
	 * @exception Raises an exception if an I/O exception occurs while reading the given file.
	 */
	protected BAudio(ResourceFile metadataFile) throws IOException {
		if (metadataFile == null) {
			throw new IllegalArgumentException("BAudio store file cannot be null!");
		}
		
		this.mapInternal = new HashMap<String, Serializable>();
		this.mapExternal = new HashMap<String, Serializable>();
		this.metadataFile = metadataFile;
		
		if (metadataFile.exists()) {
			// Read mapInternal from the metadata file
			try (ObjectInputStream ois = new ObjectInputStream(metadataFile.getInputStream())) {
				while (ois.available() > 0) {
					try {
						String keyName = ois.readUTF();
						Serializable newObj = (Serializable) ois.readObject();
						mapInternal.put(keyName, newObj);
					} catch (ClassNotFoundException e) {
						throw new IOException("Given metadata file corrupt or improperly formatted!");
					}
				}
			}
		} else {
			// Create default empty metadata file
			metadataFile.create();
			try (ObjectOutputStream oos = new ObjectOutputStream(metadataFile.getOutputStream())) {
			}
		}
	}

	/**
	 * @brief   Returns the metadata corresponding to the given String key. 
	 * @details If metadata has been set for both an external and non-external source, the non-external metadata is returned. 
	 *          Returns null if the given key has no metadata set. 
	 * @exception Raises an exception if the key is either null or an empty String.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getMetadata(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Metadata key cannot be null!");
		}
		if (key.length() < 1) {
			throw new IllegalArgumentException("Metadata key must be at least 1 character long!");
		}
		
		// Return the metadata for the given key
		// Internal metadata is given priority over external metadata
		// If no internal or external metadata is found, then 'null' is returned
		Object internalT = mapInternal.get(key);
		Object externalT = mapExternal.get(key);
		return internalT == null ? externalT == null ? null : (T) externalT : (T) internalT;
	}
	
	/**
	 * @brief   Sets the metadata corresponding to the given String 'key'.
	 * @details Providing null for 'prop' will cause the metadata with the given key to be deleted.
	 *          Will not modify any external metadata, but can ‘shadow’ or 'hide' external metadata with the same key.
	 * @exception Raises an exception if the key is either null or an empty String.
	 */
	public <T extends Serializable> void setMetadata(String key, T prop) throws IOException {
		if (key == null) {
			throw new IllegalArgumentException("Metadata key cannot be null!");
		}
		if (key.length() < 1) {
			throw new IllegalArgumentException("Metadata key must be at least 1 character long!");
		}
		
		// Remove metadata entry if key is null, otherwise add metadata entry
		if (prop == null) {
			mapInternal.remove(key);
		} else {
			mapInternal.put(key, prop);
		}
		
		// Write updated mapInternal to the metadata file
		try (ObjectOutputStream oos = new ObjectOutputStream(metadataFile.getOutputStream())) {
			Iterator<String> keyIterator = mapInternal.keySet().iterator();
			while (keyIterator.hasNext()) {
				String writeKey = keyIterator.next();
				oos.writeUTF(writeKey);
				oos.writeObject(mapInternal.get(writeKey));
			}
		}
	}
	
	/**
	 * @brief   Sets the metadata corresponding to the given String 'key'.
	 * @details Providing null for 'prop' will cause the metadata with the given key to be deleted.
	 *          This method sets only externally-stored metadata.
	 *          As such, metadata set in this manner is not saved by the Bateleur application.
	 *          This should only be called by implementations of the BAudio class.
	 * @exception Raises an exception if the key is either null or an empty String.
	 */
	protected <T extends Serializable> void setMetadataExternal(String key, T prop) {
		if (key == null) {
			throw new IllegalArgumentException("Metadata key cannot be null!");
		}
		if (key.length() < 1) {
			throw new IllegalArgumentException("Metadata key must be at least 1 character long!");
		}
		
		// Remove metadata entry if key is null, otherwise add metadata entry
		if (prop == null) {
			mapExternal.remove(key);
		} else {
			mapExternal.put(key, prop);
		}
	}
}
