package com.cozmoz.funraaga;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;

import com.cozmoz.funraaga.Service.Music_Notification_Service;
import com.cozmoz.funraaga.Utilities.Audio_Online_Item;
import com.cozmoz.funraaga.Utilities.Miscellaneous;
import com.cozmoz.funraaga.Utilities.Music_Album_Item;
import com.cozmoz.funraaga.Utilities.Music_Artist_Item;
import com.cozmoz.funraaga.Utilities.Music_Genere_Item;
import com.cozmoz.funraaga.Utilities.Music_Item;
import com.cozmoz.funraaga.Utilities.Music_Playlist_Item;
import com.cozmoz.funraaga.Utilities.Player_Controls_Action;
import com.cozmoz.funraaga.Utilities.Service_Constants;
import com.cozmoz.funraaga.Utilities.Service_Variables;
import com.cozmoz.funraaga.httpHandler.Data_Model;
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
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Audio_Activity extends AppCompatActivity
        implements
        Audio_Fragment.OnFragmentInteractionListener,
        Music_Album_Fragment.OnFragmentInteractionListener,
        Music_Artist_Fragment.OnFragmentInteractionListener,
        Music_Tracks_Fragment.OnFragmentInteractionListener,
        Music_Genere_Fragment.OnFragmentInteractionListener,
        Music_Folders_Fragment.OnFragmentInteractionListener,
        Music_Playlist_Fragment.OnFragmentInteractionListener {

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
    static ImageButton btnplay;
    static ImageButton btnpause;
    static ImageButton btnnext;
    static TextView songname;
    static TextView albumname;
    static ImageView albumposter;
    ProgressBar progressBar;
    static ImageLoader imageLoader_off;
    static DisplayImageOptions imgDisplayOptions1;
    GridView grid;
    boolean Filterlock = false;
    boolean adapterlock = false;
    String Process_URL;
    Spinner Audiofilter;
    Audio_Online_Grid audioonlineadapter = null;
    Music_Album_Grid music_album_adapter = null;
    Music_Track_Grid music_track_adapter = null;
    Music_Artist_Grid music_artist_adapter = null;
    Music_Genere_Grid music_genere_adapter = null;
    Music_Playlist_Grid music_playlist_adapter = null;
    FileOutputStream out = null;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_activity);
        setNavigationdrawer(savedInstanceState);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        fragment = new Audio_Fragment();
        Process_URL = "http://www.funraaga.in/Action_Page_app_m.php?Action=getAlbumList&Startlimit=" + Service_Variables.AUDIO_ONLINE_STARTLIMIT;
        Service_Variables.AUDIO_ONLINE_STARTLIMIT = Service_Variables.AUDIO_ONLINE_STARTLIMIT + 20;
        transaction.replace(R.id.appcontent, fragment);
        transaction.commit();
        btnplay=(ImageButton)findViewById(R.id.smallmusicplayerplay);
        btnpause=(ImageButton)findViewById(R.id.smallmusicplayerpause);
        btnnext=(ImageButton)findViewById(R.id.smallmusicplayernext);
        songname=(TextView)findViewById(R.id.smallmusicplayersongname);
        albumname=(TextView)findViewById(R.id.smallmusicplayeralbum);
        albumposter=(ImageView)findViewById(R.id.smallmusicplayerposter);
        progressBar=(ProgressBar)findViewById(R.id.smallmusicplayerprogress);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.md_amber_500), PorterDuff.Mode.SRC_IN);
        Service_Variables.MUSIC_PROGRESSBAR_HANDLER = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Integer i[] = (Integer[])msg.obj;
                progressBar.setProgress(i[2]);
            }
        };
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player_Controls_Action.musicNextControl(getApplicationContext());
            }
        });
        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player_Controls_Action.musicPlayControl(getApplicationContext());
            }
        });

        btnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player_Controls_Action.musicPauseControl(getApplicationContext());
            }
        });

        imageLoader_off = ImageLoader.getInstance();
        config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheSize(104857600)
                .memoryCacheSizePercentage(50)
                .discCacheSize(104857600)
                .threadPoolSize(50)
                .build();

        imgDisplayOptions1 = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(10))
                .cacheInMemory()
                .cacheOnDisc()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imageLoader_off.init(config);
        new ProcessJSON().execute(Process_URL);
    }

    @Override
    public void onFragmentAudioInteraction(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentMusicAlbumInteraction(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentMusicArtistInteraction(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentMusicTracksInteraction(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentMusicFoldersInteraction(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentMusicComposerInteraction(Uri uri) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentMusicPlaylistInteraction(Uri uri) {
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

    public static void changebutton()
    {
         if(Service_Variables.MUSIC_SONG_PAUSED)
         {
             btnplay.setVisibility(View.VISIBLE);
             btnpause.setVisibility(View.GONE);
         }
        else
         {
             btnplay.setVisibility(View.GONE);
             btnpause.setVisibility(View.VISIBLE);
         }
    }

    public static void updateui()
    {
        songname.setText(Service_Variables.MUSIC_CURRENT_PLAYLIST.get(Service_Variables.MUSIC_SONG_NUMBER).getTitle());
        albumname.setText(Service_Variables.MUSIC_CURRENT_PLAYLIST.get(Service_Variables.MUSIC_SONG_NUMBER).getAlbum());
        ImageAware imgawr = new ImageViewAware(albumposter, false);
        imageLoader_off.displayImage("file://" + Service_Variables.OFFLINE_IMAGE_PATH + "Aud_" + Service_Variables.MUSIC_CURRENT_PLAYLIST.get(Service_Variables.MUSIC_SONG_NUMBER).getAlbumId() + ".png", imgawr, imgDisplayOptions1);
    }

    public void checkMusicService() {
        boolean isServiceRunning = Miscellaneous.isServiceRunning(Music_Notification_Service.class.getName(), getApplicationContext());
        if (!isServiceRunning) {
            Service_Variables.MUSIC_SONG_PAUSED = false;
            Intent i = new Intent(getApplicationContext(), Music_Notification_Service.class);
            startService(i);
        }
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
                            String itemclicked = ((Nameable) drawerItem).getName().getText(Audio_Activity.this).toString();
                            result.getDrawerLayout().closeDrawer(GravityCompat.START);
                            if (getTitle().equals(itemclicked) == false) {
                                if (itemclicked.equalsIgnoreCase("Home") == true) {
                                    Intent i = new Intent(Audio_Activity.this, Home_Activity.class);
                                    startActivity(i);
                                } else if (itemclicked.equalsIgnoreCase("Radios") == true) {
                                    Intent i = new Intent(Audio_Activity.this, Radio_Activity.class);
                                    startActivity(i);
                                } else if (itemclicked.equalsIgnoreCase("Videos") == true) {
                                    Intent i = new Intent(Audio_Activity.this, Video_Activity.class);
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
            //String stream = null;
            String urlString = strings[0];
            hh = new Data_Model();
            stream = hh.HttpDataHandler(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String stream) {
            Miscellaneous.loadOnlineAudios(stream);
            grid = null;
            if (audioonlineadapter == null) {
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setOffscreenPageLimit(6);
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(mViewPager);
                audioonlineadapter = new Audio_Online_Grid(Audio_Activity.this, Service_Variables.AUDIO_ONLINE_LIST);
                grid = (GridView) findViewById(R.id.audiocontents);
                grid.setAdapter(audioonlineadapter);
                music_album_adapter = new Music_Album_Grid(Audio_Activity.this, Service_Variables.MUSIC_ALBUM_LIST);
                GridView grid_off = (GridView) findViewById(R.id.musicalbumdetails);
                grid_off.setAdapter(music_album_adapter);
                music_track_adapter = new Music_Track_Grid(Audio_Activity.this, Service_Variables.MUSIC_SONGS_LIST);
                ListView grid_track = (ListView) findViewById(R.id.musictrackdetails);
                grid_track.setAdapter(music_track_adapter);
                music_artist_adapter = new Music_Artist_Grid(Audio_Activity.this, Service_Variables.MUSIC_ARTIST_LIST);
                ListView grid_artist = (ListView) findViewById(R.id.musicartistdetails);
                grid_artist.setAdapter(music_artist_adapter);
                music_genere_adapter = new Music_Genere_Grid(Audio_Activity.this, Service_Variables.MUSIC_GENERE_LIST);
                ListView grid_genere = (ListView) findViewById(R.id.musicgeneredetails);
                grid_genere.setAdapter(music_genere_adapter);
                music_playlist_adapter = new Music_Playlist_Grid(Audio_Activity.this, Service_Variables.MUSIC_PLAYLIST_LIST);
                ListView grid_playlist = (ListView) findViewById(R.id.musicplaylistdetails);
                grid_playlist.setAdapter(music_playlist_adapter);
                actionsoffab();
            } else {
                grid = (GridView) findViewById(R.id.audiocontents);
                audioonlineadapter = new Audio_Online_Grid(Audio_Activity.this, Service_Variables.AUDIO_ONLINE_LIST);
                audioonlineadapter.notifyDataSetChanged();
                grid.invalidateViews();
            }
            Audiofilter = (Spinner) findViewById(R.id.audfilter);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    Audio_Activity.this, R.layout.filter_spinner_items, Service_Variables.AUDIO_ONLINE_FILTER_ITEMS
            );
            spinnerArrayAdapter.setDropDownViewResource(R.layout.filter_spinner_items);
            Audiofilter.setAdapter(spinnerArrayAdapter);
            Audiofilter.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

            if (filterpos == 0) {
                Audiofilter.setSelection(0, false);
            } else {
                Audiofilter.setSelection(1, false);
            }

            Audiofilter.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Filterlock = true;
                    }
                    return Filterlock;
                }
            });
            Audiofilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (Filterlock == true) {
                        audioonlineadapter = null;
                        Service_Variables.AUDIO_ONLINE_LIST.clear();
                        grid.setAdapter(null);
                        Service_Variables.AUDIO_ONLINE_STARTLIMIT = 0;
                        filterpos = position;
                        Filterlock = false;
                        new ProcessJSON().execute("http://www.funraaga.in/Action_Page_app_m.php?Action=getAlbumList&Startlimit=" + Service_Variables.AUDIO_ONLINE_STARTLIMIT + "&Filtertype=" + position);
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
                        if (Audiofilter.getSelectedItem().toString().contains("Recent")) {
                            Process_URL = "http://www.funraaga.in/Action_Page_app_m.php?Action=getAlbumList&Startlimit=" + Service_Variables.AUDIO_ONLINE_STARTLIMIT;
                        } else if (Audiofilter.getSelectedItem().toString().contains("Most")) {
                            Process_URL = "http://www.funraaga.in/Action_Page_app_m.php?Action=getAlbumList&Startlimit=" + Service_Variables.AUDIO_ONLINE_STARTLIMIT + "&Filtertype=1";
                        }
                        Service_Variables.AUDIO_ONLINE_STARTLIMIT = Service_Variables.AUDIO_ONLINE_STARTLIMIT + 20;
                        new ProcessJSON().execute(Process_URL);
                    }
                }

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }
            });

        }
    }

    public void actionsoffab() {
        FloatingActionButton albumfab = (FloatingActionButton) findViewById(R.id.albumfab);
        albumfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playactionforfab();
            }
        });
        FloatingActionButton tracksfab = (FloatingActionButton) findViewById(R.id.tracksfab);
        tracksfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playactionforfab();
            }
        });
        FloatingActionButton artistfab = (FloatingActionButton) findViewById(R.id.artistfab);
        artistfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playactionforfab();
            }
        });
    }
    public void playactionforfab()
    {
        long seed = System.nanoTime();
        Random r=new Random(seed);
        Intent i = new Intent(getApplicationContext(), Music_Notification_Service.class);
        getApplicationContext().stopService(i);
        if(Service_Variables.MUSIC_SONGS_LIST.size()<=0)
        {
            Miscellaneous.listOfSongs(getApplicationContext());
        }
        Service_Variables.MUSIC_CURRENT_PLAYLIST=Service_Variables.MUSIC_SONGS_LIST;
        Collections.shuffle(Service_Variables.MUSIC_CURRENT_PLAYLIST,r);
        /*for (int i1=0;i1<Service_Variables.MUSIC_CURRENT_PLAYLIST.size();i1++)
        {
            int index=r.nextInt(i1+1);
            Music_Item obj=Service_Variables.MUSIC_CURRENT_PLAYLIST.get(index);
            Service_Variables.MUSIC_CURRENT_PLAYLIST.set(index,Service_Variables.MUSIC_CURRENT_PLAYLIST.get(i1));
            Service_Variables.MUSIC_CURRENT_PLAYLIST.set(i1,obj);
        }*/
        checkMusicService();
    }

    public class Audio_Online_Grid extends BaseAdapter {
        private Context mContext;
        private ArrayList<Audio_Online_Item> audioonlineitems;
        ImageLoader imageLoader;
        Audio_Online_Item audonlineobj;

        public Audio_Online_Grid(Context c, ArrayList<Audio_Online_Item> tempobj) {
            this.mContext = c;
            this.audioonlineitems = tempobj;
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
            return audioonlineitems.size();
        }

        @Override
        public Object getItem(int position) {
            //return audtitle.get(position);
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
            grid = inflater.inflate(R.layout.audio_grid, null);
            final ImageView audioimg = (ImageView) grid.findViewById(R.id.Aud_Img);
            final TextView audiotitle = (TextView) grid.findViewById(R.id.Audio_Title);
            final TextView audioviews = (TextView) grid.findViewById(R.id.Audio_Views);
            final TextView audiotype = (TextView) grid.findViewById(R.id.Audio_Type);
            btnshare = (ImageButton) grid.findViewById(R.id.share);
            btndownload = (ImageButton) grid.findViewById(R.id.download);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    audonlineobj = new Audio_Online_Item();
                    audonlineobj = audioonlineitems.get(position);
                    ImageAware imgawr = new ImageViewAware(audioimg, false);
                    imageLoader.displayImage(audonlineobj.getAlbumposter().toString().replace(".jpg", ".png"), imgawr, imgDisplayOptions);
                    audiotitle.setText(audonlineobj.getAlbumname().toString());
                    audioviews.setText(audonlineobj.getAlbumviews().toString() + " views");
                    audiotype.setText(audonlineobj.getAlbumtype().toString());
                }
            });
            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    audonlineobj = new Audio_Online_Item();
                    audonlineobj = audioonlineitems.get(position);
                    Bundle bundle = new Bundle();
                    if (audonlineobj.getAlbumname().toString().length() >= 26) {
                        bundle.putString("Title", audonlineobj.getAlbumname().toString().substring(0, 25));
                    } else {
                        bundle.putString("Title", audonlineobj.getAlbumname().toString());
                    }
                    bundle.putString("RawFile", audonlineobj.getAlbumid().toString());
                    bundle.putString("Poster", audonlineobj.getAlbumposter().toString().replace(".jpg", ".png"));
                    bundle.putString("Type", "0");
                    Intent i = new Intent(Audio_Activity.this, ListTracks_Activity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
            btnshare.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    shareTextUrl("http://www.funraaga.in/Downloads_View.php?AlbumId=" + audonlineobj.getAlbumid().toString(), audonlineobj.getAlbumname().toString());
                }
            });
            btndownload.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle downloadbundle = new Bundle();
                    downloadbundle.putString("FileName", audonlineobj.getAlbumname().toString());
                    downloadbundle.putString("RawFile", audonlineobj.getAlbumid().toString());
                    downloadbundle.putString("Type", "1");
                    downloadbundle.putString("Category", audonlineobj.getAlbumtype().toString());
                    downloadbundle.putString("ImageUrl", audonlineobj.getAlbumposter().toString().replace(".jpg", ".png"));
                    Intent i = new Intent(Audio_Activity.this, Download_Activity.class);
                    i.putExtras(downloadbundle);
                    startActivity(i);
                }
            });
            return grid;
        }
    }

    public class Music_Album_Grid extends BaseAdapter {
        private Context mContext_off;
        private ArrayList<Music_Album_Item> musicalbumitems;
        Music_Album_Item musicalbumobj;
        ImageLoader imageLoader_off;

        public Music_Album_Grid(Context c, ArrayList<Music_Album_Item> tempobj) {
            this.mContext_off = c;
            this.musicalbumitems = tempobj;

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
            return musicalbumitems.size();
        }

        @Override
        public Object getItem(int position) {
            // return aud_off_title.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, final View converView, ViewGroup parent) {
            View grid;
            grid = new View(mContext_off);
            LayoutInflater inflater = (LayoutInflater) mContext_off.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.music_album_grid, null);
            final ImageView audiooffimg = (ImageView) grid.findViewById(R.id.Aud_Off_Img);
            final TextView audioofftitle = (TextView) grid.findViewById(R.id.Aud_Off_Title);
            final TextView audiooffcomposer = (TextView) grid.findViewById(R.id.Aud_composer);
            ImageButton audiomenu = (ImageButton) grid.findViewById(R.id.btn_audio_options);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicalbumobj = new Music_Album_Item();
                            musicalbumobj = musicalbumitems.get(position);
                            audioofftitle.setText(musicalbumobj.getAlbumname().toString());
                            audiooffcomposer.setText(musicalbumobj.getAlbumtrackscount() + " Songs");
                            ImageAware imgawr = new ImageViewAware(audiooffimg, false);
                            imageLoader_off.displayImage("file://" + Service_Variables.OFFLINE_IMAGE_PATH + "Aud_" + musicalbumobj.getAlbumid() + ".png", imgawr, imgDisplayOptions);
                        }
                    });
                }
            }).start();
            audiomenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position);
                }
            });

            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicalbumobj = new Music_Album_Item();
                    musicalbumobj = musicalbumitems.get(position);
                    Service_Variables.MUSIC_ALBUM_TRACKS.clear();
                    Miscellaneous.listAlbumTracks(getApplicationContext(), Long.parseLong(musicalbumobj.getAlbumid()));
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", musicalbumobj.getAlbumname());
                    bundle.putString("Poster", "file://" + Service_Variables.OFFLINE_IMAGE_PATH + "Aud_" + musicalbumobj.getAlbumid() + ".png");
                    bundle.putString("Type", "1");
                    Intent i = new Intent(Audio_Activity.this, ListTracks_Activity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });

            return grid;
        }

        private void showPopup(View view, final int position) {
            View menuItemView = view.findViewById(R.id.btn_audio_options);
            PopupMenu popup = new PopupMenu(Audio_Activity.this, menuItemView);
            MenuInflater inflate = popup.getMenuInflater();
            inflate.inflate(R.menu.audio_offline_popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.popupplay:
                            break;
                        case R.id.popuprename:
                            break;
                        case R.id.popupdelete:
                            break;
                        case R.id.popupshare:
                            break;
                    }
                    return false;
                }
            });
            popup.show();
        }

    }

    public class Music_Track_Grid extends BaseAdapter {
        private Context mContext_off;
        ArrayList<Music_Item> musictrackitems;
        Music_Item musictrackobj;

        public Music_Track_Grid(Context c, ArrayList<Music_Item> tempobj) {
            this.mContext_off = c;
            musictrackitems = tempobj;
        }

        @Override
        public int getCount() {
            return musictrackitems.size();
        }

        @Override
        public Object getItem(int position) {
            //return track_title.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, final View converView, ViewGroup parent) {
            View grid;
            grid = new View(mContext_off);
            LayoutInflater inflater = (LayoutInflater) mContext_off.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.music_tracks_list, null);
            final TextView tracktitle = (TextView) grid.findViewById(R.id.Track_Title);
            final TextView trackartistname = (TextView) grid.findViewById(R.id.Track_views);
            ImageButton artistmenu = (ImageButton) grid.findViewById(R.id.btn_track_options);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musictrackobj = new Music_Item();
                            musictrackobj = musictrackitems.get(position);
                            tracktitle.setText(musictrackobj.getTitle().toString());
                            trackartistname.setText(musictrackobj.getArtist().toString());
                        }
                    });
                }
            }).start();
            artistmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position);
                }
            });

            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return grid;
        }

        private void showPopup(View view, final int position) {
            View menuItemView = view.findViewById(R.id.btn_audio_options);
            PopupMenu popup = new PopupMenu(Audio_Activity.this, menuItemView);
            MenuInflater inflate = popup.getMenuInflater();
            inflate.inflate(R.menu.audio_offline_popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.popupplay:
                            break;
                        case R.id.popuprename:
                            break;
                        case R.id.popupdelete:
                            break;
                        case R.id.popupshare:
                            break;
                    }
                    return false;
                }
            });
            popup.show();
        }

    }

    public class Music_Artist_Grid extends BaseAdapter {
        private Context mContext_off;
        ArrayList<Music_Artist_Item> musicartistitems;
        Music_Artist_Item musictrackobj;

        public Music_Artist_Grid(Context c, ArrayList<Music_Artist_Item> tempobj) {
            this.mContext_off = c;
            musicartistitems = tempobj;
        }

        @Override
        public int getCount() {
            return musicartistitems.size();
        }

        @Override
        public Object getItem(int position) {
            //return track_title.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, final View converView, ViewGroup parent) {
            View grid;
            grid = new View(mContext_off);
            LayoutInflater inflater = (LayoutInflater) mContext_off.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.music_artist_list, null);
            final TextView artisttitle = (TextView) grid.findViewById(R.id.artist_Title);
            final TextView artisttracks = (TextView) grid.findViewById(R.id.artist_songcount);
            ImageButton artistmenu = (ImageButton) grid.findViewById(R.id.btn_artist_options);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musictrackobj = new Music_Artist_Item();
                            musictrackobj = musicartistitems.get(position);
                            artisttitle.setText(musictrackobj.getArtistname().toString());
                            artisttracks.setText(musictrackobj.getArtisttrackscount() + " Songs");
                        }
                    });
                }
            }).start();
            artistmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position);
                }
            });

            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musictrackobj = new Music_Artist_Item();
                    musictrackobj = musicartistitems.get(position);
                    Service_Variables.MUSIC_ARTIST_TRACKS.clear();
                    Miscellaneous.listArtistsTracks(getApplicationContext(), musictrackobj.getArtistid());
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", musictrackobj.getArtistname());
                    bundle.putString("Poster", "file://" + Service_Variables.OFFLINE_IMAGE_PATH);
                    bundle.putString("Type", "3");
                    Intent i = new Intent(Audio_Activity.this, ListTracks_Activity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });

            return grid;
        }

        private void showPopup(View view, final int position) {
            View menuItemView = view.findViewById(R.id.btn_audio_options);
            PopupMenu popup = new PopupMenu(Audio_Activity.this, menuItemView);
            MenuInflater inflate = popup.getMenuInflater();
            inflate.inflate(R.menu.audio_offline_popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.popupplay:
                            break;
                        case R.id.popuprename:
                            break;
                        case R.id.popupdelete:
                            break;
                        case R.id.popupshare:
                            break;
                    }
                    return false;
                }
            });
            popup.show();
        }

    }

    public class Music_Genere_Grid extends BaseAdapter {
        private Context mContext_off;
        ArrayList<Music_Genere_Item> musicgenereitems;
        Music_Genere_Item musicgenereobj;

        public Music_Genere_Grid(Context c, ArrayList<Music_Genere_Item> tempobj) {
            this.mContext_off = c;
            this.musicgenereitems = tempobj;
        }

        @Override
        public int getCount() {
            return musicgenereitems.size();
        }

        @Override
        public Object getItem(int position) {
            //return track_title.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, final View converView, ViewGroup parent) {
            View grid;
            grid = new View(mContext_off);
            LayoutInflater inflater = (LayoutInflater) mContext_off.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.music_genere_list, null);
            final TextView generetitle = (TextView) grid.findViewById(R.id.Genere_Title);
            final TextView generetracks = (TextView) grid.findViewById(R.id.Genere_trackcount);
            ImageButton artistmenu = (ImageButton) grid.findViewById(R.id.btn_genere_options);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicgenereobj = new Music_Genere_Item();
                            musicgenereobj = musicgenereitems.get(position);
                            generetitle.setText(musicgenereobj.getGenerename().toString());
                            generetracks.setText(musicgenereobj.getGeneretrackscount() + " Songs");
                        }
                    });
                }
            }).start();
            artistmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position);
                }
            });

            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return grid;
        }

        private void showPopup(View view, final int position) {
            View menuItemView = view.findViewById(R.id.btn_genere_options);
            PopupMenu popup = new PopupMenu(Audio_Activity.this, menuItemView);
            MenuInflater inflate = popup.getMenuInflater();
            inflate.inflate(R.menu.audio_offline_popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.popupplay:
                            break;
                        case R.id.popuprename:
                            break;
                        case R.id.popupdelete:
                            break;
                        case R.id.popupshare:
                            break;
                    }
                    return false;
                }
            });
            popup.show();
        }

    }

    public class Music_Playlist_Grid extends BaseAdapter {
        private Context mContext_off;
        ArrayList<Music_Playlist_Item> musicplaylistitems;
        Music_Playlist_Item musicplaylistobj;

        public Music_Playlist_Grid(Context c, ArrayList<Music_Playlist_Item> tempobj) {
            this.mContext_off = c;
            this.musicplaylistitems = tempobj;
        }

        @Override
        public int getCount() {
            return musicplaylistitems.size();
        }

        @Override
        public Object getItem(int position) {
            //return track_title.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, final View converView, ViewGroup parent) {
            View grid;
            grid = new View(mContext_off);
            LayoutInflater inflater = (LayoutInflater) mContext_off.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.music_playlist_list, null);
            final TextView playlisttitle = (TextView) grid.findViewById(R.id.Playlist_Title);
            final TextView playlisttracks = (TextView) grid.findViewById(R.id.Playlist_trackcount);
            ImageButton artistmenu = (ImageButton) grid.findViewById(R.id.btn_playlist_options);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicplaylistobj = new Music_Playlist_Item();
                            musicplaylistobj = musicplaylistitems.get(position);
                            playlisttitle.setText(musicplaylistobj.getPlaylistname().toString());
                            playlisttracks.setText(musicplaylistobj.getPlaylisttrackcount() + " Songs");
                        }
                    });
                }
            }).start();
            artistmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, position);
                }
            });

            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return grid;
        }

        private void showPopup(View view, final int position) {
            View menuItemView = view.findViewById(R.id.btn_genere_options);
            PopupMenu popup = new PopupMenu(Audio_Activity.this, menuItemView);
            MenuInflater inflate = popup.getMenuInflater();
            inflate.inflate(R.menu.audio_offline_popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.popupplay:
                            break;
                        case R.id.popuprename:
                            break;
                        case R.id.popupdelete:
                            break;
                        case R.id.popupshare:
                            break;
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
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ONLINE";
                case 1:
                    return "ALBUMS";
                case 2:
                    return "TRACKS";
                case 3:
                    return "ARTISTS";
                case 4:
                    return "GENRES";
                case 5:
                    return "FOLDERS";
                case 6:
                    return "PLAYLISTS";
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
                rootView = inflater.inflate(R.layout.audio_fragment, container, false);

            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.music_album_fragment, container, false);

            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                rootView = inflater.inflate(R.layout.music_tracks_fragment, container, false);

            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 4) {
                rootView = inflater.inflate(R.layout.music_artist_fragment, container, false);

            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 5) {
                rootView = inflater.inflate(R.layout.music_genere_fragment, container, false);

            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 6) {
                rootView = inflater.inflate(R.layout.music_folders_fragment, container, false);

            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 7) {
                rootView = inflater.inflate(R.layout.music_playlist_fragment, container, false);

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

    private void shareOfflineAudio(String Path) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("Audio/*");
        String offlinevidPath = Path;
        File videoFileToShare = new File(offlinevidPath);
        Uri uri = Uri.fromFile(videoFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Video!"));
    }

    public String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

}
