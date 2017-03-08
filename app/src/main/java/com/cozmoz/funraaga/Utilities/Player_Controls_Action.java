package com.cozmoz.funraaga.Utilities;

import android.content.Context;

import com.cozmoz.funraaga.R;
import com.cozmoz.funraaga.Service.Music_Notification_Service;

public class Player_Controls_Action {

    public static void radioPlayControl(Context context) {
        Service_Variables.RADIO_PLAY_PAUSE_LOCK=false;
        radioSendMessage(context.getResources().getString(R.string.play));
    }

    public static void radioPauseControl(Context context) {
        Service_Variables.RADIO_PLAY_PAUSE_LOCK=true;
        radioSendMessage(context.getResources().getString(R.string.pause));
    }

    public static void radioNextControl(Context context) {
        radioSendMessage("Next");
    }
    private static void radioSendMessage(String message) {
        try{
            Service_Variables.RADIO_PLAY_PAUSE_HANDLER.sendMessage(Service_Variables.RADIO_PLAY_PAUSE_HANDLER.obtainMessage(0, message));
        }catch(Exception e){}
    }
    private static void musicSendMessage(String message) {
        try{
            Service_Variables.MUSIC_PLAY_PAUSE_HANDLER.sendMessage(Service_Variables.MUSIC_PLAY_PAUSE_HANDLER.obtainMessage(0, message));
        }catch(Exception e){}
    }

    public static void musicPlayControl(Context context) {
        musicSendMessage(context.getResources().getString(R.string.play));
    }

    public static void musicPauseControl(Context context) {
        musicSendMessage(context.getResources().getString(R.string.pause));
    }

    public static void musicNextControl(Context context) {
        boolean isServiceRunning = Miscellaneous.isServiceRunning(Music_Notification_Service.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(Service_Variables.MUSIC_SONGS_LIST.size() > 0 ){
            if(Service_Variables.MUSIC_SONG_NUMBER < (Service_Variables.MUSIC_SONGS_LIST.size()-1)){
                Service_Variables.MUSIC_SONG_NUMBER++;
                Service_Variables.MUSIC_SONG_CHANGE_HANDLER.sendMessage(Service_Variables.MUSIC_SONG_CHANGE_HANDLER.obtainMessage());
            }else{
                Service_Variables.MUSIC_SONG_NUMBER = 0;
                Service_Variables.MUSIC_SONG_CHANGE_HANDLER.sendMessage(Service_Variables.MUSIC_SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
        Service_Variables.MUSIC_SONG_PAUSED = false;
    }

    public static void musicPreviousControl(Context context) {
        boolean isServiceRunning = Miscellaneous.isServiceRunning(Music_Notification_Service.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(Service_Variables.MUSIC_SONGS_LIST.size() > 0 ){
            if(Service_Variables.MUSIC_SONG_NUMBER > 0){
                Service_Variables.MUSIC_SONG_NUMBER--;
                Service_Variables.MUSIC_SONG_CHANGE_HANDLER.sendMessage(Service_Variables.MUSIC_SONG_CHANGE_HANDLER.obtainMessage());
            }else{
                Service_Variables.MUSIC_SONG_NUMBER = Service_Variables.MUSIC_SONGS_LIST.size() - 1;
                Service_Variables.MUSIC_SONG_CHANGE_HANDLER.sendMessage(Service_Variables.MUSIC_SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
        Service_Variables.MUSIC_SONG_PAUSED = false;
    }

}
