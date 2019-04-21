package com.bateleur.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio_ArtLoader;
import com.bateleur.app.datatype.BFile;
import com.bateleur.app.datatype.BReference;
import com.therealergo.main.Main;
import com.therealergo.main.math.Vector3D;
import com.therealergo.main.resource.ResourceFile;
import com.therealergo.main.resource.ResourceFolder;

public class SettingsModel extends BFile {
	public final BFile.Entry< Integer                   > TEST_VAL           = new BFile.Entry< Integer                   >( "__testVal"          , -1                          );
	
	public final BFile.Entry< ArrayList<String>         > PLAYLIST_NAME_LIST = new BFile.Entry< ArrayList<String>         >( "__playlistNameList" , new ArrayList<String>()     );
	
	public final BFile.Entry< Integer                   > FADE_TIME_USER     = new BFile.Entry< Integer                   >( "__fadeTimeUser"     , 0                           );
	public final BFile.Entry< Integer                   > FADE_TIME_AUTO     = new BFile.Entry< Integer                   >( "__fadeTimeAuto"     , 0                           );
	
	public final BReference NULL_BREFERENCE = new BReference(Main.resource.getResourceFileClass("null>nullAudio.mp3", App.class));
	private final ArrayList<ResourceFolder> LIBRARY_PATH_INIT = new ArrayList<ResourceFolder>(Arrays.asList(
		Main.resource.getResourceFolderGlobal("C:>TempMusicLibrary") //TODO: Populate this with normal Windows music directories when we're confident about our stability.
	));
	private final ArrayList<String> LIBRARY_OKAY_TYPES_INIT = new ArrayList<String>(Arrays.asList(
		"aiff", "wav", "flac", "mp3"
	));
	public final BFile.Entry< ArrayList<ResourceFolder> > LIBRARY_PATH       = new BFile.Entry< ArrayList<ResourceFolder> >( "__libraryPath"      , LIBRARY_PATH_INIT           );
    public final BFile.Entry< Long                      > LIBRARY_NEXT_VAL   = new BFile.Entry< Long                      >( "__librayNextVal"    , 0L                          );
    public final BFile.Entry< ArrayList<String>         > LIBRARY_OKAY_TYPES = new BFile.Entry< ArrayList<String>         >( "__playlistNameList" , LIBRARY_OKAY_TYPES_INIT     );

    public final BFile.Entry< Boolean                   > QUEUE_SHUFFLE_EN   = new BFile.Entry< Boolean                   >( "__queueShuffleEn"   , false                       );
    public final BFile.Entry< Boolean                   > QUEUE_QUEUE_EN     = new BFile.Entry< Boolean                   >( "__queueQueueEn"     , true                        );
    public final BFile.Entry< Boolean                   > QUEUE_REPEAT_EN    = new BFile.Entry< Boolean                   >( "__queueRepeatEn"    , true                        );
    
	public final BFile.Entry< BReference                > AUDIO_REFERENCE    = new BFile.Entry< BReference                >( "__reference"        , NULL_BREFERENCE             );
	public final BFile.Entry< BAudio_ArtLoader          > AUDIO_ARTLOADER    = new BFile.Entry< BAudio_ArtLoader          >( "__artloader"        , new BAudio_ArtLoader()      );
    public final BFile.Entry< String                    > AUDIO_PROP_TITLE   = new BFile.Entry< String                    >( "__title"            , "<<no title>>"              );
    public final BFile.Entry< String                    > AUDIO_PROP_ARTIST  = new BFile.Entry< String                    >( "__artist"           , "<<no artist>>"             );
    public final BFile.Entry< String                    > AUDIO_PROP_ALBUM   = new BFile.Entry< String                    >( "__album"            , "<<no album>>"              );
    public final BFile.Entry< String                    > AUDIO_PROP_TRACKN  = new BFile.Entry< String                    >( "__trackn"           , "<<no track number>>"       );
    public final BFile.Entry< Vector3D                  > AUDIO_PROP_COLR_BG = new BFile.Entry< Vector3D                  >( "__colorBG"          , new Vector3D(0.0, 0.0, 0.0) );
    public final BFile.Entry< Vector3D                  > AUDIO_PROP_COLR_FG = new BFile.Entry< Vector3D                  >( "__colorFG"          , new Vector3D(1.0, 1.0, 1.0) );
    
    public final BFile.Entry< Integer                   > ART_BLUR_PASSES    = new BFile.Entry< Integer                   >( "__artBlurPasses"    , 2                           );
    public final BFile.Entry< String                    > ART_IMG_ENCODING   = new BFile.Entry< String                    >( "__artImgEncoding"   , "png"                       );
    
    public final BFile.Entry< BReference                > PLAY_CUR_AUDIO_REF = new BFile.Entry< BReference                >( "__playCurrentSong"  , NULL_BREFERENCE             );
    public final BFile.Entry< Double                    > PLAY_CUR_VOLUME    = new BFile.Entry< Double                    >( "__playCurVolume"    , 1.0                         );
	
    public final BFile.Entry< Double                    > UI_MOTION_BLUR_MUL = new BFile.Entry< Double                    >( "__uiMotionBlurMul"  , 2.0                         );
    public final BFile.Entry< Double                    > UI_ANIM_TIME_MUL   = new BFile.Entry< Double                    >( "__uiAnimTimeMul"    , 5.0                         );
    public final BFile.Entry< Double                    > UI_ART_SCALING     = new BFile.Entry< Double                    >( "__uiArtScaling"     , 0.9                         );
    public final BFile.Entry< Double                    > UI_TITLEBAR_VSIZE  = new BFile.Entry< Double                    >( "__uiTitlebarVSize"  , 25.0                        );
    public final BFile.Entry< Double                    > UI_LISTOPT_VSIZE   = new BFile.Entry< Double                    >( "__uiListOptVSize"   , 30.0                        );
    public final BFile.Entry< Double                    > UI_BLUR_RADIUS     = new BFile.Entry< Double                    >( "__uiBlurRadius"     , 0.1                         );
    public final BFile.Entry< Double                    > UI_SONG_ANIM_ISIZE = new BFile.Entry< Double                    >( "__uiSongAnimISize"  , 0.7                         );
    public final BFile.Entry< Double                    > UI_SONG_ANIM_OSIZE = new BFile.Entry< Double                    >( "__uiSongAnimOSize"  , 4.0                         );
    
	/** 
	 * @brief Constructor for SettingsModel, which stores all settings for the Bateleur application as a BFile-derived instance.
	 * @see   BFile
	 * @param settingsFile The local file into which the settings are to be stored.
	 */
	public SettingsModel(ResourceFile settingsFile) throws IOException {
		super(settingsFile);
	}
}
