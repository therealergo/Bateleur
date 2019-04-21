package com.bateleur.app.datatype;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.therealergo.main.MainException;
import com.therealergo.main.resource.ResourceFile;

public class BReference implements Serializable {
	private static final long serialVersionUID = -6296710954635051390L;
	
	private final ResourceFile audioFile;
	private final byte[] audioFileHash;
	
	public BReference(ResourceFile audioFile) {
		this.audioFile = audioFile;
		
		// Compute hash of given audio file
		if (audioFile != null) {
			try (InputStream is = audioFile.getInputStream()) {
				byte[] buffer = new byte[1024];
				MessageDigest digest = MessageDigest.getInstance("MD5");
				
				int read;
				while ( (read = is.read(buffer)) >= 0 ) {
					digest.update(buffer, 0, read);
				}
				
				audioFileHash = digest.digest();
			} catch (IOException e) {
				throw new MainException(BReference.class, "IOException while hashing file!", e);
			} catch (NoSuchAlgorithmException e) {
				throw new MainException(BReference.class, "Cannot run MD5 hashing algorithm!", e);
			}
		} else {
			audioFileHash = null;
		}
	}
	
	public ResourceFile getPlaybackFile() {
		return audioFile;
	}
	
	public String getHumanReadableHash() {
		return String.format("%032x", new BigInteger(1, audioFileHash));
	}
	
	public boolean matchesExact(BReference reference) {
		return reference instanceof BReference && 
			   audioFile.equals( ((BReference)reference).audioFile ) && 
			   Arrays.equals( ((BReference)reference).audioFileHash, audioFileHash );
	}
	
	public boolean matchesFuzzy(BReference reference) {
		return reference instanceof BReference && 
			   Arrays.equals( ((BReference)reference).audioFileHash, audioFileHash );
	}
	
	@Override public boolean equals(Object other) {
		return other != null &&
			   other instanceof BReference && 
			   matchesExact( (BReference)other );
	}
	
	@Override public int hashCode() {
		return audioFile.hashCode();
	}
	
	@Override public String toString() {
		return "[BReference audioFile=" + audioFile + "]";
	}
}
