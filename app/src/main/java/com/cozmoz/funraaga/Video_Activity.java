package com.cozmoz.funraaga;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaCodecInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;
import com.cozmoz.funraaga.Utilities.Miscellaneous;
import com.cozmoz.funraaga.Utilities.Service_Variables;
import com.cozmoz.funraaga.Utilities.Video_Offline_Item;
import com.cozmoz.funraaga.Utilities.Video_Online_Item;
import com.cozmoz.funraaga.httpHandler.Data_Model;
import com.cozmoz.funraaga.httpHandler.Data_Transmission;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Video_Activity extends AppCompatActivity
        implements Video_Fragment.OnFragmentInteractionListener, Video_Offline_Fragment.OnFragmentInteractionListener {

    private AccountHeader headerResult = null;
    private Drawer result = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    FragmentManager manager;
    FragmentTransaction transaction;
    Fragment fragment;
    int filterpos = 0;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    DisplayImageOptions imgDisplayOptions;
    ImageLoaderConfiguration config;
    ImageButton btnshare;
    ImageButton btndownload;
    GridView grid;
    boolean Filterlock = false;
    boolean adapterlock = false;
    String Process_URL;
    Spinner Videofilter;
    Video_Grid videoadapter = null;
    Video_Grid_Offline video_off_adapter = null;
    ProgressBar videocontentloading;
    Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);
        setNavigationdrawer(savedInstanceState);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        fragment = new Fragment();
        fragment = new Video_Fragment();
        myTimer = new Timer();
        myTimer.schedule(new loaderTask(), 2500);
        Process_URL = "http://www.funraaga.in/Action_Page_app_m.php?Action=getVideoList&Startlimit="+ Service_Variables.VIDEO_ONLINE_STARTLIMIT;
        Service_Variables.VIDEO_ONLINE_STARTLIMIT=Service_Variables.VIDEO_ONLINE_STARTLIMIT+20;
        transaction.replace(R.id.appcontent, fragment);
        transaction.commit();
        new ProcessJSON().execute(Process_URL);
    }

    @Override
    public void onFragmentInteractionVideo(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteractionVideoOffline(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        /*if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }*/
    }

    public void setNavigationdrawer(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        final IProfile profile = new ProfileDrawerItem().withName("Funraaga").withEmail("A Place for Entertainment").withIcon(R.drawable.funraaga_logo);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.nav_bg)
                .addProfiles(profile)
                .withSelectionListEnabledForSingleProfile(false)
                .withCloseDrawerOnProfileListClick(false)
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.nav_item_home).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.nav_item_radio).withIcon(GoogleMaterial.Icon.gmd_radio).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.nav_item_video).withIcon(GoogleMaterial.Icon.gmd_collection_video).withBadge("22").withBadgeStyle(new BadgeStyle(Color.RED, Color.RED)).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.nav_item_audio).withIcon(FontAwesome.Icon.faw_music).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.nav_item_tv).withIcon(FontAwesome.Icon.faw_television).withIdentifier(4),
                        new PrimaryDrawerItem().withName(R.string.nav_item_contactus).withIcon(GoogleMaterial.Icon.gmd_format_color_fill),
                        new SectionDrawerItem().withName("Preferences"),
                        new SecondaryDrawerItem().withName(R.string.nav_item_settings).withIcon(GoogleMaterial.Icon.gmd_settings),
                        new SectionDrawerItem().withName("Who We Are"),
                        new SecondaryDrawerItem().withName(R.string.nav_item_companyinfo).withIcon(GoogleMaterial.Icon.gmd_view_web),
                        new SectionDrawerItem().withName("")
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            String itemclicked = ((Nameable) drawerItem).getName().getText(Video_Activity.this).toString();
                            result.getDrawerLayout().closeDrawer(GravityCompat.START);
                            if (getTitle().equals(itemclicked) == false) {
                                if (itemclicked.equalsIgnoreCase("Home") == true) {
                                    Intent i = new Intent(Video_Activity.this, Home_Activity.class);
                                    startActivity(i);
                                } else if (itemclicked.equalsIgnoreCase("Radios") == true) {
                                    Intent i = new Intent(Video_Activity.this, Radio_Activity.class);
                                    startActivity(i);
                                } else if (itemclicked.equalsIgnoreCase("Videos") == true) {
                                    Intent i = new Intent(Video_Activity.this, Video_Activity.class);
                                    startActivity(i);
                                }
                            }
                        }
                        //we do not consume the event and want the Drawer to continue with the event chain
                        return false;
                    }
                })
                .withSelectedItem(2)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();


        //get the CrossfadeDrawerLayout which will be used as alternative DrawerLayout for the Drawer
        //the CrossfadeDrawerLayout library can be found here: https://github.com/mikepenz/CrossfadeDrawerLayout
        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();
        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
        //add second view (which is the miniDrawer)

        final MiniDrawer miniResult = result.getMiniDrawer();

        //build the view for the MiniDrawer
        View view = miniResult.build(this);
        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });
    }

    class ProcessJSON extends AsyncTask<String, Void, String> {
        String stream = null;
        Data_Model hh;

        @Override
        protected String doInBackground(String... strings) {
            Service_Variables.VIDEO_ACTIVITY_LOCK=false;
            String urlString = strings[0];
            hh = new Data_Model();
            stream = hh.HttpDataHandler(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String stream) {
            if(stream!=null)
            {
                Miscellaneous.loadOnlineVideos(stream);
            }
            else
            {
                Snackbar.make(findViewById(android.R.id.content), "Please check your Internet Connection", Snackbar.LENGTH_LONG)
                        .show();
            }
            grid = null;
            if (videoadapter == null) {
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(mViewPager);
                video_off_adapter = new Video_Grid_Offline(Video_Activity.this, Service_Variables.VIDEO_OFFLINE_LIST);
                GridView grid_off = (GridView) findViewById(R.id.offlinevideocontents);
                grid_off.setAdapter(video_off_adapter);
                videoadapter = new Video_Grid(Video_Activity.this, Service_Variables.VIDEO_ONLINE_LIST);
                grid = (GridView) findViewById(R.id.videocontents);
                grid.setAdapter(videoadapter);
                videocontentloading=(ProgressBar)findViewById(R.id.videocontentloading);
                grid.setVisibility(View.VISIBLE);
                videocontentloading.setVisibility(View.GONE);
            } else {
                grid = (GridView) findViewById(R.id.videocontents);
                videoadapter = new Video_Grid(Video_Activity.this, Service_Variables.VIDEO_ONLINE_LIST);
                videoadapter.notifyDataSetChanged();
                grid.invalidateViews();
                videocontentloading=(ProgressBar)findViewById(R.id.videocontentloading);
                grid.setVisibility(View.VISIBLE);
                videocontentloading.setVisibility(View.GONE);
            }
            Videofilter = (Spinner) findViewById(R.id.vidfilter);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    Video_Activity.this, R.layout.filter_spinner_items, Service_Variables.VIDEO_ONLINE_FILTER_ITEMS
            );
            spinnerArrayAdapter.setDropDownViewResource(R.layout.filter_spinner_items);
            Videofilter.setAdapter(spinnerArrayAdapter);
            Videofilter.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            if (filterpos == 0) {
                Videofilter.setSelection(0, false);
            } else {
                Videofilter.setSelection(1, false);
            }
            Videofilter.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Filterlock = true;
                    }
                    return Filterlock;
                }
            });
            Videofilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (Filterlock == true) {
                        grid.setVisibility(View.GONE);
                        videocontentloading.setVisibility(View.VISIBLE);
                        videoadapter = null;
                        Service_Variables.VIDEO_ONLINE_LIST.clear();
                        grid.setAdapter(null);
                        Service_Variables.VIDEO_ONLINE_STARTLIMIT=0;
                        filterpos = position;
                        Filterlock = false;
                        new ProcessJSON().execute("http://www.funraaga.in/Action_Page_app_m.php?Action=getVideoList&Startlimit=" + Service_Variables.VIDEO_ONLINE_STARTLIMIT + "&Filtertype=" + position);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            grid.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (totalItemCount != 0 && firstVisibleItem + visibleItemCount >= totalItemCount) {
                        adapterlock = true;
                        if(Videofilter.getSelectedItem().toString().contains("Recent"))
                        {
                            Process_URL = "http://www.funraaga.in/Action_Page_app_m.php?Action=getVideoList&Startlimit=" + Service_Variables.VIDEO_ONLINE_STARTLIMIT;
                        }
                        else if(Videofilter.getSelectedItem().toString().contains("Most"))
                        {
                            Process_URL = "http://www.funraaga.in/Action_Page_app_m.php?Action=getVideoList&Startlimit=" + Service_Variables.VIDEO_ONLINE_STARTLIMIT+"&Filtertype=1";
                        }
                        Service_Variables.VIDEO_ONLINE_STARTLIMIT = Service_Variables.VIDEO_ONLINE_STARTLIMIT + 20;
                        new ProcessJSON().execute(Process_URL);
                    }
                }

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }
            });
            Service_Variables.VIDEO_ACTIVITY_LOCK=true;
        }
    }

    public class Video_Grid extends BaseAdapter {
        private Context mContext;
        private ArrayList<Video_Online_Item> videoOnlineItems;
        ImageLoader imageLoader;
        private Video_Online_Item vidonlineobj;
        public Video_Grid(Context c,ArrayList<Video_Online_Item> temparrayobj ) {
            this.mContext = c;
            this.videoOnlineItems=temparrayobj;
            imageLoader = ImageLoader.getInstance();
            config = new ImageLoaderConfiguration.Builder(this.mContext)
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
            imageLoader.init(config);
        }

        @Override
        public int getCount() {
            return videoOnlineItems.size();
        }

        @Override
        public Object getItem(int position) {
            //return vidtitle.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, final View converView, ViewGroup parent) {
            View grid;
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.video_grid, null);
            final ImageView videoimg = (ImageView) grid.findViewById(R.id.Vid_Img);
            final TextView videotitle = (TextView) grid.findViewById(R.id.Video_Title);
            final TextView videoviews = (TextView) grid.findViewById(R.id.Video_Views);
            final TextView videotype = (TextView) grid.findViewById(R.id.Video_Type);
            btnshare = (ImageButton) grid.findViewById(R.id.share);
            btndownload = (ImageButton) grid.findViewById(R.id.download);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    vidonlineobj=new Video_Online_Item();
                    vidonlineobj=videoOnlineItems.get(position);
                    ImageAware imgawr = new ImageViewAware(videoimg, false);
                    imageLoader.displayImage(vidonlineobj.getPoster().toString(), imgawr, imgDisplayOptions);
                    videotitle.setText(vidonlineobj.getTitle().toString());
                    videoviews.setText(vidonlineobj.getViews().toString() + " views");
                    videotype.setText(vidonlineobj.getVideotype().toString());
                }
            });
            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vidonlineobj=new Video_Online_Item();
                    vidonlineobj=videoOnlineItems.get(position);
                    Data_Transmission.videoPlayCount(vidonlineobj.getRawfile());
                    Bundle bundle = new Bundle();
                    if (vidonlineobj.getTitle().toString().length() >= 26) {
                        bundle.putString("Title", vidonlineobj.getTitle().toString().substring(0, 25));
                    } else {
                        bundle.putString("Title", vidonlineobj.getTitle().toString());
                    }
                    bundle.putString("URL", vidonlineobj.getRawfile().toString());
                    Intent i = new Intent(Video_Activity.this, VideoPlayer_Activity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
            btnshare.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    vidonlineobj=new Video_Online_Item();
                    vidonlineobj=videoOnlineItems.get(position);
                    shareTextUrl("http://www.funraaga.in/Videosview.php?videoplay=" + vidonlineobj.getRawfile().toString(), vidonlineobj.getTitle().toString());
                }
            });
            btndownload.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    vidonlineobj=new Video_Online_Item();
                    vidonlineobj=videoOnlineItems.get(position);
                    Bundle downloadbundle = new Bundle();
                    downloadbundle.putString("FileName", vidonlineobj.getTitle().toString());
                    downloadbundle.putString("RawFile", vidonlineobj.getRawfile().toString());
                    downloadbundle.putString("Type", "0");
                    downloadbundle.putString("Category", vidonlineobj.getVideotype().toString());
                    downloadbundle.putString("ImageUrl", vidonlineobj.getPoster().toString());
                    Intent i = new Intent(Video_Activity.this, Download_Activity.class);
                    i.putExtras(downloadbundle);
                    startActivity(i);
                }
            });
            return grid;
        }
    }

    public class Video_Grid_Offline extends BaseAdapter {
        private Context mContext_off;
        private ArrayList<Video_Offline_Item> videoOfflineItemsobj;
        ImageLoader imageLoader_off;
        private Video_Offline_Item videoofflineobj;

        public Video_Grid_Offline(Context c, ArrayList<Video_Offline_Item> tempobjarray) {
            this.mContext_off = c;
            this.videoOfflineItemsobj=tempobjarray;
            imageLoader_off = ImageLoader.getInstance();
            config = new ImageLoaderConfiguration.Builder(this.mContext_off)
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
            imageLoader_off.init(config);
        }

        @Override
        public int getCount() {
            return videoOfflineItemsobj.size();
        }

        @Override
        public Object getItem(int position) {
                return videoOfflineItemsobj.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, final View converView, ViewGroup parent) {

            View grid;
            grid = new View(mContext_off);
            LayoutInflater inflater = (LayoutInflater) mContext_off.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.video_offline_grid, null);
            final ImageView videooffimg = (ImageView) grid.findViewById(R.id.Vid_Off_Img);
            final TextView videoofftitle = (TextView) grid.findViewById(R.id.Video_Off_Title);
            final TextView videooffduration = (TextView) grid.findViewById(R.id.Video_Off_Duration);
            ImageButton videomenu = (ImageButton) grid.findViewById(R.id.btn_video_options);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoofflineobj=new Video_Offline_Item();
                            videoofflineobj=videoOfflineItemsobj.get(position);
                            videoofftitle.setText(videoofflineobj.getTitle());
                            videooffduration.setText(videoofflineobj.getDuration().toString().replace(".", ":"));
                            imageLoader_off.displayImage(videoofflineobj.getPoster().toString(), videooffimg, imgDisplayOptions);
                        }
                    });
                }
            }).start();

            videomenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position);
                }
            });

            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playvideo(position);
                }
            });

            return grid;
        }

        public void playvideo(int position) {
            Bundle bundle = new Bundle();
            videoofflineobj=new Video_Offline_Item();
            videoofflineobj=videoOfflineItemsobj.get(position);
            if (videoofflineobj.getTitle().toString().length() >= 26) {
                bundle.putString("Title", videoofflineobj.getTitle().toString().substring(0, 25));
            } else {
                bundle.putString("Title", videoofflineobj.getTitle().toString());
            }
            bundle.putString("URL", videoofflineobj.getPath().toString());
            Intent i = new Intent(Video_Activity.this, VideoPlayer_Activity.class);
            i.putExtras(bundle);
            startActivity(i);
        }

        private void showPopup(View view, final int position) {
            View menuItemView = view.findViewById(R.id.btn_video_options);
            PopupMenu popup = new PopupMenu(Video_Activity.this, menuItemView);
            MenuInflater inflate = popup.getMenuInflater();
            inflate.inflate(R.menu.video_offline_popup_menu, popup.getMenu());
            videoofflineobj=new Video_Offline_Item();
            videoofflineobj=videoOfflineItemsobj.get(position);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.popupplay:
                            playvideo(position);
                            break;
                        case R.id.popuprename:
                            // do what you need .
                            break;
                        case R.id.popupdelete:

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isFinishing()) {
                                        AlertDialog.Builder infodialog = new AlertDialog.Builder(Video_Activity.this);
                                        infodialog.setTitle("Delete - " + videoofflineobj.getTitle().toString())
                                                .setMessage("Are you sure you want to delete this file?")
                                                .setCancelable(true)
                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                File fpath = new File(videoofflineobj.getPath().toString());
                                                                if (fpath.delete()) {
                                                                    Uri videouri = MediaStore.Video.Media.getContentUri(videoofflineobj.getPath().toString());
                                                                    getApplicationContext().getContentResolver().delete(videouri,
                                                                            MediaStore.MediaColumns.DATA + "=?", new String[]{videoofflineobj.getPath().toString()});
                                                                    //vidtitle_off_m.remove(position);
                                                                    //viddur_off_m.remove(position);
                                                                    //vidpath_off_m.remove(position);
                                                                    //vidposter_off_m.remove(position);
                                                                    video_off_adapter = new Video_Grid_Offline(Video_Activity.this,Service_Variables.VIDEO_OFFLINE_LIST);
                                                                    video_off_adapter.notifyDataSetChanged();
                                                                    grid.invalidateViews();
                                                                } else {
                                                                    Toast.makeText(Video_Activity.this, "File Could not be deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                )
                                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .create();
                                        infodialog.show();
                                    }
                                }
                            });
                            break;
                        case R.id.popupshare:
                            String temppath = videoofflineobj.getPath().toString();
                            shareOfflineVideo(temppath);
                            break;
                        default:
                            String vduration = videoofflineobj.getDuration().toString();
                            String vpath = videoofflineobj.getPath().toString();
                            File ftemp = new File(vpath);
                            Date vtempdate = new Date(ftemp.lastModified());
                            String vdate = vtempdate.toString();
                            MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                            mdr.setDataSource(vpath);
                            String vheight = mdr.extractMetadata(mdr.METADATA_KEY_VIDEO_HEIGHT);
                            String vwidth = mdr.extractMetadata(mdr.METADATA_KEY_VIDEO_WIDTH);
                            String vmime = mdr.extractMetadata(mdr.METADATA_KEY_MIMETYPE);
                            String vframerate = mdr.extractMetadata(mdr.METADATA_KEY_BITRATE);
                            MediaCodecInfo infobj = Miscellaneous.selectCodec(vmime);
                            String vcodec = infobj.getName().toString();
                            String vsize = "";
                            long vsiz = ftemp.length() / 1024;
                            if (vsiz >= 1024) {
                                vsize = vsiz / 1024 + " MB";
                            } else {
                                vsize = vsiz + " KB";
                            }
                            final String message = "Basic Information" + "\n"
                                    + "-------------------------" + "\n"
                                    + " " + "\n"
                                    + "File Name : " + videoofflineobj.getTitle().toString() + "\n"
                                    + "Path : " + vpath + "\n"
                                    + "Size : " + vsize + "\n"
                                    + "Date : " + vdate + "\n"
                                    + " " + "\n"
                                    + "Advanced Information" + "\n"
                                    + "-------------------------" + "\n"
                                    + " " + "\n"
                                    + "Video Codec : " + vcodec + "\n"
                                    + "MIME Type : " + vmime + "\n"
                                    + "Resolution : " + vwidth + "x" + vheight + "\n"
                                    + "Frame Rate : " + vframerate;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isFinishing()) {
                                        AlertDialog.Builder infodialog = new AlertDialog.Builder(Video_Activity.this);
                                        infodialog.setTitle(videoofflineobj.getTitle().toString())
                                                .setMessage(message)
                                                .setCancelable(false)
                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Whatever...
                                                    }
                                                }).create();
                                        infodialog.show();

                                    }
                                }
                            });
                            return false;
                    }
                    return false;
                }
            });
            popup.show();
        }

    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ONLINE";
                case 1:
                    return "OFFLINE";
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.video_fragment, container, false);

            } else {
                rootView = inflater.inflate(R.layout.video__offline_fragment, container, false);
            }

            return rootView;
        }
    }

    public void shareTextUrl(String url, String Title) {
        String urlToShare = url;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, Title);
        share.putExtra(Intent.EXTRA_TEXT, urlToShare);
        startActivity(Intent.createChooser(share, "Share link!"));
}

    private void shareOfflineVideo(String Path) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("video/*");
        String offlinevidPath = Path;
        File videoFileToShare = new File(offlinevidPath);
        Uri uri = Uri.fromFile(videoFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Video!"));
    }

    public class loaderTask extends TimerTask {
        public void run() {
            /*if(Service_Variables.VIDEO_ACTIVITY_LOCK==false)
            {
                progressdialog = new android.app.ProgressDialog(Video_Activity.this);
                progressdialog.setMessage("Loading...");
                progressdialog.show();
            }
            else
            {
                if(progressdialog!=null) {
                    progressdialog.dismiss();
                    progressdialog = null;
                }
            }*/
        }
    }

}