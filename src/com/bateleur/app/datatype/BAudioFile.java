package com.bateleur.app.datatype;

import java.io.IOException;
import java.net.URI;

import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.resource.ResourceFile;

public class BAudioFile extends BAudio {
	// Stub implementation
	public BAudioFile(SettingsModel settings, ResourceFile stor) throws IOException {
		super(stor);
		loadMetadataFromURI(this.<URI>getMetadata(settings.stat.KEY_PLAYBACK_URI));
	}
	
	public BAudioFile(SettingsModel settings, ResourceFile stor, URI audioURI) throws IOException {
		super(stor);
		this.<URI>setMetadata(settings.stat.KEY_PLAYBACK_URI, audioURI);
		loadMetadataFromURI(audioURI);
	}
	
	private void loadMetadataFromURI(URI audioURI) {
	}
}
