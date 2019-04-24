package com.bateleur.app.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

	private HashMap<BReference, BAudio> libraryReferenceMap;
	private List<BAudio> listLibarary;
	private List<BAudio> listFiltered;
	
	public LibraryModel(SettingsModel settings, ResourceFolder data) throws Exception {
		this.settings = settings;
		
		this.NO_MEDIA_AUDIO = null;
		
		this.isUpdating = false;
		this.updateStartEvent = new NilEvent();
		this.updateFinishEvent = new NilEvent();
		
		this.libraryReferenceMap = new HashMap<BReference, BAudio>();
		this.listLibarary = new ArrayList<BAudio>();
		this.listFiltered = new ArrayList<BAudio>();
		
		data.create();
		ResourceFile[] audioFileList = data.listFileChildren();
		for (int i = 0; i<audioFileList.length; i++) {
			try {
				BAudioLocal addedAudio = new BAudioLocal(settings, audioFileList[i]);
				libraryReferenceMap.put(addedAudio.get(settings.AUDIO_REFERENCE), addedAudio);
				listLibarary.add(addedAudio);
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
	
	private void listAudioInFolder(ResourceFolder folder, List<BAudioLocal> newAudioList) throws Exception {
		// Create a new BAudioLocal pointing to each file in this folder with an extension on the LIBRARY_OKAY_TYPES list
		ResourceFile[] audioFileList = folder.listFileChildren();
		for (ResourceFile searchFile : audioFileList) {
			if (settings.get(settings.LIBRARY_OKAY_TYPES).contains(searchFile.getExtension())) {
				BAudioLocal existingAudio = new BAudioLocal(settings, searchFile, new BReference(settings));
				newAudioList.add(existingAudio);
				Main.log.log("Found existing BAudio to add to library: " + existingAudio);
			}
		}
		
		// Recursively scan to each of this folder's children
		ResourceFolder[] audioFolderList = folder.listFolderChildren();
		for (ResourceFolder searchFolder : audioFolderList) {
			listAudioInFolder(searchFolder, newAudioList);
		}
	}
	
	public void update() {
		// Ensure that only one update can occur at a time
		if (!isUpdating) {
			isUpdating = true;
			
			// Notify that we have started updating
			updateStartEvent.accept();
			
			// Make a copy of the old library list to work on
			List<BAudio> oldAudioList = new ArrayList<BAudio>();
			oldAudioList.addAll(listLibarary);
			
			// Spawn a thread to update the library in the background
			new Thread("Library Update Thread") {
				public void run() {
					try {
						// Recursively scan all library paths, constructing a BAudioLocal for each existing audio file found
						List<BAudioLocal> newAudioList = new ArrayList<BAudioLocal>();
						List<ResourceFolder> folders = settings.get(settings.LIBRARY_PATH);
						for (ResourceFolder folder : folders) {
							listAudioInFolder(folder, newAudioList);
						}
						// Canonically, every entry in this list is an audio file that the user WANTS
						// We now need to reconcile this new audio list with our old library list
						
						// Create an array to hold the new library list
						List<BAudio> finalAudioList = new ArrayList<BAudio>();
						Iterator<BAudioLocal> newAudioIterator;
						Iterator<BAudio     > oldAudioIterator;
						Iterator<BAudio     > finalAudioIterator;
						
						// Move every BAudio for which an exact match was found from the old library list to the new library list
						newAudioIterator = newAudioList.iterator();
						while (newAudioIterator.hasNext()) {
							BAudioLocal newAudio = newAudioIterator.next();
							oldAudioIterator = oldAudioList.iterator();
							while (oldAudioIterator.hasNext()) {
								BAudio oldAudioToCheck = oldAudioIterator.next();
								if (oldAudioToCheck.matchesExact(settings, newAudio)) {
									Main.log.log("Exact-matched BAudio in library: " + oldAudioToCheck);
									oldAudioIterator.remove();
									newAudioIterator.remove();
									finalAudioList.add(oldAudioToCheck);
									break;
								}
							}
						}
						
						// Perform fuzzy matching on the leftover files in the old library list and the new library list
						// These matches attempt to detect files that have been moved and edited, and treat them appropriately
						{
							// Detect files that have been moved
							// These files have their reference updated and are moved from the old library list to the new library list
							newAudioIterator = newAudioList.iterator();
							while (newAudioIterator.hasNext()) {
								BAudioLocal newAudio = newAudioIterator.next();
								oldAudioIterator = oldAudioList.iterator();
								while (oldAudioIterator.hasNext()) {
									BAudio oldAudioToCheck = oldAudioIterator.next();
									if (oldAudioToCheck.matchesMove(settings, newAudio)) {
										Main.log.log("Move-matched BAudio in library: " + oldAudioToCheck);
										oldAudioIterator.remove();
										newAudioIterator.remove();
										oldAudioToCheck.set(settings.AUDIO_RESOURCEFILE.to( newAudio.get(settings.AUDIO_RESOURCEFILE) ));
										finalAudioList.add(oldAudioToCheck);
										break;
									}
								}
							}
							
							// Detect files that have been edited
							// These files are reloaded to ensure that any updated metadata is written into the store file
							// However, other metadata (e.g. playlists) are maintained through this reload
							newAudioIterator = newAudioList.iterator();
							while (newAudioIterator.hasNext()) {
								BAudioLocal newAudio = newAudioIterator.next();
								oldAudioIterator = oldAudioList.iterator();
								while (oldAudioIterator.hasNext()) {
									BAudio oldAudioToCheck = oldAudioIterator.next();
									if (oldAudioToCheck.matchesEdit(settings, newAudio)) {
										Main.log.log("Edit-matched BAudio in library: " + oldAudioToCheck);
										oldAudioIterator.remove();
										newAudioIterator.remove();
										oldAudioToCheck.set(settings.AUDIO_RESOURCEFILE.to( newAudio.get(settings.AUDIO_RESOURCEFILE) ));
										oldAudioToCheck.set(settings.AUDIO_IDENTITYHASH.to( newAudio.get(settings.AUDIO_IDENTITYHASH) ));
										oldAudioToCheck.loadMetadata(settings);
										finalAudioList.add(oldAudioToCheck);
										break;
									}
								}
							}
						}
						
						// Add new BAudio instances for every entry in the new reference list not matched with a BAudio instance from the old library list
						newAudioIterator = newAudioList.iterator();
						while (newAudioIterator.hasNext()) {
							BAudioLocal newAudio = newAudioIterator.next();
							Main.log.log("Adding new BAudio to library: " + newAudio);
							newAudio.loadMetadata(settings);
							finalAudioList.add(newAudio);
						}
						
						// Create new libraryReferenceMap hash from list
						HashMap<BReference, BAudio> finalReferenceMap = new HashMap<BReference, BAudio>();
						finalAudioIterator = finalAudioList.iterator();
						while (finalAudioIterator.hasNext()) {
							BAudio finalAudio = finalAudioIterator.next();
							finalReferenceMap.put(finalAudio.get(settings.AUDIO_REFERENCE), finalAudio);
						}
						
						// Join back up with the JavaFX thread to actually set this LibraryModel's internal list to the newly-generated list
						Platform.runLater(() -> {
							// Delete any BAudio instances in the old library list that were not matched with entries in the new reference list
							Iterator<BAudio> toDeleteAudioIterator = oldAudioList.iterator();
							while (toDeleteAudioIterator.hasNext()) {
								BAudio toDeleteAudio = toDeleteAudioIterator.next();
								Main.log.log("Removing old BAudio from library: " + toDeleteAudio);
								toDeleteAudio.delete();
							}
							
							// Update the internal list and hash to match the newly-generated list and hash
							listLibarary = finalAudioList;
							libraryReferenceMap = finalReferenceMap;
							
							// Notify that we have finished updating
							updateFinishEvent.accept();
							Main.log.log("Library update complete!");
							
							// Ensure that only one update can occur at a time
							isUpdating = false;
						});
					} catch (Exception e) {
						isUpdating = false;
						throw new MainException(LibraryModel.class, "Library update failed!", e);
					}
				}
			}.start();
		}
	}
	
	public BAudio getByReference(BReference reference) {
		if (reference.equals(BReference.NO_MEDIA_REF)) {
			if (NO_MEDIA_AUDIO == null) {
				try {
					ResourceFile noMediaPlaybackFile = Main.resource.getResourceFileLocal("nomedia.mp3");
					ResourceFile noMediaSerialFile   = BReference.NO_MEDIA_REF.getStorageFile();
					
					noMediaPlaybackFile.toFile().deleteOnExit();
					noMediaSerialFile  .toFile().deleteOnExit();
					
					Files.copy(
						Main.resource.getResourceFileClass("audio>nomedia.mp3", App.class).toPath(), 
						noMediaPlaybackFile.toPath(), 
						StandardCopyOption.REPLACE_EXISTING
					);
					
					NO_MEDIA_AUDIO = new BAudioLocal(settings, noMediaPlaybackFile, BReference.NO_MEDIA_REF);
					NO_MEDIA_AUDIO.loadMetadata(settings);
				} catch (Exception e) {
					throw new MainException(LibraryModel.class, "Unable to instantiate no media BAudio!", e);
				}
			}
			return NO_MEDIA_AUDIO;
		}
		
		BAudio inLibraryAudio = libraryReferenceMap.get(reference);
		return inLibraryAudio == null ? getByReference(BReference.NO_MEDIA_REF) : inLibraryAudio;
	}
	
	public void saveAll() throws IOException {
		Iterator<BAudio> audioIterator = listLibarary.iterator();
		while (audioIterator.hasNext()) {
			audioIterator.next().save();
		}
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
			return a0.get(settings.AUDIO_META_TITLE).compareTo(a1.get(settings.AUDIO_META_TITLE));
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
