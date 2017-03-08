package com.cozmoz.funraaga.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.cozmoz.funraaga.Service.Radio_Notification_Service;
import com.cozmoz.funraaga.Utilities.Player_Controls_Action;
import com.cozmoz.funraaga.Utilities.Service_Variables;

public class Radio_BroadCast_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if(!Service_Variables.RADIO_PLAY_PAUSE_LOCK){
                        Player_Controls_Action.radioPauseControl(context);
                    }else{
                        Player_Controls_Action.radioPlayControl(context);
                    }
                    break;
            }
        }  else{
            if (intent.getAction().equals(Radio_Notification_Service.NOTIFY_RADIO_PLAY)) {
                Player_Controls_Action.radioPlayControl(context);
            } else if (intent.getAction().equals(Radio_Notification_Service.NOTIFY_RADIO_PAUSE)) {
                Player_Controls_Action.radioPauseControl(context);
            } else if (intent.getAction().equals(Radio_Notification_Service.NOTIFY_DELETE)) {
                Intent i = new Intent(context, Radio_Notification_Service.class);
                context.stopService(i);
            }
        }
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}
