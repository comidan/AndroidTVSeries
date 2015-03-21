package test.tvdb.dev.com.tvdb_test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Episode;
import java.util.List;

public class SeriesActivity extends ActionBarActivity
{
    private ImageView image;
    private ListView episodesList;
    private TextView description;
    private Bundle extras;
    private TheTVDBApi tvDB;
    private List<Episode> episodeList;
    private SlidingTabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        extras=getIntent().getExtras();
        String title=extras.getString("TITLE");
        toolbar.setTitle("My TV Series");
        toolbar.setSubtitle(title);
        image=(ImageView)findViewById(R.id.poster);
        ViewCompat.setTransitionName(image,"SeriesActivity:image");
        byte[] bitmap=(byte[])extras.getSerializable("BITMAP");
        new DecodeByteArray().execute(bitmap);
        Button button=(Button)findViewById(R.id.more);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SeriesActivity.this,SeriesInfo.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
        description=(TextView)findViewById(R.id.description);
    }

    private class FetchEpisode extends AsyncTask<Void,Void,Void>
    {
        private Episode episode;
        private String title;

        public FetchEpisode(View view)
        {
            title=((TextView)view.findViewById(android.R.id.text1)).getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(episodeList==null)
            {
                tvDB=new TheTVDBApi("2C8BD989F33B0C84");
                try
                {
                    episodeList=tvDB.getAllEpisodes(getIntent().getExtras().getString("ID"),"en");
                }
                catch(TvDbException exc)
                {
                    finish();
                }
            }
           for(int i=0;i<episodeList.size();i++)
                if(episodeList.get(i).getEpisodeName().equals(title))
                {
                     episode=episodeList.get(i);
                     break;
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent=new Intent(SeriesActivity.this,EpisodeActivity.class);
            intent.putExtra("EPISODE",episode);
            startActivity(intent);
        }
    }

    private class LoginTVDB extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            tvDB=new TheTVDBApi("2C8BD989F33B0C84");
            try
            {
                episodeList=tvDB.getAllEpisodes(getIntent().getExtras().getString("ID"),"en");
            }
            catch(TvDbException exc)
            {
                finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(SeriesActivity.this, "You can now search a TV Series", Toast.LENGTH_SHORT).show();
        }
    }

    public static void launchAndAnimate(Activity activity,View transitionView,Intent intent)
    {
        ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(activity,transitionView,"SeriesActivity:image");
        ActivityCompat.startActivity(activity,intent,options.toBundle());
    }

    private class DecodeByteArray extends AsyncTask<byte[],Void,Void>
    {
        private Bitmap bitmap;

        @Override
        protected Void doInBackground(byte[][] params)
        {
            bitmap= BitmapFactory.decodeByteArray(params[0],0,params[0].length);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            image.setImageBitmap(bitmap);
            description.setText(extras.getString("DESCRIPTION"));
        }
    }
}
