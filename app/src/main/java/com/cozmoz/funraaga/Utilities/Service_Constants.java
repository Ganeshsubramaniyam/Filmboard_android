package com.cozmoz.funraaga.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cozmoz.funraaga.R;

/**
 * Created by Ganesh Subramaniyam on 9/19/2016.
 */
public class Service_Constants {

    public interface ACTION {
        public static String MUSIC_MAIN_ACTION = "com.cozmoz.funraaga.action.musicmain";
        public static String MUSIC_PLAY_ACTION = "com.cozmoz.funraaga.action.musicplay";
        public static String MUSIC_STARTFOREGROUND_ACTION = "com.cozmoz.funraaga.action.musicstartforeground";
        public static String MUSIC_STOPFOREGROUND_ACTION = "com.cozmoz.funraaga.action.musicstopforeground";
        public static String INIT_ACTION = "com.cozmoz.funraaga.action.init";
        public static String MUSIC_PREV_ACTION = "com.cozmoz.funraaga.action.musicprev";
        public static String MUSIC_NEXT_ACTION = "com.cozmoz.funraaga.action.musicnext";
        public static String RADIO_MAIN_ACTION = "com.cozmoz.funraaga.action.main";
        public static String RADIO_PLAY_ACTION = "com.cozmoz.funraaga.action.radioplay";
        public static String RADIO_STARTFOREGROUND_ACTION = "com.cozmoz.funraaga.action.radiostartforeground";
        public static String RADIO_STOPFOREGROUND_ACTION = "com.cozmoz.funraaga.action.radiostopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultRadioAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.fm_logo, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }
}
