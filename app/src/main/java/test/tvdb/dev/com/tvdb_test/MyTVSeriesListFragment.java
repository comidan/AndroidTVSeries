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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.activity_seen,container,false);
        db=new Database(getActivity());
        toolbar=((ActionBarActivity)getActivity()).getSupportActionBar();
        toolbar.setTitle("My TV Series");
        gridView=(GridView)rootView.findViewById(R.id.gridView);
        new LoadImages().execute();
        return rootView;
    }

    private class LoadImages extends AsyncTask<Void,Void,Void>
    {
        ArrayList<MyTVSeries> series;

        @Override
        protected Void doInBackground(Void... params) {
            series=read();
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

    private ArrayList<MyTVSeries> read()
    {
        /*try
        {
            FileInputStream fis=getActivity().openFileInput("TV_Series.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<MyTVSeries> object=(ArrayList<MyTVSeries>)ois.readObject();
            return object;
        }
        catch(FileNotFoundException exc)
        {
            return null;
        }
        catch(IOException exc)
        {
            return null;
        }
        catch(ClassNotFoundException exc)
        {
            return null;
        }*/
        return db.getSeries();
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("My TV Series");
    }
}
