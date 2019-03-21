package com.bateleur.app.datatype;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import com.therealergo.main.resource.ResourceFile;

public abstract class BAudio extends BFile {
	/**
	 * @brief   Map used to relate metadata keys to Serializable metadata values.
	 * @details This map stores metadata values that are created and stored externally from the application.
	 */
	private final HashMap<String, Serializable> valueMapExternal;
	
	/** 
	 * @brief   Constructor for BAudio, the base class of any Bateleur audio file.
	 * @details A BAudioFile stores two types of metadata:
	 *          	External metadata is stored in the audio file or some other file separate from Bateleur, e.g. metadata values stored in an audio file's IDV3 tags.
	 *          	Internal metadata is stored within the Bateleur application, as a BFile.
	 *          Internal metadata takes precedence, so if both internal and external metadata are set for the same key internal metadata is returned.
	 *          External metadata should be loaded when the object is created by the subclass using the 'setMetadataExternal(...)' method.
	 *          This class provides getters and setters for this metadata, and automatically saves/loads internal metadata as a BFile.
	 *          The BAudio takes as a parameter a file which points NOT to an audio file but to a file into which internal metadata is to be stored.
	 *          If the given file does not exist, it is created and filled with default metadata.
	 * @param metadataFile The local file into which the internal metadata is to be stored.
	 * @exception Raises an exception if the given file exists and does not contain correct audio file metadata.
	 * @exception Raises an exception if an I/O exception occurs while reading the given file.
	 */
	protected BAudio(ResourceFile metadataFile) throws IOException {
		super(metadataFile);
		
		this.valueMapExternal = new HashMap<String, Serializable>();
	}
	
	/**
	 * @brief   Returns the metadata corresponding to the given Entry's key.
	 * @details If metadata has been set for both an external and non-external source, the non-external metadata is returned.
	 * @param entry The Entry for which this the metadata value is to be retrieved.
	 * @see     BFile.get(...)
	 */
	public <T extends Serializable> T get(BFile.Entry<T> entry) {
		// Return the metadata for the given key
		// Internal metadata is given priority over external metadata
		@SuppressWarnings("unchecked")
		T externalValue = (T)valueMapExternal.get(entry.key);
		if (externalValue != null) {
			entry = entry.to(externalValue);
		}
		return super.get(entry);
	}
	
	/**
	 * @brief   Deletes this BAudio.
	 * @see     BFile.delete()
	 * @return  A reference to the BAudio on which this is invoked, for method chaining.
	 */
	public BAudio delete() {
		return (BAudio) super.delete();
	}
	
	/**
	 * @brief   Sets the metadata value corresponding to the given Entry's key field to the Entry's val field.
	 * @details This method sets only externally-stored metadata.
	 *          As such, metadata set in this manner is not written to disk by BFile.
	 *          This should be called only by BAudio implementations.
	 * @param entry The Entry for which this BAudio's stored external metadata value is to be set.
	 * @see     BFile.set(...)
	 */
	protected <T extends Serializable> void setExternal(BFile.Entry<T> entry) {
		valueMapExternal.put(entry.key, entry.val);
	}
	
	/**
	 * @brief   Removes the metadata value corresponding to the given Entry's key field.
	 * @details This method removes only externally-stored metadata.
	 *          As such, metadata removed in this manner is not written to disk by BFile.
	 *          This should be called only by BAudio implementations.
	 * @param entry The Entry for which this BAudio's stored external metadata value is to be removed.
	 * @return  true if there was an external metadata value corresponding to the given Entry's key.
	 *          false if there was not an external metadata value corresponding to the given Entry's key.
	 * @see     BFile.remove(...)
	 */
	protected <T extends Serializable> boolean removeExternal(BFile.Entry<T> entry) {
		return valueMapExternal.remove(entry.key) != null;
	}
}
