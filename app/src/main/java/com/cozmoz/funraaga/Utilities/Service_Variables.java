package com.cozmoz.funraaga.Utilities;


import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ganesh Subramaniyam on 9/21/2016.
 */
public class Service_Variables {

    public static int MANIFEST_READ_EXTERNAL_STORAGE_VAL=35;
    public static int MANIFEST_WRITE_EXTERNAL_STORAGE_VAL=36;
    public static String OFFLINE_IMAGE_PATH= Environment.getExternalStorageDirectory() + File.separator + "Funraaga" + File.separator;

    public static String RADIO_NAME = "ABC Tamil FM";
    public static String RADIO_LANGUAGE = "Tamizh";
    public static String RADIO_PLAY_URL = "http://198.154.106.101:8118/";
    public static Boolean RADIO_PLAY_PAUSE_LOCK=true;
    public static Handler RADIO_PLAY_PAUSE_HANDLER;

    public static Boolean VIDEO_ACTIVITY_LOCK=false;
    public static ArrayList<Video_Online_Item> VIDEO_ONLINE_LIST=new ArrayList<Video_Online_Item>();
    public static int VIDEO_ONLINE_STARTLIMIT=0;
    public static String[] VIDEO_ONLINE_FILTER_ITEMS = new String[]{
            "Recent Uploads","Most Viewed"
    };
    public static ArrayList<Video_Offline_Item> VIDEO_OFFLINE_LIST = new ArrayList<Video_Offline_Item>();

    public static ArrayList<Audio_Online_Item> AUDIO_ONLINE_LIST=new ArrayList<Audio_Online_Item>();
    public static int AUDIO_ONLINE_STARTLIMIT=0;
    public static String[] AUDIO_ONLINE_FILTER_ITEMS = new String[]{
            "Recent Uploads","Most Listened"
    };
    public static ArrayList<Music_Item> MUSIC_SONGS_LIST = new ArrayList<Music_Item>();
    public static int MUSIC_SONG_NUMBER = 0;
    public static boolean MUSIC_SONG_PAUSED = true;
    public static boolean MUSIC_SONG_CHANGED = false;
    public static Handler MUSIC_SONG_CHANGE_HANDLER;
    public static Handler MUSIC_PLAY_PAUSE_HANDLER;
    public static Handler MUSIC_PROGRESSBAR_HANDLER;

    public static ArrayList MUSIC_ALBUM_LIST=new ArrayList();
    public static ArrayList MUSIC_ALBUM_TRACKS=new ArrayList();

    public static ArrayList MUSIC_ARTIST_LIST=new ArrayList();
    public static ArrayList MUSIC_ARTIST_TRACKS=new ArrayList();

    public static ArrayList MUSIC_GENERE_LIST=new ArrayList();

    public static ArrayList MUSIC_FOLDERS_LIST=new ArrayList();

    public static ArrayList MUSIC_PLAYLIST_LIST=new ArrayList();

    public static ArrayList<Music_Item> MUSIC_CURRENT_PLAYLIST=new ArrayList<Music_Item>();

}
