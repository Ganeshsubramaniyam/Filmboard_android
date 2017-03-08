package com.cozmoz.funraaga.Service;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.cozmoz.funraaga.Audio_Activity;
import com.cozmoz.funraaga.BroadcastReceiver.Music_Broadcast_Receiver;
import com.cozmoz.funraaga.R;
import com.cozmoz.funraaga.Utilities.*;

public class Music_Notification_Service extends Service implements AudioManager.OnAudioFocusChangeListener{
    String LOG_CLASS = "SongService";
    private MediaPlayer mp;
    int NOTIFICATION_ID = 1111;
    public static final String NOTIFY_PREVIOUS = "com.cozmoz.funraaga.musicprevious";
    public static final String NOTIFY_DELETE = "com.cozmoz.funraaga.musicdelete";
    public static final String NOTIFY_PAUSE = "com.cozmoz.funraaga.musicpause";
    public static final String NOTIFY_PLAY = "com.cozmoz.funraaga.musicplay";
    public static final String NOTIFY_NEXT = "com.cozmoz.funraaga.musicnext";

    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    AudioManager audioManager;
    Bitmap mDummyAlbumArt;
    private static Timer timer;
    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mp = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        currentVersionSupportBigNotification = Miscellaneous.currentVersionSupportBigNotification();
        currentVersionSupportLockScreenControls = Miscellaneous.currentVersionSupportLockScreenControls();
        timer = new Timer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Player_Controls_Action.musicNextControl(getApplicationContext());
            }
        });
        super.onCreate();
    }

    /**
     * Send message from timer
     * @author jonty.ankit
     */
    private class MainTask extends TimerTask{
        public void run(){
            handler.sendEmptyMessage(0);
        }
    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(mp != null){
                int progress = (mp.getCurrentPosition()*100) / mp.getDuration();
                Integer i[] = new Integer[3];
                i[0] = mp.getCurrentPosition();
                i[1] = mp.getDuration();
                i[2] = progress;
                try{
                    Service_Variables.MUSIC_PROGRESSBAR_HANDLER.sendMessage(Service_Variables.MUSIC_PROGRESSBAR_HANDLER.obtainMessage(0, i));
                }catch(Exception e){}
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if(Service_Variables.MUSIC_CURRENT_PLAYLIST.size() <= 0){
                Miscellaneous.listOfSongs(getApplicationContext());
                Service_Variables.MUSIC_CURRENT_PLAYLIST=Service_Variables.MUSIC_SONGS_LIST;
            }
            Music_Item data = Service_Variables.MUSIC_CURRENT_PLAYLIST.get(Service_Variables.MUSIC_SONG_NUMBER);
            if(currentVersionSupportLockScreenControls){
                RegisterRemoteClient();
            }
            String songPath = data.getPath();
            playSong(songPath, data);
            newNotification();

            Service_Variables.MUSIC_SONG_CHANGE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Music_Item data = Service_Variables.MUSIC_CURRENT_PLAYLIST.get(Service_Variables.MUSIC_SONG_NUMBER);
                    String songPath = data.getPath();
                    newNotification();
                    try{
                        playSong(songPath, data);
                        Audio_Activity.changebutton();
                        Audio_Activity.updateui();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            Service_Variables.MUSIC_PLAY_PAUSE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String message = (String)msg.obj;
                    if(mp == null)
                        return false;
                    if(message.equalsIgnoreCase(getResources().getString(R.string.play))){
                        Service_Variables.MUSIC_SONG_PAUSED = false;
                        if(currentVersionSupportLockScreenControls){
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        mp.start();
                    }else if(message.equalsIgnoreCase(getResources().getString(R.string.pause))){
                        Service_Variables.MUSIC_SONG_PAUSED = true;
                        if(currentVersionSupportLockScreenControls){
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                        }
                        mp.pause();
                    }
                    newNotification();
                    try{
                        Audio_Activity.changebutton();
                        Audio_Activity.updateui();
                    }catch(Exception e){}
                    Log.d("TAG", "TAG Pressed: " + message);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    /**
     * Notification
     * Custom Bignotification is available from API 16
     */
    @SuppressLint("NewApi")
    private void newNotification() {
        String songName = Service_Variables.MUSIC_CURRENT_PLAYLIST.get(Service_Variables.MUSIC_SONG_NUMBER).getTitle();
        String albumName = Service_Variables.MUSIC_CURRENT_PLAYLIST.get(Service_Variables.MUSIC_SONG_NUMBER).getAlbum();
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.music_custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.music_notification);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.funraaga_logo)
                .setContentTitle(songName).build();

        setListeners(simpleContentView);
        setListeners(expandedView);

        notification.contentView = simpleContentView;
        if(currentVersionSupportBigNotification){
            notification.bigContentView = expandedView;
        }

        try{
            long albumId = Service_Variables.MUSIC_CURRENT_PLAYLIST.get(Service_Variables.MUSIC_SONG_NUMBER).getAlbumId();
            Bitmap albumArt = Miscellaneous.getAlbumart(getApplicationContext(), albumId);
            if(albumArt != null){
                notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                if(currentVersionSupportBigNotification){
                    notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, albumArt);
                }
            }else{
                notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                if(currentVersionSupportBigNotification){
                    notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(Service_Variables.MUSIC_SONG_PAUSED){
            notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

            if(currentVersionSupportBigNotification){
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
            }
        }else{
            notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

            if(currentVersionSupportBigNotification){
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
            }
        }

        notification.contentView.setTextViewText(R.id.textSongName, songName);
        notification.contentView.setTextViewText(R.id.textAlbumName, albumName);
        if(currentVersionSupportBigNotification){
            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
            notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);
        }
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Notification click listeners
     * @param view
     */
    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

    }

    @Override
    public void onDestroy() {
        if(mp != null){
            mp.stop();
            mp = null;
        }
        Service_Variables.RADIO_PLAY_PAUSE_LOCK=false;
        Audio_Activity.changebutton();
        Audio_Activity.updateui();
        super.onDestroy();
    }

    /**
     * Play song, Update Lockscreen fields
     * @param songPath
     * @param data
     */
    @SuppressLint("NewApi")
    private void playSong(String songPath, Music_Item data) {
        try {
            if(currentVersionSupportLockScreenControls){
                UpdateMetadata(data);
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            }
            mp.reset();
            mp.setDataSource(songPath);
            mp.prepare();
            mp.start();
            timer.scheduleAtFixedRate(new MainTask(), 0, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Audio_Activity.updateui();
            Audio_Activity.changebutton();
        }
    }

    @SuppressLint("NewApi")
    private void RegisterRemoteClient(){
        remoteComponentName = new ComponentName(getApplicationContext(), new Music_Broadcast_Receiver().ComponentName());
        try {
            if(remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        }catch(Exception ex) {
        }
    }

    @SuppressLint("NewApi")
    private void UpdateMetadata(Music_Item data){
        if (remoteControlClient == null)
            return;
        MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, data.getAlbum());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, data.getArtist());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, data.getTitle());
        mDummyAlbumArt = Miscellaneous.getAlbumart(getApplicationContext(), data.getAlbumId());
        if(mDummyAlbumArt == null){
            mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.default_album_art);
        }
        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
        metadataEditor.apply();
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {}
}