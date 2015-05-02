package test.tvdb.dev.com.tvdb_test;

/**
 * Created by daniele on 07/03/2015.
 */

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.melnykov.fab.FloatingActionButton;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Episode;
import com.omertron.thetvdbapi.model.Series;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SeriesInfo extends ActionBarActivity
{
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabLayout=(SlidingTabLayout)findViewById(R.id.tabs);
        viewPager=(ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(),getIntent()));
        tabLayout.setViewPager(viewPager);
        tabLayout.setDistributeEvenly(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
            MyFragment fragment=MyFragment.getIstance(position,intent,toolbar);
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
        private ListView episodesList,actorList;
        private TheTVDBApi tvDB;
        private View rootView;
        private static Toolbar toolbar;
        private static Intent intent;

        public static MyFragment getIstance(int position,Intent _intent,Toolbar _toolbar)
        {
            MyFragment fragment=new MyFragment();
            intent=_intent;
            toolbar=_toolbar;
            Bundle extras=intent.getExtras();
            extras.putInt("position",position);
            fragment.setArguments(extras);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final Bundle extras = getArguments();
            switch (extras.getInt("position")) {
                case 0:
                    rootView = inflater.inflate(R.layout.fragment_test, container, false);
                    final ArrayList<String> updatedWatches=new ArrayList<>();
                    episodesList = (ListView) rootView.findViewById(R.id.listView);
                    final ArrayList<Season> tmpSeasons =(ArrayList<Season>)extras.getSerializable("EPISODES");
                    ArrayList<String> tmp=new ArrayList<>(),tmpIDs=new ArrayList<>();
                    Database db=new Database(getActivity());
                    SQLiteDatabase sqlDb=db.getReadableDatabase();
                    final ArrayList<Boolean> watches=new ArrayList<>();
                    for(Season s : tmpSeasons)
                        for(int i=1; i<=s.getTotEpisodes(); i++){
                            Cursor cursor=sqlDb.rawQuery("SELECT SEEN FROM EPISODES WHERE ID_EPISODES=" + s.getEpisode(i).getId(), null);
                            while(cursor.moveToNext())
                                watches.add(cursor.getInt(cursor.getColumnIndex("SEEN"))==0 ? false : true);
                        }
                    FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
                    fab.attachToListView(episodesList);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new UpdatedWatches().execute(updatedWatches);
                            ShowDate showDate=new ShowDate(watches);
                            MetaEpisode metaData=new MetaEpisode();
                            metaData.id=extras.getString("ID");
                            showDate.fillMetaData(metaData, tmpSeasons);
                            new FetchDate().execute(metaData);
                        }
                    });
                    for(Season s : tmpSeasons)
                        for(int i=1; i<=s.getTotEpisodes(); i++) {
                            tmp.add(s.getEpisode(i).getEpisodeName());
                            tmpIDs.add(s.getEpisode(i).getId());
                        }
                    ShowDate showDate=new ShowDate(watches);
                    MetaEpisode metaData=new MetaEpisode();
                    metaData.id=extras.getString("ID");
                    showDate.fillMetaData(metaData, tmpSeasons);
                    new FetchDate().execute(metaData);
                    EpisodesAdapter adapter = new EpisodesAdapter(getActivity(), Arrays.copyOf(tmp.toArray(), tmp.size(), String[].class),
                            intent.getExtras().getString("ID"),updatedWatches,watches,Arrays.copyOf(tmpIDs.toArray(), tmpIDs.size(), String[].class));
                    episodesList.setAdapter(adapter);
                    return rootView;
                case 1:
                    rootView = inflater.inflate(R.layout.test_rating, container, false);
                    new GetRating().execute();
                    return rootView;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_test, container, false);
                    actorList = (ListView) rootView.findViewById(R.id.listView);
                    ArrayList<String> _tmp = extras.getStringArrayList("ACTORS");
                    ArrayAdapter<String> _adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,
                                                                           Arrays.copyOf(_tmp.toArray(), _tmp.size(), String[].class));
                    actorList.setAdapter(_adapter);

                default : return rootView;

            }
        }

        private class FetchDate extends AsyncTask<MetaEpisode,Void,Void>
        {
            private Episode episode;
            private String outputDate;

            @Override
            protected Void doInBackground(MetaEpisode... params) {
                try
                {
                    if(tvDB==null)
                        tvDB=new TheTVDBApi("2C8BD989F33B0C84");
                    if(!params[0].full) {
                        if (params[0].season == 0)
                            params[0].season += params[0].seasonOffset;
                        episode = tvDB.getEpisode(params[0].id, params[0].season, params[0].index, "en");
                        String date = episode.getFirstAired();
                        String[] splitDate = date.split("-");
                        Date currentDate = new Date();
                        Calendar episodeDate = Calendar.getInstance();
                        episodeDate.set(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]) - 1, Integer.parseInt(splitDate[2]));
                        if (currentDate.compareTo(episodeDate.getTime()) < 0)
                            outputDate = "Next episode air on ";
                        else
                            outputDate = "Next episode aired on ";
                        outputDate += splitDate[2] + "/" + splitDate[1] + "/" + splitDate[0];
                    }
                    else
                        outputDate="Concluded";
                }
                catch(TvDbException exc)
                {
                    exc.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                toolbar.setSubtitle(outputDate);
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

        private class UpdatedWatches extends AsyncTask<ArrayList<String>,Void,Void> {
            @Override
            protected Void doInBackground(ArrayList<String>... params) {
                Database db = new Database(getActivity());
                SQLiteDatabase sqlDb = db.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                for (int i = 0; i < params[0].size(); i++) {
                    String data = params[0].get(i);
                    String[] meta = data.split(" ");
                    contentValues.put("SEEN", Integer.parseInt(meta[1]));
                    sqlDb.update("EPISODES", contentValues, "ID_EPISODES=" + meta[0], null);
                }
                sqlDb.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), "Watches updated", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

