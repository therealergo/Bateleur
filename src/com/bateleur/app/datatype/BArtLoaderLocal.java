package com.bateleur.app.datatype;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;

import org.jaudiotagger.audio.AudioFileIO;

import javafx.scene.image.Image;

public class BArtLoaderLocal extends BArtLoader {
	private static final long serialVersionUID = -4271906009974761079L;
	
	private final URI audioURI;
	private final byte[] image_th_encoded_bytes;
	private final byte[] image_bl_encoded_bytes;
	
	private transient Image image_pr;
	private transient Image image_th;
	private transient Image image_bl;
	
	public BArtLoaderLocal(URI audioURI, byte[] image_th_encoded_bytes, byte[] image_bl_encoded_bytes) {
		if (audioURI == null) {
			throw new NullPointerException("audioURI cannot be null!");
		}
		if (image_th_encoded_bytes == null) {
			throw new NullPointerException("image_th_encoded_bytes cannot be null!");
		}
		if (image_bl_encoded_bytes == null) {
			throw new NullPointerException("image_bl_encoded_bytes cannot be null!");
		}
		
		this.audioURI = audioURI;
		this.image_th_encoded_bytes = image_th_encoded_bytes;
		this.image_bl_encoded_bytes = image_bl_encoded_bytes;
		
		this.image_pr = null;
		this.image_th = null;
		this.image_bl = null;
	}
	
	public Image getImagePrimary() throws Exception {
		if (image_pr == null) {
			image_pr = new Image(new ByteArrayInputStream(AudioFileIO.read(new File(audioURI)).getTag().getFirstArtwork().getBinaryData()));
		}
		return image_pr;
	}
	
	public Image getImageThumbnail() throws Exception {
		if (image_th == null) {
			image_th = new Image(new ByteArrayInputStream(image_th_encoded_bytes));
		}
		return image_th;
	}
	
	public Image getImageBlurred() throws Exception {
		if (image_bl == null) {
			image_bl = new Image(new ByteArrayInputStream(image_bl_encoded_bytes));
		}
		return image_bl;
	}
}
