package test.tvdb.dev.com.tvdb_test;

/**
 * Created by daniele on 07/03/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Episode;
import com.omertron.thetvdbapi.model.Series;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeriesInfo extends ActionBarActivity
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
        setContentView(R.layout.test);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String title=getIntent().getExtras().getString("TITLE");
        getSupportActionBar().setTitle(title);
        tabLayout=(SlidingTabLayout)findViewById(R.id.tabs);
        viewPager=(ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(),getIntent()));
        tabLayout.setViewPager(viewPager);
        tabLayout.setDistributeEvenly(true);
    }

    class MyFragmentAdapter extends FragmentPagerAdapter
    {
        private String[] tabs;
        private Intent intent;
        public MyFragmentAdapter(FragmentManager fm,Intent intent) {
            super(fm);
            tabs=getResources().getStringArray(R.array.tabs);
            this.intent=intent;
        }

        @Override
        public Fragment getItem(int position) {
            MyFragment fragment=MyFragment.getIstance(position,intent);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class MyFragment extends Fragment
    {
        private ListView episodesList;
        private List<Episode> episodeList;
        private TheTVDBApi tvDB;
        private View rootView;
        private static Intent intent;

        public static MyFragment getIstance(int position,Intent _intent)
        {
            MyFragment fragment=new MyFragment();
            intent=_intent;
            Bundle extras=intent.getExtras();
            extras.putInt("position",position);
            fragment.setArguments(extras);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Bundle extras=getArguments();
            if(extras.getInt("position")!=1) {
                rootView = inflater.inflate(R.layout.fragment_test, container, false);

                episodesList = (ListView) rootView.findViewById(R.id.listView);
                ArrayList<String> tmp=extras.getStringArrayList("EPISODES");
                boolean[] seen_tmp=new boolean[tmp.size()];
                Arrays.fill(seen_tmp,false);
                EpisodesAdapter adapter = new EpisodesAdapter(getActivity(),Arrays.copyOf(tmp.toArray(),tmp.size(),String[].class),
                                                              intent.getExtras().getString("ID"),seen_tmp);
                episodesList.setAdapter(adapter);
                /*episodesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        new FetchEpisode(view).execute();
                    }
                });*/
                return rootView;
            }
            else
            {
                rootView = inflater.inflate(R.layout.test_rating, container, false);
                new GetRating().execute();
                return rootView;
            }
        }

        private class GetRating extends AsyncTask<Void,Void,Void>
        {
            private String rating;

            @Override
            protected Void doInBackground(Void... params) {
                try
                {
                    if(tvDB==null)
                        tvDB=new TheTVDBApi("2C8BD989F33B0C84");  //TODO create one intance in the fragment management..
                    Series series=tvDB.getSeries(intent.getExtras().getString("ID"),"en");
                    rating=series.getRating();
                }
                catch(TvDbException exc)
                {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ((TextView)rootView.findViewById(R.id.textView)).setText(rating);
            }
        }

        /*private class FetchEpisode extends AsyncTask<Void,Void,Void>
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
                        episodeList=tvDB.getAllEpisodes(intent.getExtras().getString("ID"),"en");
                    }
                    catch(TvDbException exc)
                    {

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
                Intent intent=new Intent(getActivity(),EpisodeActivity.class);
                intent.putExtra("EPISODE",episode);
                startActivity(intent);
            }
        }*/
    }
}

