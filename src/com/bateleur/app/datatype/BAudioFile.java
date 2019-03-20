package com.bateleur.app.datatype;

import java.io.IOException;
import java.net.URI;

import com.therealergo.main.resource.ResourceFile;

public class BAudioFile extends BAudio {
	// Stub implementation
	public BAudioFile(ResourceFile stor) throws IOException {
		super(stor);
		loadMetadataFromURI(this.<URI>getMetadata("__playbackSourceURI"));
	}
	
	public BAudioFile(ResourceFile stor, URI audioURI) throws IOException {
		super(stor);
		this.<URI>setMetadata("__playbackSourceURI", audioURI);
		loadMetadataFromURI(audioURI);
	}
	
	private void loadMetadataFromURI(URI audioURI) {
	}
}
