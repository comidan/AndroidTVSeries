package test.tvdb.dev.com.tvdb_test;

/**
 * Created by daniele on 07/03/2015.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.melnykov.fab.FloatingActionButton;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Episode;
import com.omertron.thetvdbapi.model.Series;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
        private List<Episode> episodeList;
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
                            showDate.fillMetaData(metaData,tmpSeasons);
                            System.out.println("Season "+metaData.season+" episode "+metaData.index);
                            new FetchDate().execute(metaData);
                        }
                    });

                    /*for(int i=0;i<tmpSeasons.size();i++)
                        for(int j=0;j<tmpSeasons.get(i).getEpisodesList().size();j++)
                            tmp.add(tmpSeasons.get(i).getEpisodesList().get(j).getEpisodeName());*/
                    for(Season s : tmpSeasons)
                        for(int i=1; i<=s.getTotEpisodes(); i++) {
                            tmp.add(s.getEpisode(i).getEpisodeName());
                            tmpIDs.add(s.getEpisode(i).getId());
                        }
                    //ArrayList<String> tmp = extras.getStringArrayList("EPISODES");
                    /*boolean[] seen_tmp = new boolean[tmp.size()];
                    Arrays.fill(seen_tmp, false);*/

                    ShowDate showDate=new ShowDate(watches);
                    MetaEpisode metaData=new MetaEpisode();
                    metaData.id=extras.getString("ID");
                    showDate.fillMetaData(metaData,tmpSeasons);
                    System.out.println("Season "+metaData.season+" episode "+metaData.index);
                    new FetchDate().execute(metaData);
                    EpisodesAdapter adapter = new EpisodesAdapter(getActivity(), Arrays.copyOf(tmp.toArray(), tmp.size(), String[].class),
                            intent.getExtras().getString("ID"),updatedWatches,watches,Arrays.copyOf(tmpIDs.toArray(), tmpIDs.size(), String[].class));
                    episodesList.setAdapter(adapter);
                        /*episodesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                new FetchEpisode(view).execute();
                            }
                        });*/
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
                    System.out.println("Offset "+params[0].seasonOffset);
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
                            outputDate = "Next air on ";
                        else
                            outputDate = "Aired on ";
                        outputDate += splitDate[2] + "/" + splitDate[1] + "/" + splitDate[0];
                        System.out.print("Date : ");
                        System.out.println(outputDate);
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

        private class UpdatedWatches extends AsyncTask<ArrayList<String>,Void,Void>
        {
            @Override
            protected Void doInBackground(ArrayList<String>... params) {
                Database db=new Database(getActivity());
                SQLiteDatabase sqlDb=db.getWritableDatabase();
                ContentValues contentValues=new ContentValues();
                for(int i=0;i<params[0].size();i++) {
                    String data=params[0].get(i);
                    String[] meta=data.split(" ");
                    contentValues.put("SEEN",Integer.parseInt(meta[1]));
                    sqlDb.update("EPISODES",contentValues,"ID_EPISODES="+meta[0],null);
                }
                sqlDb.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(),"Watches updated",Toast.LENGTH_SHORT).show();
            }
        }

        /*private class LoadWatches extends AsyncTask<Void,Void,Void>
        {
            private ArrayList<Boolean> watches=new ArrayList<>();

            @Override
            protected Void doInBackground(Void... params) {

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }*/

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

