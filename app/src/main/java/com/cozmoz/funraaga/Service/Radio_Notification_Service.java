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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.cozmoz.funraaga.R;
import com.cozmoz.funraaga.Radio_Activity;
import com.cozmoz.funraaga.Utilities.*;
import com.cozmoz.funraaga.BroadcastReceiver.Radio_BroadCast_Receiver;

public class Radio_Notification_Service extends Service implements AudioManager.OnAudioFocusChangeListener{
    private MediaPlayer mp;
    int NOTIFICATION_ID = 1111;
    public static final String NOTIFY_DELETE = "com.cozmoz.funraaga.radiodelete";
    public static final String NOTIFY_RADIO_PAUSE = "com.cozmoz.funraaga.radiopause";
    public static final String NOTIFY_RADIO_PLAY = "com.cozmoz.funraaga.radioplay";

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
        super.onCreate();
    }

    private class MainTask extends TimerTask{
        public void run(){
            handler.sendEmptyMessage(0);
        }
    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){

        }
    };

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if(currentVersionSupportLockScreenControls){
                RegisterRemoteClient();
            }
            String radioUrl = Service_Variables.RADIO_PLAY_URL;
            playRadio(radioUrl);
            newNotification();

            Service_Variables.RADIO_PLAY_PAUSE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String message = (String)msg.obj;
                    if(mp == null)
                        return false;
                    if(message.equalsIgnoreCase(getResources().getString(R.string.play))){
                        Service_Variables.RADIO_PLAY_PAUSE_LOCK = false;
                        if(currentVersionSupportLockScreenControls){
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        mp.start();
                    }else if(message.equalsIgnoreCase(getResources().getString(R.string.pause))){
                        Service_Variables.RADIO_PLAY_PAUSE_LOCK = true;
                        if(currentVersionSupportLockScreenControls){
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                        }
                        mp.pause();
                    }else{
                        Service_Variables.RADIO_PLAY_PAUSE_LOCK = false;
                        if(currentVersionSupportLockScreenControls){
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
                        }
                        playRadio(Service_Variables.RADIO_PLAY_URL);
                    }
                    newNotification();
                    try{
                        Radio_Activity.updateUI();
                        Radio_Activity.changeButton();
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

    @SuppressLint("NewApi")
    private void newNotification() {
        String radioName = Service_Variables.RADIO_NAME;
        String radioLang = Service_Variables.RADIO_LANGUAGE;
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.radio_custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.radio_notification);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_media_play)
                .setContentTitle(radioName).build();

        setListeners(simpleContentView);
        setListeners(expandedView);

        notification.contentView = simpleContentView;
        if(currentVersionSupportBigNotification){
            notification.bigContentView = expandedView;
        }

        if(Service_Variables.RADIO_PLAY_PAUSE_LOCK){
            notification.contentView.setViewVisibility(R.id.btnRadioPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnRadioPlay, View.VISIBLE);

            if(currentVersionSupportBigNotification){
                notification.bigContentView.setViewVisibility(R.id.btnRadioPause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btnRadioPlay, View.VISIBLE);
            }
        }else{
            notification.contentView.setViewVisibility(R.id.btnRadioPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnRadioPlay, View.GONE);

            if(currentVersionSupportBigNotification){
                notification.bigContentView.setViewVisibility(R.id.btnRadioPause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btnRadioPlay, View.GONE);
            }
        }

        notification.contentView.setTextViewText(R.id.notificationRadioName, radioName);
        notification.contentView.setTextViewText(R.id.notificationRadioLang, radioLang);
        if(currentVersionSupportBigNotification){
            notification.bigContentView.setTextViewText(R.id.notificationRadioName, radioName);
            notification.bigContentView.setTextViewText(R.id.notificationRadioLang, radioLang);
        }
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Notification click listeners
     * @param view
     */
    public void setListeners(RemoteViews view) {
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_RADIO_PAUSE);
        Intent play = new Intent(NOTIFY_RADIO_PLAY);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnRadioDelete, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnRadioPause, pPause);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnRadioPlay, pPlay);

    }

    @Override
    public void onDestroy() {
        if(mp != null){
            mp.stop();
            mp = null;
            Service_Variables.RADIO_PLAY_PAUSE_LOCK=true;
            Radio_Activity.updateUI();
            Radio_Activity.changeButton();
        }
        super.onDestroy();
    }


    @SuppressLint("NewApi")
    private void playRadio(String radioUrl) {
        try {
            if(currentVersionSupportLockScreenControls){
                //UpdateMetadata(data);
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            }
            mp.reset();
            mp.setDataSource(radioUrl);
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    Radio_Activity.updateUI();
                    Radio_Activity.changeButton();
                    timer.scheduleAtFixedRate(new MainTask(), 0, 100);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void RegisterRemoteClient(){
        remoteComponentName = new ComponentName(getApplicationContext(), new Radio_BroadCast_Receiver().ComponentName());
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

    @Override
    public void onAudioFocusChange(int focusChange) {}
}