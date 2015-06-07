package test.tvdb.dev.com.tvdb_test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Series;
import com.uwetrottmann.trakt.v2.TraktV2;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import retrofit.RetrofitError;

/**
 * Created by daniele on 07/06/2015.
 */
public class TrendsFragment extends Fragment
{
    private View rootView;
    private TheTVDBApi tvDB;
    private ProgressBar bar;
    private ArrayList<MyTVSeries> myTvSeries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.trends_fragment,container,false);
        bar=(ProgressBar)rootView.findViewById(R.id.progressBar);
        new LoginTVDB().execute();
        return rootView;
    }

    private class LoginTVDB extends AsyncTask<Void,Void,Void>
    {
        private ArrayList<Series> series;
        private Bitmap[] poster;

        @Override
        protected Void doInBackground(Void... params) {
            tvDB=new TheTVDBApi("2C8BD989F33B0C84");
            TraktV2 trakt = new TraktV2();
            trakt.setApiKey("ebd4307c9eb9a7afcf922ac13f6b6ea95bede7784c4fd067ce3916991b955986");
            trakt.setAccessToken("14c085f8ea2d0f4c061f8cde34f7dd8198402dd33e3559655ef2098175799886");  //could give problems
            series=new ArrayList<>();
            try
            {
                List<Show> shows=trakt.recommendations().shows(Extended.FULL);
                poster=new Bitmap[shows.size()];
                for(int i=0;i<shows.size();i++)
                {
                    String tmpID=shows.get(i).ids.tvdb+"";
                    series.add(tvDB.getSeries(tmpID, "en"));
                    try
                    {
                        InputStream in=new java.net.URL(series.get(i).getPoster()).openStream();
                        poster[i]=Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in),230,320,true);
                    }
                    catch(IndexOutOfBoundsException exc)
                    {
                        System.out.println("No poster has been found");
                        poster[i]=null; //no poster has been found
                    }
                    catch (MalformedURLException ex)
                    {
                        System.out.println("No poster has been found");
                        poster[i]=null; //no poster has been found
                    }
                    catch (IOException ex)
                    {
                        System.out.println("No poster has been found");
                        poster[i]=null; //no poster has been found
                    }
                }
            }
            catch(OAuthUnauthorizedException exc)
            {
                exc.printStackTrace();
            }
            catch (RetrofitError exc)
            {
                exc.printStackTrace();
            }
            catch(TvDbException exc)
            {
                exc.printStackTrace();
            }
            myTvSeries=new Database(getActivity()).getSeries();
            if(myTvSeries==null)
                myTvSeries=new ArrayList<>();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            bar.setVisibility(View.INVISIBLE);
            bar.setIndeterminate(false);
            GridView gridView=(GridView)rootView.findViewById(R.id.gridView);
            GridViewAdapter customGridAdapter = new GridViewAdapter(getActivity(),R.layout.grid_cell,series,poster,myTvSeries);
            gridView.setAdapter(customGridAdapter);
        }
    }
}
