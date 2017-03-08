package com.cozmoz.funraaga;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cozmoz.funraaga.Service.Radio_Notification_Service;
import com.cozmoz.funraaga.Utilities.Miscellaneous;
import com.cozmoz.funraaga.Utilities.Player_Controls_Action;
import com.cozmoz.funraaga.Utilities.Service_Variables;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Radio_Activity extends AppCompatActivity {

    private AccountHeader headerResult = null;
    private Drawer result = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    GridView grid;
    Boolean Filterlock = false;
    int lock = 0;
    MediaPlayer mediaPlayer;
    ArrayList radioid=new ArrayList();
    ArrayList languages = new ArrayList();
    ArrayList rating = new ArrayList();
    ArrayList radionames = new ArrayList();
    ArrayList urlpaths = new ArrayList();
    String Process_URL;
    Timer myTimer;
    private ViewPager mViewPager;
    int filterpos = 0;
    Spinner Radiofilter;
    static TextView RadioName, RadioLang, BigRadioName, BigRadioLang;
    static ImageView RadioPlay, RadioPause;
    static ImageButton BigRadioPlay, BigRadioPause, BigRadioFav;
    static ProgressBar SmallRadioLoading, BigRadioLoading,RadioContentLoading;
    Radio_Grid radioadapter = null;
    FileOutputStream out = null;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();
    SlidingUpPanelLayout mLayout;
    int radioplayerlock = 0;
    int radiofavlock = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio_activity);
        setNavigationdrawer(savedInstanceState);
        myTimer = new Timer();
        myTimer.schedule(new loaderTask(), 5000);
        Process_URL = "http://www.funraaga.in/Action_Page_app_m.php?Action=getRadioList";
        Radiofilter = (Spinner) findViewById(R.id.radiofilter);
        String[] filteritems = new String[]{
                "Language", "Arabic", "Bangla", "English", "Ghazals", "Gujrat", "Hindi", "Kannada", "Lounge", "Malayalam", "Marathi",
                "Meditation", "Punjabi", "Rap", "Rock", "Tamizh", "Telugu"
        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                Radio_Activity.this, R.layout.filter_spinner_items, filteritems
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.filter_spinner_items);
        Radiofilter.setAdapter(spinnerArrayAdapter);
        Radiofilter.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        //MobileAds.initialize(getApplicationContext(), "ca-app-pub-8205498786717408~1317926905");
        //AdView mAdView = (AdView) findViewById(R.id.adView);
        /*AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .setGender(AdRequest.GENDER_FEMALE)
                .setBirthday(new GregorianCalendar(1990, 9, 1).getTime())
                .tagForChildDirectedTreatment(true)
                .build();*/
        // mAdView.loadAd(request);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
        Radiofilter.setSelection(filterpos, false);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.smallradioplayer);
                if (radioplayerlock <= 1) {
                    rl.setVisibility(View.GONE);
                } else if (radioplayerlock > 1) {
                    rl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.smallradioplayer);
                if (previousState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    rl.setVisibility(View.VISIBLE);
                    radioplayerlock = 1;
                } else if (previousState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    rl.setVisibility(View.GONE);
                    radioplayerlock = 2;
                }

                Log.i("sliderup", "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        Radiofilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Filterlock == true) {
                    RadioContentLoading.setVisibility(View.VISIBLE);

                    Process_URL = "http://www.filmboard.in/Action_Page_app_m.php?Action=getRadioList&Lang=" + Radiofilter.getSelectedItem().toString();
                    Filterlock = false;
                    filterpos = position;
                    radioadapter = null;
                    grid.setAdapter(null);
                    radionames.clear();
                    languages.clear();
                    rating.clear();
                    urlpaths.clear();
                    new ProcessJSON().execute(Process_URL);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        RadioContentLoading=(ProgressBar)findViewById(R.id.radiocontentloading);
        new ProcessJSON().execute(Process_URL);
    }

    public void intiateVibration() {
        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(250);
    }

    public static void tempchangeButton(int type) {
        if (type == 0) {
            RadioPlay.setVisibility(View.GONE);
            BigRadioPlay.setVisibility(View.GONE);
            SmallRadioLoading.setVisibility(View.VISIBLE);
            BigRadioLoading.setVisibility(View.VISIBLE);
        } else {
            BigRadioPause.setVisibility(View.GONE);
            RadioPause.setVisibility(View.GONE);
        }
    }

    public static void changeButton() {
        if (Service_Variables.RADIO_PLAY_PAUSE_LOCK) {
            SmallRadioLoading.setVisibility(View.GONE);
            BigRadioLoading.setVisibility(View.GONE);
            RadioPause.setVisibility(View.GONE);
            RadioPlay.setVisibility(View.VISIBLE);
            BigRadioPause.setVisibility(View.GONE);
            BigRadioPlay.setVisibility(View.VISIBLE);
        } else {
            SmallRadioLoading.setVisibility(View.GONE);
            BigRadioLoading.setVisibility(View.GONE);
            RadioPause.setVisibility(View.VISIBLE);
            RadioPlay.setVisibility(View.GONE);
            BigRadioPause.setVisibility(View.VISIBLE);
            BigRadioPlay.setVisibility(View.GONE);
        }
    }

    public static void updateUI() {
        try {
            RadioName.setText(Service_Variables.RADIO_NAME);
            RadioLang.setText(Service_Variables.RADIO_LANGUAGE);
            BigRadioName.setText(Service_Variables.RADIO_NAME);
        } catch (Exception e) {
        }
    }

    public void checkRadioService() {
        boolean isServiceRunning = Miscellaneous.isServiceRunning(Radio_Notification_Service.class.getName(), getApplicationContext());
        if (!isServiceRunning) {
            Service_Variables.RADIO_PLAY_PAUSE_LOCK = false;
            Intent i = new Intent(getApplicationContext(), Radio_Notification_Service.class);
            startService(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            final boolean isServiceRunning = Miscellaneous.isServiceRunning(Radio_Notification_Service.class.getName(), getApplicationContext());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isServiceRunning) {
                        updateUI();
                    }
                    changeButton();
                }
            });
        } catch (Exception e) {
        }
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
        getSupportActionBar().setTitle("FM / Radios");
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
                .withSelectedItem(1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            String itemclicked = ((Nameable) drawerItem).getName().getText(Radio_Activity.this).toString();
                            result.getDrawerLayout().closeDrawer(GravityCompat.START);
                            if (getTitle().equals(itemclicked) == false) {
                                if (itemclicked.equalsIgnoreCase("Home") == true) {
                                    Intent i = new Intent(Radio_Activity.this, Home_Activity.class);
                                    startActivity(i);
                                } else if (itemclicked.equalsIgnoreCase("Radios") == true) {
                                    Intent i = new Intent(Radio_Activity.this, Radio_Activity.class);
                                    startActivity(i);
                                } else if (itemclicked.equalsIgnoreCase("Videos") == true) {
                                    Intent i = new Intent(getApplicationContext(), Radio_Notification_Service.class);
                                    getApplicationContext().stopService(i);
                                    Intent i1 = new Intent(Radio_Activity.this, Video_Activity.class);
                                    startActivity(i1);
                                }
                            }
                        }
                        //we do not consume the event and want the Drawer to continue with the event chain
                        return false;
                    }
                })
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
            if (Filterlock == false) {
                loadDataOnContents(stream);
                radioadapter = new Radio_Grid(Radio_Activity.this, radionames, languages, rating, urlpaths);
                grid = (GridView) findViewById(R.id.radiocontents);
                grid.setAdapter(radioadapter);
                Filterlock = true;
                RadioContentLoading.setVisibility(View.GONE);
                grid.setVisibility(View.VISIBLE);

            } else {
                loadDataOnContents(stream);
                radioadapter = new Radio_Grid(Radio_Activity.this, radionames, languages, rating, urlpaths);
                radioadapter.notifyDataSetChanged();
                grid.invalidateViews();
                Filterlock = true;
                RadioContentLoading.setVisibility(View.GONE);
                grid.setVisibility(View.VISIBLE);

            }

        }
    }

    public void loadDataOnContents(String stream) {
        try {
            if (stream != null) {
                try {
                    JSONArray radioarray = new JSONArray(stream);
                    for (int i = 0; i < radioarray.length(); i++) {
                        JSONObject radobj = (JSONObject) radioarray.get(i);
                        radioid.add(radobj.getString("Id"));
                        radionames.add(radobj.getString("Name"));
                        languages.add(radobj.getString("Lang"));
                        rating.add(radobj.getInt("Rate"));
                        urlpaths.add(radobj.getString("Url"));
                    }
                } catch (Exception e) {
                    Toast.makeText(Radio_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                RadioPlay = (ImageView) findViewById(R.id.radioplaybutton);
                RadioPause = (ImageView) findViewById(R.id.radiopausebutton);
                RadioLang = (TextView) findViewById(R.id.radiolang);
                RadioName = (TextView) findViewById(R.id.radioname);
                BigRadioName = (TextView) findViewById(R.id.bigradioplayerradioname);
                BigRadioPlay = (ImageButton) findViewById(R.id.bigradioplay);
                BigRadioPause = (ImageButton) findViewById(R.id.bigradiopause);
                SmallRadioLoading = (ProgressBar) findViewById(R.id.smallradioloading);
                BigRadioLoading = (ProgressBar) findViewById(R.id.bigradioloading);
                BigRadioFav = (ImageButton) findViewById(R.id.btn_fav);
                if (radiofavlock == 0) {
                    BigRadioFav.setBackgroundResource(R.drawable.btn_fav);
                } else {
                    BigRadioFav.setBackgroundResource(R.drawable.btn_filled_fav);
                }
                RadioPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intiateVibration();
                        checkRadioService();
                        tempchangeButton(0);
                        Player_Controls_Action.radioPlayControl(getApplicationContext());

                    }
                });
                RadioPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intiateVibration();
                        checkRadioService();
                        tempchangeButton(1);
                        Player_Controls_Action.radioPauseControl(getApplicationContext());

                    }
                });
                BigRadioPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intiateVibration();
                        checkRadioService();
                        tempchangeButton(0);
                        Player_Controls_Action.radioPlayControl(getApplicationContext());

                    }
                });
                BigRadioPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intiateVibration();
                        checkRadioService();
                        tempchangeButton(1);
                        Player_Controls_Action.radioPauseControl(getApplicationContext());

                    }
                });
                BigRadioFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intiateVibration();
                        if (radiofavlock == 0) {
                            BigRadioFav.setBackgroundResource(R.drawable.btn_filled_fav);
                            radiofavlock = 1;
                        } else {
                            BigRadioFav.setBackgroundResource(R.drawable.btn_fav);
                            radiofavlock = 0;
                        }
                    }
                });
                updateUI();
                changeButton();
            }
            else
            {
                Snackbar.make(findViewById(android.R.id.content), "Please check your Internet Connection", Snackbar.LENGTH_LONG)
                        .show();
            }
        } catch (Exception e) {

        }
    }

    public class Radio_Grid extends BaseAdapter {
        private Context mContext;
        private final ArrayList Radios;
        private final ArrayList rate;
        private final ArrayList lang;
        private final ArrayList urlPaths;
        private int index;

        public Radio_Grid(Context c, ArrayList Rads, ArrayList lang, ArrayList rate, ArrayList urls) {
            mContext = c;
            this.lang = lang;
            this.Radios = Rads;
            this.rate = rate;
            this.urlPaths = urls;
        }

        @Override
        public int getCount() {
            return Radios.size();
        }

        @Override
        public Object getItem(int position) {
            return Radios.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View grid;
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.radio_grid, null);
            TextView RadioTitle = (TextView) grid.findViewById(R.id.radiotitle);
            TextView RadioLanguage = (TextView) grid.findViewById(R.id.radio_language);
            RatingBar RadioRating = (RatingBar) grid.findViewById(R.id.radiorating);
            Button btn = (Button) grid.findViewById(R.id.Radioplay);
            RadioTitle.setText(Radios.get(position).toString());
            RadioLanguage.setText(lang.get(position).toString());
            RadioRating.setRating((int) rate.get(position));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Data_Transmission.radioPlayCount(Integer.parseInt(radioid.get(position).toString()));
                            Service_Variables.RADIO_PLAY_URL = urlPaths.get(position).toString();
                            Service_Variables.RADIO_NAME = radionames.get(position).toString();
                            Service_Variables.RADIO_LANGUAGE = languages.get(position).toString();
                            intiateVibration();
                            checkRadioService();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempchangeButton(0);
                                }
                            });
                            Player_Controls_Action.radioNextControl(getApplication());
                        }
                    }).start();
                }
            });

            return grid;
        }


    }

    public class loaderTask extends TimerTask {
        public void run() {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying() == true) {
                    RadioPlay.setVisibility(View.GONE);
                    RadioPause.setVisibility(View.VISIBLE);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            RadioLang.setText("Radio is Offline");
                            RadioPlay.setVisibility(View.VISIBLE);
                            RadioPause.setVisibility(View.GONE);
                        }
                    });
                }

            }

        }
    }

}