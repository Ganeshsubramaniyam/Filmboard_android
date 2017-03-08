package com.cozmoz.funraaga.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.cozmoz.funraaga.Service.Music_Notification_Service;
import com.cozmoz.funraaga.Utilities.Player_Controls_Action;
import com.cozmoz.funraaga.Utilities.Service_Variables;

public class Music_Broadcast_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if(!Service_Variables.MUSIC_SONG_PAUSED){
                        Player_Controls_Action.musicPauseControl(context);
                    }else{
                        Player_Controls_Action.musicPlayControl(context);
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                    Player_Controls_Action.musicNextControl(context);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                    Player_Controls_Action.musicPreviousControl(context);
                    break;
            }
        }  else{
            if (intent.getAction().equals(Music_Notification_Service.NOTIFY_PLAY)) {
                Player_Controls_Action.musicPlayControl(context);
            } else if (intent.getAction().equals(Music_Notification_Service.NOTIFY_PAUSE)) {
                Player_Controls_Action.musicPauseControl(context);
            } else if (intent.getAction().equals(Music_Notification_Service.NOTIFY_NEXT)) {
                Player_Controls_Action.musicNextControl(context);
            } else if (intent.getAction().equals(Music_Notification_Service.NOTIFY_DELETE)) {
                Intent i = new Intent(context, Music_Notification_Service.class);
                context.stopService(i);
                //Intent in = new Intent(context, MainActivity.class);
                //in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //context.startActivity(in);
            }else if (intent.getAction().equals(Music_Notification_Service.NOTIFY_PREVIOUS)) {
                Player_Controls_Action.musicPreviousControl(context);
            }
        }
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}
