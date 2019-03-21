package com.bateleur.app.model;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import com.bateleur.app.datatype.BFile;
import com.therealergo.main.resource.ResourceFile;

public class SettingsModel extends BFile {
	public final BFile.Entry< Integer           > TEST_VAL           = new BFile.Entry< Integer           >( "__testVal"          , -1                       );
	
	public final BFile.Entry< URI               > PLAYBACK_URI       = new BFile.Entry< URI               >( "__playbackURI"      , URI.create("")           );
	
	public final BFile.Entry< ArrayList<String> > PLAYLIST_NAME_LIST = new BFile.Entry< ArrayList<String> >( "__playlistNameList" , new ArrayList<String>()  );

	public final BFile.Entry< Integer           > FADE_TIME_USER     = new BFile.Entry< Integer           >( "__fadeTimeUser"     , 0                        );
	public final BFile.Entry< Integer           > FADE_TIME_AUTO     = new BFile.Entry< Integer           >( "__fadeTimeAuto"     , 0                        );
	
	public final BFile.Entry< String            > LIBRARY_PATH       = new BFile.Entry< String            >( "__libraryPath"      , "C:>TempMusicLibrary"    );
	public final BFile.Entry< Long              > LIBRARY_NEXT_VAL   = new BFile.Entry< Long              >( "__librayNextVal"    , 0L                       );

	public final BFile.Entry< Boolean           > QUEUE_SHUFFLE_EN   = new BFile.Entry< Boolean           >( "__queueShuffleEn"   , false                    );
	public final BFile.Entry< Boolean           > QUEUE_QUEUE_EN     = new BFile.Entry< Boolean           >( "__queueQueueEn"     , true                     );
	public final BFile.Entry< Boolean           > QUEUE_REPEAT_EN    = new BFile.Entry< Boolean           >( "__queueRepeatEn"    , true                     );
	
	public final BFile.Entry< String            > AUDIO_PROP_TITLE   = new BFile.Entry< String            >( "title"              , "No title found!"        );
	public final BFile.Entry< String            > AUDIO_PROP_ARTIST  = new BFile.Entry< String            >( "xmpDM:artist"       , "No artist found!"       );
	public final BFile.Entry< String            > AUDIO_PROP_ALBUM   = new BFile.Entry< String            >( "xmpDM:album"        , "No album found!"        );
	public final BFile.Entry< String            > AUDIO_PROP_TRACKN  = new BFile.Entry< String            >( "xmpDM:trackNumber"  , "No track number found!" );
	
	/** 
	 * @brief Constructor for SettingsModel, which stores all settings for the Bateleur application as a BFile-derived instance.
	 * @see   BFile
	 * @param settingsFile The local file into which the settings are to be stored.
	 */
	public SettingsModel(ResourceFile settingsFile) throws IOException {
		super(settingsFile);
	}
}
