package com.bateleur.app.datatype;

import java.io.IOException;
import java.net.URI;

import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.resource.ResourceFile;

public class BAudioFile extends BAudio {
	// Stub implementation
	public BAudioFile(SettingsModel settings, ResourceFile file) throws IOException {
		super(file);
		loadMetadataFromURI(get(settings.PLAYBACK_URI));
	}
	
	public BAudioFile(SettingsModel settings, ResourceFile file, URI audioURI) throws IOException {
		super(file);
		set(settings.PLAYBACK_URI.to(audioURI));
		loadMetadataFromURI(audioURI);
	}
	
	private void loadMetadataFromURI(URI audioURI) {
	}
}
