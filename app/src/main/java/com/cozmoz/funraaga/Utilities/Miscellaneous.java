package com.cozmoz.funraaga.Utilities;

import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.cozmoz.funraaga.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ganesh Subramaniyam on 9/21/2016.
 */
public class Miscellaneous {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void listOfSongs(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor c = context.getContentResolver().query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null);
        c.moveToFirst();
        while (c.moveToNext()) {
            Music_Item songData = new Music_Item();

            String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String titleid=c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY));
            String artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String artistid=c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
            String album = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            long duration = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String data = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
            long albumId = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String composer = c.getString(c.getColumnIndex(MediaStore.Audio.Media.COMPOSER));

            songData.setTitle(title);
            songData.setTitleid(titleid);
            songData.setAlbum(album);
            songData.setArtist(artist);
            songData.setArtistid(artistid);
            songData.setDuration(duration);
            songData.setPath(data);
            songData.setAlbumId(albumId);
            songData.setComposer(composer);
            Service_Variables.MUSIC_SONGS_LIST.add(songData);
        }
        c.close();
    }

    public static void listOfAlbums(Context context) {
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        };
        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Media.ALBUM + " ASC");
            if (cursor != null) {
                cursor.moveToFirst();
                int position = 1;
                while (!cursor.isAfterLast()) {
                    Music_Album_Item albumobj = new Music_Album_Item();
                    albumobj.setAlbumid(cursor.getString(0).toString());
                    albumobj.setAlbumname(cursor.getString(1).toString());
                    albumobj.setAlbumtrackscount(Integer.parseInt(cursor.getString(2).toString()));
                    writeMusicAlbumart(context, Long.parseLong(cursor.getString(0)));
                    Service_Variables.MUSIC_ALBUM_LIST.add(albumobj);
                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Log.e("Media", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();

            }
        }
    }

    public static void listAlbumTracks(Context context,long albumid)
    {
        for (int i=0;i<Service_Variables.MUSIC_SONGS_LIST.size();i++)
        {
            Music_Item musicItemobj=new Music_Item();
            Music_Album_Tracks_Item musicAlbumTracksItemobj = new Music_Album_Tracks_Item();
            musicItemobj=Service_Variables.MUSIC_SONGS_LIST.get(i);
            if(musicItemobj.getAlbumId()==albumid)
            {
                musicAlbumTracksItemobj.setAlbumid(albumid);
                musicAlbumTracksItemobj.setTrackid(musicItemobj.getTitleid());
                musicAlbumTracksItemobj.setArtistname(musicItemobj.getArtist());
                musicAlbumTracksItemobj.setTrackname(musicItemobj.getTitle());
                musicAlbumTracksItemobj.setComposername(musicItemobj.getComposer());
                Service_Variables.MUSIC_ALBUM_TRACKS.add(musicAlbumTracksItemobj);
            }
        }
    }

    public static void listOfArtists(Context context) {
        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };
        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
            cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Media.ARTIST + " ASC");
            if (cursor != null) {
                cursor.moveToFirst();
                int position = 1;
                while (!cursor.isAfterLast()) {
                    Music_Artist_Item albumobj = new Music_Artist_Item();
                    albumobj.setArtistid(cursor.getString(0).toString());
                    albumobj.setArtistname(cursor.getString(1).toString());
                    albumobj.setArtisttrackscount(Integer.parseInt(cursor.getString(2).toString()));
                    Service_Variables.MUSIC_ARTIST_LIST.add(albumobj);
                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Log.e("Media", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

    public static void listArtistsTracks(Context context,String artistid)
    {
        for (int i=0;i<Service_Variables.MUSIC_SONGS_LIST.size();i++)
        {
            Music_Item musicItemobj;
            Music_Artist_Tracks_Item musicArtistTracksItemobj = new Music_Artist_Tracks_Item();
            musicItemobj=Service_Variables.MUSIC_SONGS_LIST.get(i);
            if(musicItemobj.getArtistid().equals(artistid))
            {
                musicArtistTracksItemobj.setAlbumid(musicItemobj.getAlbumId());
                musicArtistTracksItemobj.setTrackid(musicItemobj.getTitleid());
                musicArtistTracksItemobj.setArtistname(musicItemobj.getArtist());
                musicArtistTracksItemobj.setArtistid(artistid);
                musicArtistTracksItemobj.setTrackname(musicItemobj.getTitle());
                musicArtistTracksItemobj.setComposername(musicItemobj.getComposer());
                Service_Variables.MUSIC_ARTIST_TRACKS.add(musicArtistTracksItemobj);
            }
        }
    }

    public static void listOfGeneres(Context context) {
        String[] projection = {
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
        };
        String[] projection1 = {
                MediaStore.Audio.Media.DISPLAY_NAME
        };
        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Music_Genere_Item genereobj = new Music_Genere_Item();
                    genereobj.setGenereid(cursor.getString(0).toString());
                    genereobj.setGenerename(cursor.getString(1).toString());
                    Uri tempuri = MediaStore.Audio.Genres.Members.getContentUri("external", Long.parseLong(cursor.getString(0)));
                    Cursor tempcursor = context.getContentResolver().query(tempuri, projection1, null, null, null);
                    if (tempcursor != null) {
                        int trackcount = 0;
                        tempcursor.moveToFirst();
                        while (!tempcursor.isAfterLast()) {
                            trackcount = trackcount + 1;
                            tempcursor.moveToNext();
                        }
                        genereobj.setGeneretrackscount(trackcount);
                    }
                    Service_Variables.MUSIC_GENERE_LIST.add(genereobj);
                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Log.e("Media", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

    public static void listOfPlaylist(Context context) {
        String[] projection = {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };
        String[] projection1 = {
                MediaStore.Audio.Media.DISPLAY_NAME
        };
        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Music_Playlist_Item playlistobj = new Music_Playlist_Item();
                    playlistobj.setPlaylistid(cursor.getString(0).toString());
                    playlistobj.setPlaylistname(cursor.getString(1).toString());
                    Uri tempuri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(cursor.getString(0)));
                    Cursor tempcursor = context.getContentResolver().query(tempuri, projection1, null, null, null);
                    if (tempcursor != null) {
                        int trackcount = 0;
                        tempcursor.moveToFirst();
                        while (!tempcursor.isAfterLast()) {
                            trackcount = trackcount + 1;
                            tempcursor.moveToNext();
                        }
                        playlistobj.setPlaylisttrackcount(trackcount);
                    }
                    Service_Variables.MUSIC_PLAYLIST_LIST.add(playlistobj);
                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Log.e("Media", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

    public static void writeMusicAlbumart(Context context, long albumId) {
        FileOutputStream out = null;
        File posterimg = new File(Service_Variables.OFFLINE_IMAGE_PATH + "Aud_" + albumId + ".png");
        if (posterimg.exists() == false) {
            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), albumArtUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.funraaga_logo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File bMapfile = new File(Service_Variables.OFFLINE_IMAGE_PATH + "Aud_" + albumId + ".png");
            try {
                out = new FileOutputStream(bMapfile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    Log.d("Error1", e.getMessage());
                }
            }
        }
    }

    public static Bitmap getAlbumart(Context context, Long album_id) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                pfd = null;
                fd = null;
            }
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_art, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        return false;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return true;
        }
        return false;
    }

    public static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].contains(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    public String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static void loadOnlineVideos(String streamoutput) {
        try {
            if (streamoutput != null) {
                JSONArray videoarray = new JSONArray(streamoutput);
                for (int i = 0; i < videoarray.length(); i++) {
                    Video_Online_Item vidonlineobj = new Video_Online_Item();
                    JSONObject vidobj = (JSONObject) videoarray.get(i);
                    vidonlineobj.setTitle(vidobj.getString("Title"));
                    vidonlineobj.setRawfile(vidobj.getString("Rawfile"));
                    vidonlineobj.setPoster(vidobj.getString("Poster"));
                    vidonlineobj.setVideotype(vidobj.getString("VidType"));
                    vidonlineobj.setViews(vidobj.getString("Views"));
                    Service_Variables.VIDEO_ONLINE_LIST.add(vidonlineobj);
                }
            }
        } catch (Exception e) {
            Log.w("Error - online", e.getMessage());
        }
    }

    public static void loadOfflineVideos(final Context context) {
        FileOutputStream out;
        ArrayList temptitle = new ArrayList();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.DATA};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
        int vidsCount = 0;
        if (c != null) {
            vidsCount = c.getCount();
            Log.w("Offline Video Count - ", Integer.toString(vidsCount));
            while (c.moveToNext()) {
                if (temptitle.contains(c.getString(0).substring(c.getString(0).lastIndexOf('/') + 1, c.getString(0).length())) == false) {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    File F = new File(c.getString(0));
                    if (F.exists()) {
                        Video_Offline_Item vidofflineobj = new Video_Offline_Item();
                        temptitle.add(c.getString(0).substring(c.getString(0).lastIndexOf('/') + 1, c.getString(0).length()));
                        vidofflineobj.setTitle(c.getString(0).substring(c.getString(0).lastIndexOf('/') + 1, c.getString(0).length()));
                        vidofflineobj.setPath(c.getString(0));
                        retriever.setDataSource(context, Uri.fromFile(F));
                        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        Double timeInMillisec = Double.parseDouble(time);
                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.CEILING);
                        Double Dtime = timeInMillisec / 60000;
                        vidofflineobj.setDuration(df.format(Dtime));
                        String tempfilename = "";
                        tempfilename = c.getString(0).substring(c.getString(0).lastIndexOf('/') + 1, c.getString(0).length());
                        tempfilename = tempfilename.substring(0, tempfilename.lastIndexOf("."));
                        File posterimg = new File(Service_Variables.OFFLINE_IMAGE_PATH + tempfilename + ".png");
                        if (posterimg.exists() == false) {
                            File file = new File(c.getString(0));
                            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                            File bMapfile = new File(Service_Variables.OFFLINE_IMAGE_PATH + tempfilename + ".png");
                            try {
                                out = new FileOutputStream(bMapfile);
                                bMap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                if (out != null) {
                                    out.flush();
                                    out.close();
                                    vidofflineobj.setPoster("file://" + Service_Variables.OFFLINE_IMAGE_PATH + tempfilename + ".png");
                                }
                            } catch (Exception e) {
                                Log.d("Error", e.getMessage());
                            }
                        } else {
                            vidofflineobj.setPoster("file://" + Service_Variables.OFFLINE_IMAGE_PATH + tempfilename + ".png");
                        }
                        Service_Variables.VIDEO_OFFLINE_LIST.add(vidofflineobj);
                    }
                }
            }
            c.close();
            Service_Variables.VIDEO_ACTIVITY_LOCK = true;
        }
    }

    public static void loadOnlineAudios(String streamoutput) {
        try {
            if (streamoutput != null) {
                JSONArray audioarray = new JSONArray(streamoutput);
                for (int i = 0; i < audioarray.length(); i++) {
                    Audio_Online_Item musiconlineobj = new Audio_Online_Item();
                    JSONObject audobj = (JSONObject) audioarray.get(i);
                    musiconlineobj.setAlbumname(audobj.getString("Title"));
                    musiconlineobj.setAlbumid(audobj.getString("AlbumId"));
                    musiconlineobj.setAlbumposter(audobj.getString("Poster"));
                    musiconlineobj.setAlbumtype(audobj.getString("AudType"));
                    musiconlineobj.setAlbumviews(audobj.getString("Views"));
                    Service_Variables.AUDIO_ONLINE_LIST.add(musiconlineobj);
                }
            }
        } catch (Exception e) {

        }
    }
}
