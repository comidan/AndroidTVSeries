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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MyTVSeriesListFragment extends Fragment
{
    private View rootView;
    private GridView gridView;
    private GridViewAdapter customGridAdapter;
    private ActionBar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.activity_seen,container,false);
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
        try
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("My TV Series");
    }
}
