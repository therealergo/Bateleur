package com.bateleur.app.model;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import com.bateleur.app.datatype.BArtLoader;
import com.bateleur.app.datatype.BArtLoaderNone;
import com.bateleur.app.datatype.BFile;
import com.therealergo.main.math.Vector3D;
import com.therealergo.main.resource.ResourceFile;

public class SettingsModel extends BFile {
	public final BFile.Entry< Integer           > TEST_VAL           = new BFile.Entry< Integer           >( "__testVal"          , -1                         );
	
	public final BFile.Entry< URI               > PLAYBACK_URI       = new BFile.Entry< URI               >( "__playbackURI"      , URI.create("")             );
	
	public final BFile.Entry< ArrayList<String> > PLAYLIST_NAME_LIST = new BFile.Entry< ArrayList<String> >( "__playlistNameList" , new ArrayList<String>()    );

	public final BFile.Entry< Integer           > FADE_TIME_USER     = new BFile.Entry< Integer           >( "__fadeTimeUser"     , 0                          );
	public final BFile.Entry< Integer           > FADE_TIME_AUTO     = new BFile.Entry< Integer           >( "__fadeTimeAuto"     , 0                          );
	
	public final BFile.Entry< String            > LIBRARY_PATH       = new BFile.Entry< String            >( "__libraryPath"      , "C:>TempMusicLibrary"      );
	public final BFile.Entry< Long              > LIBRARY_NEXT_VAL   = new BFile.Entry< Long              >( "__librayNextVal"    , 0L                         );
	public final BFile.Entry< String            > LIBRARY_OKAY_TYPES = new BFile.Entry< String            >( "__playlistNameList" , ".wav.flac.ogg.mp3"        );

	public final BFile.Entry< Boolean           > QUEUE_SHUFFLE_EN   = new BFile.Entry< Boolean           >( "__queueShuffleEn"   , false                      );
	public final BFile.Entry< Boolean           > QUEUE_QUEUE_EN     = new BFile.Entry< Boolean           >( "__queueQueueEn"     , true                       );
	public final BFile.Entry< Boolean           > QUEUE_REPEAT_EN    = new BFile.Entry< Boolean           >( "__queueRepeatEn"    , true                       );
	
	public final BFile.Entry< String            > AUDIO_PROP_TITLE   = new BFile.Entry< String            >( "__title"            , "No title found!"          );
	public final BFile.Entry< String            > AUDIO_PROP_ARTIST  = new BFile.Entry< String            >( "__artist"           , "No artist found!"         );
	public final BFile.Entry< String            > AUDIO_PROP_ALBUM   = new BFile.Entry< String            >( "__album"            , "No album found!"          );
	public final BFile.Entry< String            > AUDIO_PROP_TRACKN  = new BFile.Entry< String            >( "__trackn"           , "No track number found!"   );
	public final BFile.Entry< BArtLoader        > AUDIO_PROP_ART     = new BFile.Entry< BArtLoader        >( "__art"              , new BArtLoaderNone()       );
	public final BFile.Entry< Vector3D          > AUDIO_PROP_COLR_BG = new BFile.Entry< Vector3D          >( "__colorBG"          , new Vector3D(0.0, 0.0, 0.0));
	public final BFile.Entry< Vector3D          > AUDIO_PROP_COLR_FG = new BFile.Entry< Vector3D          >( "__colorFG"          , new Vector3D(1.0, 1.0, 1.0));
	public final BFile.Entry< String            > AUDIO_PROP_ART_ENC = new BFile.Entry< String            >( "__artEncoding"      , "png"                      );
	public final BFile.Entry< Integer           > AUDIO_ART_BLUR_NUM = new BFile.Entry< Integer           >( "__audioArtBlurNum"  , 2                          );
	
	public final BFile.Entry< Double            > UI_MOTION_BLUR_MUL = new BFile.Entry< Double           >( "__uiMotionBlurMul"   , 2.0                        );
	public final BFile.Entry< Double            > UI_ANIM_TIME_MUL   = new BFile.Entry< Double           >( "__uiAnimTimeMul"     , 5.0                        );
	public final BFile.Entry< Double            > UI_ART_SCALING     = new BFile.Entry< Double           >( "__uiArtScaling"      , 0.9                        );
	public final BFile.Entry< Double            > UI_BLUR_RADIUS     = new BFile.Entry< Double           >( "__uiBlurRadius"      , 0.1                        );
	
	/** 
	 * @brief Constructor for SettingsModel, which stores all settings for the Bateleur application as a BFile-derived instance.
	 * @see   BFile
	 * @param settingsFile The local file into which the settings are to be stored.
	 */
	public SettingsModel(ResourceFile settingsFile) throws IOException {
		super(settingsFile);
	}
}
