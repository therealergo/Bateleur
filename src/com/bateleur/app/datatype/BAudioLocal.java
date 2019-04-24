package com.bateleur.app.datatype;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.MainException;
import com.therealergo.main.math.Vector3D;
import com.therealergo.main.resource.ResourceFile;
import com.twelvemonkeys.image.ConvolveWithEdgeOp;
import com.twelvemonkeys.image.ResampleOp;

public class BAudioLocal extends BAudio {
	public BAudioLocal(SettingsModel settings, ResourceFile file) throws Exception {
		super(file);
	}
	
	public BAudioLocal(SettingsModel settings, ResourceFile audioFile, BReference reference) throws Exception {
		super(reference == null ? null : reference.getStorageFile(settings));
		
		if (audioFile == null) {
			throw new MainException(BAudioLocal.class, "BAudioLocal audio file cannot be null!");
		}
		if (!audioFile.exists()) {
			throw new MainException(BAudioLocal.class, "BAudioLocal audio must exist!");
		}
		if (reference == null) {
			throw new MainException(BAudioLocal.class, "BAudioLocal reference cannot be null!");
		}

		// Locally store the reference pointing to the audio file and reference
		set(settings.AUDIO_RESOURCEFILE.to(audioFile));
		set(settings.AUDIO_REFERENCE   .to(reference));
		
		// Compute the hash of the audio file
		try (InputStream is = audioFile.getInputStream()) {
			byte[] buffer = new byte[1024];
			MessageDigest digest = MessageDigest.getInstance("MD5");
			
			int read;
			while ( (read = is.read(buffer)) >= 0 ) {
				digest.update(buffer, 0, read);
			}
			
			byte[] audioFileHash = digest.digest();
			set(settings.AUDIO_IDENTITYHASH.to( String.format("%032x", new BigInteger(1, audioFileHash)) ));
		} catch (IOException e) {
			throw new MainException(BAudioLocal.class, "IOException while hashing file!", e);
		} catch (NoSuchAlgorithmException e) {
			throw new MainException(BAudioLocal.class, "Cannot run MD5 hashing algorithm!", e);
		}
	}
	
	public String toString() {
		return "[BAudioLocal songId=" + getBackingFile().getShortName() + "]";
	}
	
	public boolean matchesExact(SettingsModel settings, BAudio audio) {
		return get(settings.AUDIO_IDENTITYHASH).equals(audio.get(settings.AUDIO_IDENTITYHASH)) && 
			   get(settings.AUDIO_RESOURCEFILE).equals(audio.get(settings.AUDIO_RESOURCEFILE));
	}
	
	public boolean matchesMove(SettingsModel settings, BAudio audio) {
		return get(settings.AUDIO_IDENTITYHASH).equals(audio.get(settings.AUDIO_IDENTITYHASH));
	}
	
	public boolean matchesEdit(SettingsModel settings, BAudio audio) {
		return get(settings.AUDIO_RESOURCEFILE).equals(audio.get(settings.AUDIO_RESOURCEFILE));
	}
	
	public void loadMetadata(SettingsModel settings) throws Exception {
		// Read all metadata from the audio file
		AudioFile f = AudioFileIO.read(get(settings.AUDIO_RESOURCEFILE).toFile());
		Tag tag = f.getTag();
		
		// Read and locally store all of the audio file's metadata tags
		if (tag.hasField(FieldKey.TITLE )) { set(settings.AUDIO_META_TITLE .to(tag.getFirst(FieldKey.TITLE ))); }
		if (tag.hasField(FieldKey.ARTIST)) { set(settings.AUDIO_META_ARTIST.to(tag.getFirst(FieldKey.ARTIST))); }
		if (tag.hasField(FieldKey.ALBUM )) { set(settings.AUDIO_META_ALBUM .to(tag.getFirst(FieldKey.ALBUM ))); }
		if (tag.hasField(FieldKey.TRACK )) { set(settings.AUDIO_META_TRACKN.to(tag.getFirst(FieldKey.TRACK ))); }

		// Set title to filename if no title is found
		if (get(settings.AUDIO_META_TITLE).equals(settings.AUDIO_META_TITLE.val)) { set(settings.AUDIO_META_TITLE.to(get(settings.AUDIO_RESOURCEFILE).getShortName())); }
		
		// Read and set all data pertaining to the audio file's art
		Artwork art = tag.getFirstArtwork();
		if (art != null) {
			// Read original file art into a BufferedImage
			BufferedImage image_bi = ImageIO.read(new ByteArrayInputStream(art.getBinaryData()));
			
			// Scale original file art image to a 'thumbnail image' height of 107 pixels, preserving aspect ratio
			float scaleFactor = 107.0f / image_bi.getHeight();
			int scaledWidth  = (int)(image_bi.getWidth() *scaleFactor);
			int scaledHeight = (int)(image_bi.getHeight()*scaleFactor);
			BufferedImageOp resizer = new ResampleOp(scaledWidth, scaledHeight, ResampleOp.FILTER_LANCZOS);
			BufferedImage image_th_bi = resizer.filter(image_bi, null);
			
			// Create a filter to blur the resulting thumbnail image
			// This filter performs a 2-pass separated 7x7 Gaussian blur
			float[] gaussianKernel = new float[]{0.034793f, 0.102006f, 0.216137f, 0.294128f, 0.216137f, 0.102006f, 0.034793f};
			int numBlurPasses = settings.get(settings.ART_BLUR_PASSES);
			Kernel kernel_0 = new Kernel(gaussianKernel.length, 1, gaussianKernel);
			BufferedImageOp op_0 = new ConvolveWithEdgeOp(kernel_0, ConvolveWithEdgeOp.EDGE_REFLECT, null);
			Kernel kernel_1 = new Kernel(1, gaussianKernel.length, gaussianKernel);
			BufferedImageOp op_1 = new ConvolveWithEdgeOp(kernel_1, ConvolveWithEdgeOp.EDGE_REFLECT, null);
			
			// Apply the blur filter 'numBlurPasses' times to create the blurred image
			BufferedImage image_bl_bi = image_th_bi;
			for (int i = 0; i<numBlurPasses; i++) {
				image_bl_bi = op_0.filter(image_bl_bi, null);
				image_bl_bi = op_1.filter(image_bl_bi, null);
			}
			
			// Encode the thumbnail image with AUDIO_PROP_ART_ENC encoding into a byte array
			ByteArrayOutputStream image_th_baos = new ByteArrayOutputStream();
			ImageIO.write(image_th_bi, settings.get(settings.ART_IMG_ENCODING), image_th_baos);
			image_th_baos.flush();
			byte[] image_th_encoded_bytes = image_th_baos.toByteArray();
			image_th_baos.close();
			
			// Encode the blurred image with AUDIO_PROP_ART_ENC encoding into a byte array
			ByteArrayOutputStream image_bl_baos = new ByteArrayOutputStream();
			ImageIO.write(image_bl_bi, settings.get(settings.ART_IMG_ENCODING), image_bl_baos);
			image_bl_baos.flush();
			byte[] image_bl_encoded_bytes = image_bl_baos.toByteArray();
			image_bl_baos.close();
			
			// Create the BArtLoaderLocal instance which will locally store all of the image data
			set(settings.AUDIO_META_ARTLOAD.to(new BAudioLocal_ArtLoader(image_th_encoded_bytes, image_bl_encoded_bytes)));
			
			// Compute the foreground and background colors of the album art
			{
				// Precompute the width and height of the thumbnail image to be scanned for its foreground and background colors
				int width  = (int) image_th_bi.getWidth();
				int height = (int) image_th_bi.getHeight();
				
				// Actually compute the foreground and background colors of the image
				// This algorithm will be replaced later, so it is not documented
				int baseX = 0;
				int baseY = 0;
				int baseZ = 0;
				int baseX_f = 0;
				int baseY_f = 0;
				int baseZ_f = 0;
				int baseThresh = -1;
				for (int run = 0; run<2; run++) {
					int[][][] countBlocks = new int[2][2][2];
					int fitTotal = 0;
					int blockSize = 128;
					for (int iter = 0; iter<8; iter++) {
						for (int x = 0; x<width; x++) {
							for (int y = 0; y<height; y++) {
								int argb = image_th_bi.getRGB(x, y);
								int r = (int) ((argb>>16) & 0xFF);
								int g = (int) ((argb>> 8) & 0xFF);
								int b = (int) ( argb      & 0xFF);
								if (r >= baseX && r < baseX+blockSize*2 && 
									g >= baseY && g < baseY+blockSize*2 && 
									b >= baseZ && b < baseZ+blockSize*2 && 
									(r-baseX_f)*(r-baseX_f)+(b-baseY_f)*(b-baseY_f)+(g-baseZ_f)*(g-baseZ_f) >= baseThresh) {
									countBlocks[(r-baseX)/blockSize]
											   [(g-baseY)/blockSize]
											   [(b-baseZ)/blockSize]++;
								}
							}
						}
						int cbMax = Math.max(Math.max(Math.max(countBlocks[0][0][0], countBlocks[0][0][1]), 
								             	      Math.max(countBlocks[0][1][0], countBlocks[0][1][1])), 
											 Math.max(Math.max(countBlocks[1][0][0], countBlocks[1][0][1]), 
								             	      Math.max(countBlocks[1][1][0], countBlocks[1][1][1])));
						fitTotal += cbMax;
						if (countBlocks[1][1][1] == cbMax) {
							baseX += blockSize;
							baseY += blockSize;
							baseZ += blockSize;
						} else if (countBlocks[0][1][1] == cbMax) {
							baseY += blockSize;
							baseZ += blockSize;
						} else if (countBlocks[1][0][1] == cbMax) {
							baseX += blockSize;
							baseZ += blockSize;
						} else if (countBlocks[1][1][0] == cbMax) {
							baseX += blockSize;
							baseY += blockSize;
						} else if (countBlocks[0][0][1] == cbMax) {
							baseZ += blockSize;
						} else if (countBlocks[0][1][0] == cbMax) {
							baseY += blockSize;
						} else if (countBlocks[1][0][0] == cbMax) {
							baseX += blockSize;
						}
						blockSize/=2;
						countBlocks[0][0][0] = 0;
						countBlocks[0][0][1] = 0;
						countBlocks[0][1][0] = 0;
						countBlocks[0][1][1] = 0;
						countBlocks[1][0][0] = 0;
						countBlocks[1][0][1] = 0;
						countBlocks[1][1][0] = 0;
						countBlocks[1][1][1] = 0;
					}
					if (run == 0) {
						baseX_f = baseX;
						baseY_f = baseY;
						baseZ_f = baseZ;
						baseX = 0;
						baseY = 0;
						baseZ = 0;
						baseThresh = 40000;
					}
					if (run == 1) {
						if (fitTotal < 30) {
							baseX = baseX_f;
							baseY = baseY_f;
							baseZ = baseZ_f;
						}
						if ((baseX-baseX_f)*(baseX-baseX_f)+(baseY-baseY_f)*(baseY-baseY_f)+(baseZ-baseZ_f)*(baseZ-baseZ_f) < baseThresh) {
							int blDist = baseX*baseX + baseY*baseY + baseZ*baseZ;
							int whDist = (255-baseX)*(255-baseX) + (255-baseY)*(255-baseY) + (255-baseZ)*(255-baseZ);
							if (blDist > whDist) {
								baseX = 0;
								baseY = 0;
								baseZ = 0;
							} else {
								baseX = 255;
								baseY = 255;
								baseZ = 255;
							}
						}
					}
				}
				
				// Locally store the computed art foreground and background colors
				set( settings.AUDIO_META_COLR_BG.to(new Vector3D(baseX_f/255.0, baseY_f/255.0, baseZ_f/255.0)) );
				set( settings.AUDIO_META_COLR_FG.to(new Vector3D(baseX  /255.0, baseY  /255.0, baseZ  /255.0)) );
			}
		}
	}
}
