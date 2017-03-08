package com.cozmoz.funraaga;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;


import com.cozmoz.funraaga.Utilities.Miscellaneous;
import com.cozmoz.funraaga.Utilities.Service_Variables;

public class SplashScreen_Activity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(SplashScreen_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != 0) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Service_Variables.MANIFEST_READ_EXTERNAL_STORAGE_VAL);

                }
            } else {
                applicationStartupActions();
            }
        } else {
            applicationStartupActions();
        }


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen_Activity.this, Home_Activity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Service_Variables.MANIFEST_READ_EXTERNAL_STORAGE_VAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                applicationStartupActions();
            } else {
                // User refused to grant permission.
            }
        }
    }

    public void applicationStartupActions() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Miscellaneous.loadOfflineVideos(getApplicationContext());
                Miscellaneous.listOfSongs(getApplicationContext());
                Miscellaneous.listOfAlbums(getApplicationContext());
                Miscellaneous.listOfArtists(getApplicationContext());
                Miscellaneous.listOfGeneres(getApplicationContext());
                Miscellaneous.listOfPlaylist(getApplicationContext());
            }
        }).start();
    }
}
