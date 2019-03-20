package com.bateleur.app.model;

import java.util.Iterator;

import com.bateleur.app.datatype.BAudio;

public class PlaylistModel {
	private SettingsModel settings;
	
	public PlaylistModel(SettingsModel settings) {
		this.settings = settings;
	}
	
	public Iterator<String> iterator() {
		return settings.get(settings.PLAYLIST_NAME_LIST).iterator();
	}

	public void createPlaylist(String playlistName) {
		settings.get(settings.PLAYLIST_NAME_LIST).add(playlistName);
	}

	public void deletePlaylist(String playlistName) {
		settings.get(settings.PLAYLIST_NAME_LIST).remove(playlistName);
	}

	public boolean doesPlaylistExist(String playlistName) {
		return settings.get(settings.PLAYLIST_NAME_LIST).contains(playlistName);
	}

	public void addToPlaylist(BAudio audio, String playlist) {
		audio.get(settings.PLAYLIST_NAME_LIST).add(playlist);
	}

	public void removeFromPlaylist(BAudio audio, String playlist) {
		audio.get(settings.PLAYLIST_NAME_LIST).remove(playlist);
	}

	public boolean isInPlaylist(BAudio audio, String playlist) {
		return audio.get(settings.PLAYLIST_NAME_LIST).contains(playlist);
	}

	public void filterByPlaylist(LibraryModel library, String playlist) {
		library.filterBy( (BAudio audio) -> audio.get(settings.PLAYLIST_NAME_LIST).contains(playlist) ); 
	}
}
