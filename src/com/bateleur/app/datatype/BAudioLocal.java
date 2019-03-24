package com.bateleur.app.datatype;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.resource.ResourceFile;

import javafx.scene.image.Image;

public class BAudioLocal extends BAudio {
	public BAudioLocal(SettingsModel settings, ResourceFile file) throws Exception {
		super(file);
		loadMetadataFromURI(settings, get(settings.PLAYBACK_URI));
	}
	
	public BAudioLocal(SettingsModel settings, ResourceFile file, URI audioURI) throws Exception {
		super(file);
		set(settings.PLAYBACK_URI.to(audioURI));
		loadMetadataFromURI(settings, audioURI);
	}
	
	private void loadMetadataFromURI(SettingsModel settings, URI audioURI) throws Exception {
		if (audioURI.getPath().length() > 0) {
			AudioFile f = AudioFileIO.read(new File(audioURI));
			Tag tag = f.getTag();
			
			Artwork art = tag.getFirstArtwork();
			if (art != null) {
				Image image = new Image(new ByteArrayInputStream(art.getBinaryData()));
				setExternal(settings.AUDIO_PROP_ART.to(
					new BArtLoader() {
					private static final long serialVersionUID = 1L;

					@Override
					public Image getImage() throws Exception {
						return image;
					}
				}));
			}
			
			setExternal(settings.AUDIO_PROP_TITLE .to(tag.getFirst(FieldKey.TITLE )));
			setExternal(settings.AUDIO_PROP_ARTIST.to(tag.getFirst(FieldKey.ARTIST)));
			setExternal(settings.AUDIO_PROP_ALBUM .to(tag.getFirst(FieldKey.ALBUM )));
			setExternal(settings.AUDIO_PROP_TRACKN.to(tag.getFirst(FieldKey.TRACK )));
		}
	}
}
