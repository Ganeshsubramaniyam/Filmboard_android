package com.cozmoz.funraaga;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

public class Home_Activity extends AppCompatActivity {

    private AccountHeader headerResult = null;
    private Drawer result = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        // Handle Toolbar
        setNavigationdrawer(savedInstanceState);
        /*MobileAds.initialize(getApplicationContext(), "ca-app-pub-8205498786717408~1317926905");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .setGender(AdRequest.GENDER_FEMALE)
                .setBirthday(new GregorianCalendar(1990, 9, 1).getTime())
                .tagForChildDirectedTreatment(true)
                .build();
        mAdView.loadAd(request);*/
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        //startActivityForResult(intent, 42);

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public final void onActivityResult(final int requestCode, final int resultCode, final Intent resultData) {
        if (requestCode == 42) {
            Uri treeUri = null;
            if (resultCode == Activity.RESULT_OK) {
                // Get Uri from Storage Access Framework.
                treeUri = resultData.getData();
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                // List all existing files inside picked directory
                for (DocumentFile file : pickedDir.listFiles()) {
                    Log.d("root access", "Found file " + file.getName() + " with size " + file.length()+" Path"+file.getUri());
                }
            }
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

    public void setNavigationdrawer(Bundle savedInstanceState)
    {
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
                            String itemclicked=((Nameable)drawerItem).getName().getText(Home_Activity.this).toString();
                            result.getDrawerLayout().closeDrawer(GravityCompat.START);
                            if(getTitle().equals(itemclicked)==false)
                            {
                                if(itemclicked.equalsIgnoreCase("Home")==true)
                                {
                                    Intent i=new Intent(Home_Activity.this,Home_Activity.class);
                                    startActivity(i);
                                }
                                else if(itemclicked.equalsIgnoreCase("Radios")==true)
                                {
                                    Intent i = new Intent(Home_Activity.this, Radio_Activity.class);
                                    startActivity(i);
                                }
                                else if(itemclicked.equalsIgnoreCase("Videos")==true)
                                {
                                    Intent i=new Intent(Home_Activity.this,Video_Activity.class);
                                    startActivity(i);
                                }
                                else if(itemclicked.equalsIgnoreCase("Music")==true)
                                {
                                    Intent i=new Intent(Home_Activity.this,Audio_Activity.class);
                                    startActivity(i);
                                }

                            }

                        }
                        //we do not consume the event and want the Drawer to continue with the event chain
                        return false;
                    }
                })
                .withSelectedItem(0)
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
}
