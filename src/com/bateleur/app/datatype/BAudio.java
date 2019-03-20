package com.bateleur.app.datatype;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import com.therealergo.main.resource.ResourceFile;

public abstract class BAudio {
	/**
	 * @brief   Map used to relate metadata keys to Serializable metadata values.
	 * @details This map stores metadata values that are created and stored externally from the application.
	 */
	private final HashMap<String, Serializable> mapExternal;
	
	/**
	 * @brief The file used to locally store any metadata values that are meant to be stored internally to the application.
	 */
	private final BFile file;
	
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
		this.mapExternal = new HashMap<String, Serializable>();
		this.file = new BFile(metadataFile);
	}

	/**
	 * @brief   Returns the metadata corresponding to the given String key. 
	 * @details If metadata has been set for both an external and non-external source, the non-external metadata is returned. 
	 *          Returns null if the given key has no metadata set. 
	 * @exception Raises an exception if the key is either null or an empty String.
	 */
	public <T extends Serializable> T get(BFile.Entry<T> entry) {
		// Return the metadata for the given key
		// Internal metadata is given priority over external metadata
		// If no internal or external metadata is found, then 'null' is returned
		@SuppressWarnings("unchecked")
		T externalValue = (T)mapExternal.get(entry.key);
		if (externalValue != null) {
			entry = entry.to(externalValue);
		}
		return file.<T>get(entry);
	}
	
	/**
	 * @brief   Sets the metadata corresponding to the given String 'key'.
	 * @details Providing null for 'prop' will cause the metadata with the given key to be deleted.
	 *          Will not modify any external metadata, but can ‘shadow’ or 'hide' external metadata with the same key.
	 * @exception Raises an exception if the key is either null or an empty String.
	 */
	public <T extends Serializable> void set(BFile.Entry<T> entry) throws IOException {
		file.<T>set(entry);
	}
	
	/**
	 * @brief   Sets the metadata corresponding to the given String 'key'.
	 * @details Providing null for 'prop' will cause the metadata with the given key to be deleted.
	 *          This method sets only externally-stored metadata.
	 *          As such, metadata set in this manner is not saved by the Bateleur application.
	 *          This should only be called by implementations of the BAudio class.
	 * @exception Raises an exception if the key is either null or an empty String.
	 */
	protected <T extends Serializable> void setExternal(BFile.Entry<T> entry) {
		// Remove metadata entry if key is null, otherwise add metadata entry
		if (entry.val == null) {
			mapExternal.remove(entry.key);
		} else {
			mapExternal.put(entry.key, entry.val);
		}
	}

	public BAudio delete() {
		file.delete();
		return this;
	}
}
