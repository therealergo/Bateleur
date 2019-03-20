package com.bateleur.app.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

import com.bateleur.app.datatype.BFile;
import com.therealergo.main.resource.ResourceFile;

public class SettingsModel {
	public final BFile.Entry< Integer           > TEST_VAL           = new BFile.Entry< Integer           >( "__testVal"          , -1                       );
	
	public final BFile.Entry< URI               > PLAYBACK_URI       = new BFile.Entry< URI               >( "__playbackURI"      , null                     );
	
	public final BFile.Entry< ArrayList<String> > PLAYLIST_NAME_LIST = new BFile.Entry< ArrayList<String> >( "__playlistNameList" , new ArrayList<String>()  );

	public final BFile.Entry< Integer           > FADE_TIME_USER     = new BFile.Entry< Integer           >( "__fadeTimeUser"     , 0                        );
	public final BFile.Entry< Integer           > FADE_TIME_AUTO     = new BFile.Entry< Integer           >( "__fadeTimeAuto"     , 0                        );
	
	public final BFile.Entry< String            > LIBRARY_PATH       = new BFile.Entry< String            >( "__libraryPath"      , "C:>TempMusicLibrary"    );
	public final BFile.Entry< Long              > LIBRARY_NEXT_VAL   = new BFile.Entry< Long              >( "__librayNextVal"    , 0L                       );

	public final BFile.Entry< Boolean           > QUEUE_SHUFFLE_EN   = new BFile.Entry< Boolean           >( "__queueShuffleEn"   , false                    );
	public final BFile.Entry< Boolean           > QUEUE_QUEUE_EN     = new BFile.Entry< Boolean           >( "__queueQueueEn"     , true                     );
	public final BFile.Entry< Boolean           > QUEUE_REPEAT_EN    = new BFile.Entry< Boolean           >( "__queueRepeatEn"    , true                     );
	
	/**
	 * @brief 
	 */
	private final BFile file;
	
	/** 
	 * @brief Constructor for SettingsModel, which stores all settings for the Bateleur application.
	 * @param settingsFile The local file into which the settings are to be stored.
	 * @exception Raises an exception if the given file exists and does not contain a correctly-formatted settings file.
	 * @exception Raises an exception if an I/O exception occurs while reading the given file.
	 */
	public SettingsModel(ResourceFile settingsFile) throws IOException {
		this.file = new BFile(settingsFile);
	}

	/**
	 * @brief   Returns the setting corresponding to the given String 'key'. 
	 * @details Returns 'defaultValue' if the setting corresponding to 'key' is not set. 
	 * @exception Raises an exception if 'key' is either null or an empty String.
	 */
	public <T extends Serializable> T get(BFile.Entry<T> entry) {
		return file.<T>get(entry);
	}
	
	/**
	 * @brief   Sets the setting corresponding to the given String 'key'.
	 * @details Providing null for 'value' will cause the setting corresponding to 'key' to be deleted.
	 * @exception Raises an exception if 'key' is either null or an empty String.
	 * @exception Raises an exception if setting storage file I/O fails.
	 */
	public <T extends Serializable> void set(BFile.Entry<T> entry) {
		file.<T>set(entry);
	}
}
