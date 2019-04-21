package com.bateleur.app.datatype;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import javafx.scene.image.Image;

public class BAudioLocal_ArtLoader extends BAudio_ArtLoader {
	private static final long serialVersionUID = -4271906009974761079L;
	
	/** This BArtLoaderLocal's thumbnail image, encoded into a format meant for on-disk storage. */
	private final byte[] image_th_encoded_bytes;
	/** This BArtLoaderLocal's blurred image, encoded into a format meant for on-disk storage. */
	private final byte[] image_bl_encoded_bytes;
	
	/** 
	 * This BArtLoaderLocal's actual full-resolution primary Image.
	 * Marked as transient to ensure that the large amount of memory this represents is not saved locally, 
	 * instead being reloaded from the audio file whenever the image is requested.
	 * Encapsulated in a SoftReference to allow the JVM to effectively use this as a cache,
	 * the large amount of memory it represents is only freed if memory is low and the image is not currently being used.
	 */
	private transient SoftReference<Image> image_pr;
	/** 
	 * This BArtLoaderLocal's smaller thumbnail Image.
	 * Marked as transient to ensure that the large amount of memory this represents is not saved locally, 
	 * instead being reloaded from the audio file whenever the image is requested.
	 * Encapsulated in a SoftReference to allow the JVM to effectively use this as a cache,
	 * the large amount of memory it represents is only freed if memory is low and the image is not currently being used.
	 */
	private transient SoftReference<Image> image_th;
	/** 
	 * This BArtLoaderLocal's blurred Image.
	 * Marked as transient to ensure that the large amount of memory this represents is not saved locally, 
	 * instead being reloaded from the audio file whenever the image is requested.
	 * Encapsulated in a SoftReference to allow the JVM to effectively use this as a cache,
	 * the large amount of memory it represents is only freed if memory is low and the image is not currently being used.
	 */
	private transient SoftReference<Image> image_bl;
	
	/**
	 * Creates a new BArtLoaderLocal instance, which represents audio art loaded from a local file.
	 * @param audioFile              ResourceFile pointing to the audio file from which the audio art is loaded.
	 * @param image_th_encoded_bytes The thumbnail image of audio file, stored in a byte array as a disk-writable encoded format (e.g. PNG or JPEG).
	 * @param image_bl_encoded_bytes The blurred image of audio file, stored in a byte array as a disk-writable encoded format (e.g. PNG or JPEG).
	 */
	public BAudioLocal_ArtLoader(byte[] image_th_encoded_bytes, byte[] image_bl_encoded_bytes) {
		if (image_th_encoded_bytes == null) {
			throw new NullPointerException("image_th_encoded_bytes cannot be null!");
		}
		if (image_bl_encoded_bytes == null) {
			throw new NullPointerException("image_bl_encoded_bytes cannot be null!");
		}
		
		this.image_th_encoded_bytes = image_th_encoded_bytes;
		this.image_bl_encoded_bytes = image_bl_encoded_bytes;
		
		this.image_pr = null;
		this.image_th = null;
		this.image_bl = null;
	}
	
	@Override public Image getImagePrimary(BReference parentReference) {
		// Read the image from the cache if the cache has been created
		Image retVal = image_pr == null ? null : image_pr.get();
		
		// Attempt to load the primary image from the audio file at 'audioURI'
		if (retVal == null) {
			try {
				retVal = new Image(
					new ByteArrayInputStream(
						AudioFileIO.read(parentReference.getPlaybackFile().toFile()).getTag().getFirstArtwork().getBinaryData()
					)
				);
			} catch (InvalidAudioFrameException | CannotReadException | IOException | TagException | ReadOnlyFileException e) {
			}
		}
		
		// If the image load failed for any reason, use the default 'no art found' image
		if (retVal == null || retVal.isError()) {
			retVal = super.getImagePrimary(parentReference);
		}
		
		// Update the cache to refer to the new image
		if ( image_pr==null || !retVal.equals(image_pr.get()) ) {
			image_pr = new SoftReference<Image>(retVal);
		}
		
		return retVal;
	}
	
	@Override public Image getImageThumbnail(BReference parentReference) {
		// Read the image from the cache if the cache has been created
		Image retVal = image_th == null ? null : image_th.get();
		
		// Attempt to load the thumbnail image from the stored encoded byte array 'image_th_encoded_bytes'
		if (retVal == null) {
			retVal = new Image(new ByteArrayInputStream(image_th_encoded_bytes));
		}
		
		// If the image load failed for any reason, use the default 'no art found' image
		if (retVal.isError()) {
			retVal = super.getImageThumbnail(parentReference);
		}
		
		// Update the cache to refer to the new image
		if ( image_th==null || !retVal.equals(image_th.get()) ) {
			image_th = new SoftReference<Image>(retVal);
		}
		
		return retVal;
	}
	
	@Override public Image getImageBlurred(BReference parentReference) {
		// Read the image from the cache if the cache has been created
		Image retVal = image_bl == null ? null : image_bl.get();
		
		// Attempt to load the blurred image from the stored encoded byte array 'image_bl_encoded_bytes'
		if (retVal == null) {
			retVal = new Image(new ByteArrayInputStream(image_bl_encoded_bytes));
		}
		
		// If the image load failed for any reason, use the default 'no art found' image
		if (retVal.isError()) {
			retVal = super.getImageBlurred(parentReference);
		}
		
		// Update the cache to refer to the new image
		if ( image_bl==null || !retVal.equals(image_bl.get()) ) {
			image_bl = new SoftReference<Image>(retVal);
		}
		
		return retVal;
	}
}
