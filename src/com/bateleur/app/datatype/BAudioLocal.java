package com.bateleur.app.datatype;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.SAXException;

import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;
import com.therealergo.main.resource.ResourceFile;

public class BAudioLocal extends BAudio {
	public BAudioLocal(SettingsModel settings, ResourceFile file) throws IOException, SAXException, TikaException {
		super(file);
		loadMetadataFromURI(get(settings.PLAYBACK_URI));
	}
	
	public BAudioLocal(SettingsModel settings, ResourceFile file, URI audioURI) throws IOException, SAXException, TikaException {
		super(file);
		set(settings.PLAYBACK_URI.to(audioURI));
		loadMetadataFromURI(audioURI);
	}
	
	private void loadMetadataFromURI(URI audioURI) throws IOException, SAXException, TikaException {
		if (audioURI == null) {
			return;
			//TODO: Throw error here.
		}
		
		try (InputStream stream = Main.resource.getResourceFileGlobal(audioURI.getPath().substring(1).replaceAll("/", ">")).getInputStream()) {
			Metadata metadata = new Metadata();
			new Tika().parse(stream, metadata);
			String[] names = metadata.names();
			for (int i = 0; i<names.length; i++) {
//				Main.log.log(names[i] + " = " + metadata.get(names[i]));
				setExternal(new BFile.Entry<String>(names[i], metadata.get(names[i])));
			}
		}
	}
}
