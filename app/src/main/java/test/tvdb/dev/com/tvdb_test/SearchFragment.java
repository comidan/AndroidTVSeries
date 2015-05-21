package test.tvdb.dev.com.tvdb_test;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Banners;
import com.omertron.thetvdbapi.model.Episode;
import com.omertron.thetvdbapi.model.Series;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private ProgressBar bar;
    private EditText editText;
    private Button search;
    private TheTVDBApi tvDB;
    private ArrayList<MyTVSeries> myTvSeries;
    private View rootView;
    private Database db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.search,container,false);
        db=new Database(getActivity());
        bar=(ProgressBar)rootView.findViewById(R.id.progressBar);
        editText=(EditText)rootView.findViewById(R.id.search_box);
        search=(Button)rootView.findViewById(R.id.search_button);
        db = new Database(getActivity());
        myTvSeries=read();
        if(myTvSeries==null)
            myTvSeries=new ArrayList<>();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadSeriesData().execute(editText.getText().toString());
            }
        });
        bar.setVisibility(View.VISIBLE);
        bar.setIndeterminate(true);
        new LoginTVDB().execute();
        return rootView;
    }

    private class LoginTVDB extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            tvDB=new TheTVDBApi("2C8BD989F33B0C84");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            bar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(),"You can now search a TV Series",Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadSeriesData extends AsyncTask<String,Void,Bitmap[]>  //TODO implement serial AsyncTasks for each series
    {
        private List<Episode>[] episodes;
        private List<Series> results,backup;

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            bar.setIndeterminate(true);
        }

        @Override
        protected Bitmap[] doInBackground(String[] params)
        {
            try
            {
                results=tvDB.searchSeries(params[0],"en");
                backup=new ArrayList<>();
                backup.addAll(results);  //needed in case of null posters
                episodes=new List[results.size()];
                Bitmap[] poster=new Bitmap[results.size()];
                for(int i=0,j=0;i<episodes.length;i++,j++)
                {
                    try
                    {
                        Series tmp;
                        System.out.println((tmp=tvDB.getSeries(results.get(i).getId(),"en")).getPoster());
                        InputStream in=new java.net.URL(tmp.getPoster()).openStream();
                        poster[i]=Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in),230,320,true);
                        System.out.println(i);
                    }
                    catch(IndexOutOfBoundsException exc)
                    {
                        System.out.println("No poster has been found");
                        poster[i]=null; //no poster has been found
                        backup.remove(j);
                        j--;
                    }
                    catch (MalformedURLException ex)
                    {
                        System.out.println("No poster has been found");
                        poster[i]=null; //no poster has been found
                        backup.remove(j);
                        j--;
                    }
                    catch (IOException ex)
                    {
                        System.out.println("No poster has been found");
                        poster[i]=null; //no poster has been found
                        backup.remove(j);
                        j--;
                    }
                }
                Bitmap[] validPosters=new Bitmap[backup.size()];
                for(int i=0,j=0;i<poster.length;i++)
                    if(poster[i]!=null)
                    {
                        validPosters[j]=poster[i];
                        j++;
                    }
                return validPosters;
            }
            catch(TvDbException e)
            {
                e.printStackTrace();
                System.out.println(results.size());
            }
            catch (VerifyError e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap[] bitmap) {
            bar.setVisibility(View.INVISIBLE);
            bar.setIndeterminate(false);
            GridView gridView=(GridView)rootView.findViewById(R.id.gridView);
            GridViewAdapter customGridAdapter = new GridViewAdapter(getActivity(),R.layout.grid_cell,backup,bitmap,myTvSeries);
            gridView.setAdapter(customGridAdapter);
        }
    }

    private ArrayList<MyTVSeries> read()
    {
        return db.getSeries();
    }

    private void write(final ArrayList<MyTVSeries> tvSeries)
    {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if(db.storeSeries(tvSeries))
                    return true;
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result)
                    Toast.makeText(getActivity(),"Added successfully", Toast.LENGTH_SHORT).show();
                else Toast.makeText(getActivity(),"Problem while adding", Toast.LENGTH_SHORT).show();
            }
        };
        task.execute();

    }
}
