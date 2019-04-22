package com.bateleur.app.model;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioLocal;
import com.bateleur.app.datatype.BReference;
import com.therealergo.main.Main;
import com.therealergo.main.MainException;
import com.therealergo.main.NilEvent;
import com.therealergo.main.resource.ResourceFile;
import com.therealergo.main.resource.ResourceFolder;

import javafx.application.Platform;

public class LibraryModel implements Iterable<BAudio> {
	private final SettingsModel settings;
	
	private BAudio NO_MEDIA_AUDIO;
	
	private boolean isUpdating;
	public final NilEvent updateStartEvent;
	public final NilEvent updateFinishEvent;
	
	private List<BAudio> listLibarary;
	private List<BAudio> listFiltered;
	private ResourceFolder data;
	
	public LibraryModel(SettingsModel settings, ResourceFolder data) throws Exception {
		this.settings = settings;
		
		this.NO_MEDIA_AUDIO = null;
		
		this.isUpdating = false;
		this.updateStartEvent = new NilEvent();
		this.updateFinishEvent = new NilEvent();
		
		this.listLibarary = new ArrayList<BAudio>();
		this.listFiltered = new ArrayList<BAudio>();
		this.data = data;
		
		data.create();
		ResourceFile[] audioFileList = data.listFileChildren();
		for (int i = 0; i<audioFileList.length; i++) {
			try {
				listLibarary.add(new BAudioLocal(settings, audioFileList[i]));
			} catch (Exception e) {
				Main.log.logErr("Error adding audio store file to library! Corrupted audio store file will be deleted.");
				Main.log.logErr(e);
				audioFileList[i].delete();
			}
		}
		
		reset();
	}
	
	public boolean isUpdating() {
		return isUpdating;
	}
	
	private void getFileReferencesInFolder(ResourceFolder folder, List<BReference> existingReferenceList) {
		// Create a new BReference pointing to each file in this folder with an extension on the LIBRARY_OKAY_TYPES list
		ResourceFile[] audioFileList = folder.listFileChildren();
		for (ResourceFile searchFile : audioFileList) {
			if (settings.get(settings.LIBRARY_OKAY_TYPES).contains(searchFile.getExtension())) {
				BReference existingReference = new BReference(searchFile);
				existingReferenceList.add(existingReference);
				Main.log.log("Found existing BAudio to add to library: " + existingReference);
			}
		}
		
		// Recursively scan to each of this folder's children
		ResourceFolder[] audioFolderList = folder.listFolderChildren();
		for (ResourceFolder searchFolder : audioFolderList) {
			getFileReferencesInFolder(searchFolder, existingReferenceList);
		}
	}
	
	public void update() {
		// Ensure that only one update can occur at a time
		if (!isUpdating) {
			isUpdating = true;
			
			// Notify that we have started updating
			updateStartEvent.accept();
			
			// Make a copy of the old library list to work on
			List<BAudio> currentBAudioList = new ArrayList<BAudio>();
			currentBAudioList.addAll(listLibarary);
			
			// Spawn a thread to update the library in the background
			new Thread("Library Update Thread") {
				public void run() {
					
					// Recursively scan all library paths, constructing a BReference for each existing audio file found
					List<BReference> existingReferenceList = new ArrayList<BReference>();
					List<ResourceFolder> folders = settings.get(settings.LIBRARY_PATH);
					for (ResourceFolder folder : folders) {
						getFileReferencesInFolder(folder, existingReferenceList);
					}
					// Canonically, every entry in this list is an audio file that the user WANTS
					// We now need to reconcile this new reference list with our old library list
					
					// Create an array to hold the new library list
					List<BAudio> finalBAudioList = new ArrayList<BAudio>();
					Iterator<BReference> referenceIterator;
					Iterator<BAudio> audioIterator;
					
					// Move every BAudio for which an exact match was found from the old library list to the new library list
					referenceIterator = existingReferenceList.iterator();
					while (referenceIterator.hasNext()) {
						BReference testReference = referenceIterator.next();
						audioIterator = currentBAudioList.iterator();
						while (audioIterator.hasNext()) {
							BAudio testAudio = audioIterator.next();
							if (testAudio.get(settings.AUDIO_REFERENCE).matchesExact(testReference)) {
								Main.log.log("Exact-matched BAudio in library: " + testReference);
								audioIterator.remove();
								referenceIterator.remove();
								finalBAudioList.add(testAudio);
								break;
							}
						}
					}
					
					// Move every BAudio for which a fuzzy match was found from the old library list to the new library list
					referenceIterator = existingReferenceList.iterator();
					while (referenceIterator.hasNext()) {
						BReference testReference = referenceIterator.next();
						audioIterator = currentBAudioList.iterator();
						while (audioIterator.hasNext()) {
							BAudio testAudio = audioIterator.next();
							if (testAudio.get(settings.AUDIO_REFERENCE).matchesFuzzy(testReference)) {
								Main.log.log("Fuzzy-matched BAudio in library: " + testReference);
								audioIterator.remove();
								referenceIterator.remove();
								testAudio.set(settings.AUDIO_REFERENCE.to(testReference));
								finalBAudioList.add(testAudio);
								break;
							}
						}
					}
					
					// Add new BAudio instances for every entry in the new reference list not matched with a BAudio instance from the old library list
					referenceIterator = existingReferenceList.iterator();
					while (referenceIterator.hasNext()) {
						BReference newReference = referenceIterator.next();
						Main.log.log("Adding new BAudio to library: " + newReference);
						try {
							long nameVal = settings.get(settings.LIBRARY_NEXT_VAL);
							finalBAudioList.add(new BAudioLocal(settings, data.getChildFile(nameVal + ".ser"), newReference));
							settings.set(settings.LIBRARY_NEXT_VAL.to(nameVal + 1));
						} catch (Exception e) {
							Main.log.logErr(e);
						}
					}
					
					// Join back up with the JavaFX thread to actually set this LibraryModel's internal list to the newly-generated list
					Platform.runLater(() -> {
						// Delete any BAudio instances in the old library list that were not matched with entries in the new reference list
						Iterator<BAudio> oldAudioIterator = currentBAudioList.iterator();
						while (oldAudioIterator.hasNext()) {
							BAudio oldAudio = oldAudioIterator.next();
							Main.log.log("Removing old BAudio from library: " + oldAudio.get(settings.AUDIO_REFERENCE));
							oldAudio.delete();
						}
						
						// Fill the internal list with entries from the newly-generated list
						listLibarary.clear();
						listLibarary.addAll(finalBAudioList);
						Main.log.log("Library update complete!");
						
						// Notify that we have finished updating
						updateFinishEvent.accept();

						// Ensure that only one update can occur at a time
						isUpdating = false;
					});
				}
			}.start();
		}
	}
	
	public BAudio getByReference(BReference reference) {
		if (reference.equals(BReference.NO_MEDIA_REF)) {
			if (NO_MEDIA_AUDIO == null) {
				try {
					ResourceFile noMediaPlaybackFile = BReference.NO_MEDIA_REF.getPlaybackFile();
					ResourceFile noMediaSerialFile   = Main.resource.getResourceFileLocal("nomedia.ser");
					
					noMediaPlaybackFile.create();
					noMediaSerialFile  .create();
					
					noMediaPlaybackFile.toFile().deleteOnExit();
					noMediaSerialFile  .toFile().deleteOnExit();
					
					Files.copy(
						Main.resource.getResourceFileClass("audio>nomedia.mp3", App.class).toPath(), 
						noMediaPlaybackFile.toPath(), 
						StandardCopyOption.REPLACE_EXISTING
					);
					
					NO_MEDIA_AUDIO = new BAudioLocal(settings, noMediaSerialFile, BReference.NO_MEDIA_REF);
				} catch (Exception e) {
					throw new MainException(LibraryModel.class, "Unable to instantiate no media BAudio!", e);
				}
			}
			return NO_MEDIA_AUDIO;
		}
		
		Iterator<BAudio> audioIterator = listLibarary.iterator();
		while (audioIterator.hasNext()) {
			BAudio searchAudio = audioIterator.next();
			if (reference.equals(searchAudio.get(settings.AUDIO_REFERENCE))) {
				return searchAudio;
			}
		}
		return null;
	}
	
	public void sortBy(Comparator<BAudio> comparator) {
		listFiltered.sort(comparator);
	}

	public void filterBy(Predicate<BAudio> filter) {
		listFiltered.removeIf(filter.negate());
	}

	public void reset() {
		listFiltered.clear();
		listFiltered.addAll(listLibarary);
		
		//TODO: This will eventally be a 'staticsorting' or some such parameter
		// This will be used by the buttons that allow you to sort AND filter search
		// This method will have to take a boolean as to whether or not to do that ('showInvisible?')
		sortBy((BAudio a0, BAudio a1) -> {
			return a0.get(settings.AUDIO_PROP_TITLE).compareTo(a1.get(settings.AUDIO_PROP_TITLE));
		});
	}

	public int size() {
		return listFiltered.size();
	}

	@Override
	public Iterator<BAudio> iterator() {
		return listFiltered.iterator();
	}
}
