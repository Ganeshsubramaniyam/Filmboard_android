package com.cozmoz.funraaga;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.GregorianCalendar;

public class Download_Activity extends AppCompatActivity {

    private AccountHeader headerResult = null;
    private Drawer result = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    ImageLoader imageLoader;
    DisplayImageOptions imgDisplayOptions;
    ImageLoaderConfiguration config;
    String Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_activity);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4CAFC2")));
        Bundle bundle = getIntent().getExtras();
        String FileName = bundle.getString("FileName");
        Type = bundle.getString("Type");
        final String RawFile = bundle.getString("RawFile");
        String Category = bundle.getString("Category");
        String Imageurl = bundle.getString("ImageUrl");
        TextView vfilename = (TextView) findViewById(R.id.downloadfilename);
        TextView vcategory = (TextView) findViewById(R.id.downloadmovieoralbum);
        ImageView vposter = (ImageView) findViewById(R.id.downloadposter);
        Button vdownload = (Button) findViewById(R.id.downloadbutton);
        final WebView vwebView = (WebView) findViewById(R.id.webView);
        vfilename.setText("File Name : " + FileName);
        vcategory.setText("Category : " + Category);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8205498786717408~1317926905");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .setGender(AdRequest.GENDER_FEMALE)
                .setBirthday(new GregorianCalendar(1990, 9, 1).getTime())
                .tagForChildDirectedTreatment(true)
                .build();
        mAdView.loadAd(request);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
        config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheSize(104857600)
                .memoryCacheSizePercentage(50)
                .discCacheSize(104857600)
                .threadPoolSize(50)
                .build();

        imgDisplayOptions = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(10))
                .cacheInMemory()
                .cacheOnDisc()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        ImageAware imgawr = new ImageViewAware(vposter, false);
        imageLoader.displayImage(Imageurl, imgawr, imgDisplayOptions);
        vwebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                final String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        });
        vdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Type.equals("0")) {
                    vwebView.loadUrl("http://www.funraaga.in/Downloads_View.php?VideoId=" + RawFile);
                } else {
                    vwebView.loadUrl("http://www.funraaga.in/Downloads_View.php?AlbumId=" + RawFile);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
