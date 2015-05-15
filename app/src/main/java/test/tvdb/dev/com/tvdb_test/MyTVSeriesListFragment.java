package test.tvdb.dev.com.tvdb_test;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.model.Episode;
import com.omertron.thetvdbapi.model.SeriesUpdate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyTVSeriesListFragment extends Fragment
{
    private View rootView;
    private GridView gridView;
    private GridViewAdapter customGridAdapter;
    private ActionBar toolbar;
    private Database db;
    private ArrayList<MyTVSeries> series;
    private TheTVDBApi tvDB;
    private Handler finishLoadingHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.activity_seen,container,false);
        db=new Database(getActivity());
        toolbar=((ActionBarActivity)getActivity()).getSupportActionBar();
        toolbar.setTitle("My TV Series");
        gridView=(GridView)rootView.findViewById(R.id.gridView);
        finishLoadingHandler= new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if(series.size()>0)
                    new UpdateSeries().execute();
                return false;
            }
        });
        read();
        return rootView;
    }

    private class LoadImages extends AsyncTask<Void,Void,Void> {
        //ArrayList<MyTVSeries> series;

        @Override
        protected Void doInBackground(Void... params) {
            //series=read();                                NO MORE USELESS METHODS BRO!!
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            customGridAdapter=new GridViewAdapter(getActivity(), R.layout.grid_cell, series,finishLoadingHandler);
            try {
                gridView.setAdapter(customGridAdapter);
            } catch (NullPointerException exc) {
                Toast.makeText(getActivity(), "No TV Series added", Toast.LENGTH_SHORT).show();
            }
            //new UpdateSeries().execute();
        }
    }

    private void read()
    {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return (series = db.getSeries())!=null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(!result)
                    Toast.makeText(getActivity(),"Could not read from database", Toast.LENGTH_SHORT).show();
                else
                {
                    if(series.size()==0)
                        rootView.findViewById(R.id.no_tv_series).setVisibility(View.VISIBLE);
                    else
                        new LoadImages().execute();
                }
            }
        };
        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("My TV Series");
    }

    private class UpdateSeries extends AsyncTask<Void,Void,Void>
    {
        boolean isUpdated=false;

        @Override
        protected Void doInBackground(Void... params) {
            if(tvDB==null)
                tvDB=new TheTVDBApi("2C8BD989F33B0C84");
            try {
                SharedPreferences preferences = getActivity().getPreferences(Activity.MODE_PRIVATE);
                String timeTmp;
                if (!(timeTmp = tvDB.getWeeklyUpdates().getTime()).equals(preferences.getString("TIME", new Date().toString()))) {
                    isUpdated=true;
                    preferences.edit().putString("TIME", timeTmp).apply();
                    List<SeriesUpdate> updateList = tvDB.getWeeklyUpdates().getSeriesUpdates();
                    for (int i=0;i<updateList.size();i++)
                        for(int j=0;j<series.size();j++)
                            if(series.get(j).getID().equals(updateList.get(i).getId())) {
                                List<Episode> fullEpisodeUpdate=tvDB.getAllEpisodes(updateList.get(i).getId(),"en");
                                ArrayList<String> titleEpisodesUpdate=new ArrayList<>();
                                for(int k=0;i<fullEpisodeUpdate.size();k++)
                                    titleEpisodesUpdate.add(fullEpisodeUpdate.get(k).getEpisodeName());
                                //series.get(j).setEpisodes(titleEpisodesUpdate);
                                MyTVSeries tmp = series.get(j);
                                series.set(j, new MyTVSeries(tmp.getTitle(), tmp.getDescription(), tmp.getBitmapPoster(), titleEpisodesUpdate, tmp.getID(), tmp.getFirstAired(), tmp.getActors(), fullEpisodeUpdate));
                            }
                    new Database(getActivity()).updateSeries(series);
                }
            }
            catch(Exception exc)
            {
                exc.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(isUpdated) {
                Toast.makeText(getActivity(),"Updating your TV Series...",Toast.LENGTH_SHORT).show();
                customGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_cell, series, null);
                gridView.setAdapter(customGridAdapter);
            }
        }
    }
}
