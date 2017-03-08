package com.cozmoz.funraaga.httpHandler;

import android.os.AsyncTask;

/**
 * Created by Ganesh Subramaniyam on 10/1/2016.
 */

public class Data_Transmission {
    public static void radioPlayCount(int radioid) {
        String process_url = "http://www.funraaga.in/Action_Page_app_m.php?Action=radioCountIncrement&RadioId="+radioid;
        new ProcessJSON().execute(process_url);
    }
    public static void videoPlayCount(String videoid) {
        String process_url = "http://www.funraaga.in/Action_Page_app_m.php?Action=videoCountIncrement&VideoId="+videoid;
        new ProcessJSON().execute(process_url);
    }
    public static void albumPlayCount(String albumid)
    {
        String process_url = "http://www.funraaga.in/Action_Page_app_m.php?Action=albumCountIncrement&VideoId="+albumid;
        new ProcessJSON().execute(process_url);
    }
    public static void trackPlayCount(int trackid)
    {
        String process_url = "http://www.funraaga.in/Action_Page_app_m.php?Action=trackCountIncrement&VideoId="+trackid;
        new ProcessJSON().execute(process_url);
    }

    static class ProcessJSON extends AsyncTask<String, Void, String> {
        String stream = null;
        Data_Model hh;

        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];
            hh = new Data_Model();
            stream = hh.HttpDataHandler(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String stream) {
            if (stream.equals("Success")) {

            } else {

            }
        }

    }
}
