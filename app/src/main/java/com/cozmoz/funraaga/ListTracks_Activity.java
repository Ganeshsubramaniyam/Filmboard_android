package com.cozmoz.funraaga;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cozmoz.funraaga.Utilities.Music_Album_Tracks_Item;
import com.cozmoz.funraaga.Utilities.Music_Artist_Tracks_Item;
import com.cozmoz.funraaga.Utilities.Service_Variables;
import com.cozmoz.funraaga.httpHandler.Data_Model;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListTracks_Activity extends AppCompatActivity {

    ImageLoader imageLoader;
    DisplayImageOptions imgDisplayOptions;
    ImageLoaderConfiguration config;
    Boolean toolbarlock=false;
    ArrayList tracktitle=new ArrayList();
    ArrayList trackid=new ArrayList();
    ArrayList trackinfo=new ArrayList();
    ListView grid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listtracks_activity);
        imageLoader= ImageLoader.getInstance();
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
        imageLoader.init(config);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.btn_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
        Bundle inputval=getIntent().getExtras();
        final String albumtitle=inputval.getString("Title");
        toolbar.setTitle(albumtitle);
        setSupportActionBar(toolbar);
        String posterurl=inputval.getString("Poster");
        final ImageView toolimg=(ImageView) findViewById(R.id.trackalbumposter);
        ImageAware imgawr = new ImageViewAware(toolimg, false);
        imageLoader.displayImage(posterurl.replace(".jpg",".png"), imgawr, imgDisplayOptions);

        if(inputval.getString("Type").equals("0"))
        {
            String albumid=inputval.getString("RawFile");
            new ProcessJSON().execute("http://www.funraaga.in/Action_Page_app_m.php?Action=getAlbumTracks&AlbumId="+albumid);
        }
        else if(inputval.getString("Type").equals("1"))
        {
            ArrayList<Music_Album_Tracks_Item> music_album_tracks_items=Service_Variables.MUSIC_ALBUM_TRACKS;
            Music_Album_Tracks_Item musicAlbumTracksItemobj=new Music_Album_Tracks_Item();
            for (int i=0;i<music_album_tracks_items.size();i++)
            {
                musicAlbumTracksItemobj=music_album_tracks_items.get(i);
                tracktitle.add(musicAlbumTracksItemobj.getTrackname());
                trackid.add(musicAlbumTracksItemobj.getTrackid());
                trackinfo.add(musicAlbumTracksItemobj.getArtistname());
            }
            grid = null;
            Track_Grid trackadapter = new Track_Grid(ListTracks_Activity.this, tracktitle,trackid,trackinfo);
            grid = (ListView) findViewById(R.id.trackdetails);
            grid.setAdapter(trackadapter);
        }
        else if(inputval.getString("Type").equals("3"))
        {
            toolimg.setBackgroundResource(R.drawable.nav_bg);
            ArrayList<Music_Artist_Tracks_Item> music_album_tracks_items=Service_Variables.MUSIC_ARTIST_TRACKS;
            Music_Artist_Tracks_Item musicArtistTracksItemobj=new Music_Artist_Tracks_Item();
            for (int i=0;i<music_album_tracks_items.size();i++)
            {
                musicArtistTracksItemobj=music_album_tracks_items.get(i);
                tracktitle.add(musicArtistTracksItemobj.getTrackname());
                trackid.add(musicArtistTracksItemobj.getTrackid());
                trackinfo.add(musicArtistTracksItemobj.getArtistname());
            }
            grid = null;
            Track_Grid trackadapter = new Track_Grid(ListTracks_Activity.this, tracktitle,trackid,trackinfo);
            grid = (ListView) findViewById(R.id.trackdetails);
            grid.setAdapter(trackadapter);
        }

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
            loadDataOnContents(stream);
            grid = null;
            Track_Grid trackadapter = new Track_Grid(ListTracks_Activity.this, tracktitle,trackid,trackinfo);
            grid = (ListView) findViewById(R.id.trackdetails);
            grid.setAdapter(trackadapter);
        }
    }

    public void loadDataOnContents(String stream) {
        try {
            if (stream != null) {
                try {
                    JSONArray audioarray = new JSONArray(stream);
                    for (int i = 0; i < audioarray.length(); i++) {
                        JSONObject audobj = (JSONObject) audioarray.get(i);
                        if (trackid.contains(audobj.getString("TrackId")) == false) {
                            tracktitle.add(audobj.getString("TrackName"));
                            trackid.add(audobj.getString("TrackId"));
                            trackinfo.add(audobj.getString("Views"));
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(ListTracks_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        } catch (Exception e) {

        }
    }

    public class Track_Grid extends BaseAdapter {
        private Context mContext;
        private ArrayList trackname;
        private ArrayList trackid;
        private ArrayList trackinfo;

        public Track_Grid(Context c, ArrayList tracknames, ArrayList trackids, ArrayList trackinfos) {
            this.mContext = c;
            this.trackname = tracknames;
            this.trackid = trackids;
            this.trackinfo = trackinfos;
        }

        @Override
        public int getCount() {
            return trackname.size();
        }

        @Override
        public Object getItem(int position) {
            return trackname.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, final View converView, ViewGroup parent) {
            View grid;
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.tracks_grid, null);
            final TextView tracktexttitle = (TextView) grid.findViewById(R.id.Track_Title);
            final TextView tracktextinfo = (TextView) grid.findViewById(R.id.Track_views);
            ImageView trackoptions=(ImageView)grid.findViewById(R.id.btn_track_options);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tracktexttitle.setText(trackname.get(position).toString().replace(".mp3",""));
                    tracktextinfo.setText(trackinfo.get(position).toString() + " hits");
                }
            });
            /*grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (audtitle.get(position).toString().length() >= 26) {
                        bundle.putString("Title", audtitle.get(position).toString().substring(0, 25));
                    } else {
                        bundle.putString("Title", audtitle.get(position).toString());
                    }
                    bundle.putString("RawFile", audalbumid.get(position).toString());
                    bundle.putString("Poster",audposter.get(position).toString().replace(".jpg",".png"));
                }
            });*/
            trackoptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v,position);
                }
            });
            return grid;
        }
        private void showPopup(View view, final int position) {
            View menuItemView = view.findViewById(R.id.btn_track_options);
            PopupMenu popup = new PopupMenu(ListTracks_Activity.this, menuItemView);
            MenuInflater inflate = popup.getMenuInflater();
            inflate.inflate(R.menu.tracks_list_menu, popup.getMenu());
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
                        default:
                            break;
                    }
                    return false;
                }
            });
            popup.show();
        }

    }

}
