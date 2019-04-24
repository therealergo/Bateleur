package com.bateleur.app.datatype;

import java.io.Serializable;

import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.MainException;
import com.therealergo.main.resource.ResourceFile;

public final class BReference implements Serializable {
	private static final long serialVersionUID = -4691959388427184809L;
	
	public static final BReference NO_MEDIA_REF = new BReference(0);
	
	private final long songId;
	
	public BReference(SettingsModel settings) {
		long generatedSongId = Math.max(1, settings.get(settings.LIBRARY_NEXT_VAL));
		while (settings.get(settings.LIBRARY_STORE_FOLD).getChildFile(generatedSongId + ".ser").exists()) {
			generatedSongId = Math.max(1, generatedSongId + 1);
		}
		settings.set(settings.LIBRARY_NEXT_VAL.to( Math.max(1, generatedSongId + 1 )));
		this.songId = generatedSongId;
	}
	
	public BReference(int songId) {
		if (songId < 0) {
			throw new MainException(BReference.class, "Parameter 'songId' cannot be negative!");
		}
		this.songId = songId;
	}
	
	public ResourceFile getStorageFile(SettingsModel settings) {
		return settings.get(settings.LIBRARY_STORE_FOLD).getChildFile(songId + ".ser");
	}
	
	@Override public boolean equals(Object other) {
		return other instanceof BReference && 
			   songId == ((BReference)other).songId;
	}
	
	@Override public int hashCode() {
		return (int) songId;
	}
	
	@Override public String toString() {
		return "[BReference songId=" + songId + "]";
	}
}
