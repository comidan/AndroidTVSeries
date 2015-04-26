package test.tvdb.dev.com.tvdb_test;

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
import java.util.ArrayList;

public class MyTVSeriesListFragment extends Fragment
{
    private View rootView;
    private GridView gridView;
    private GridViewAdapter customGridAdapter;
    private ActionBar toolbar;
    private Database db;
    private ArrayList<MyTVSeries> series;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.activity_seen,container,false);
        db=new Database(getActivity());
        toolbar=((ActionBarActivity)getActivity()).getSupportActionBar();
        toolbar.setTitle("My TV Series");
        gridView=(GridView)rootView.findViewById(R.id.gridView);
        read();
        return rootView;
    }

    private class LoadImages extends AsyncTask<Void,Void,Void>
    {
        //ArrayList<MyTVSeries> series;

        @Override
        protected Void doInBackground(Void... params) {
            //series=read();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //System.out.println(series.size()+" "+series.get(0).getSeasons().size()+" "+series.get(0).getSeasons().get(0).getEpisodesList().size());
            for(int i=0;i<series.get(0).getSeasons().size();i++)
                for(int j=0;j<series.get(0).getSeasons().get(i).getEpisodesList().size();j++)
                    System.out.println(series.get(0).getSeasons().get(i).getEpisodesList().get(j).getEpisodeName());
            customGridAdapter = new GridViewAdapter(getActivity(),R.layout.grid_cell,series);
            try
            {
                gridView.setAdapter(customGridAdapter);
            }
            catch (NullPointerException exc)
            {
                Toast.makeText(getActivity(),"No TV Series added",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void read()
    {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if((series = db.getSeries()) != null)
                    return true;
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(!result)
                    Toast.makeText(getActivity(),"Could not read from database", Toast.LENGTH_SHORT).show();
                new LoadImages().execute();
            }
        };
        task.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("My TV Series");
    }
}
