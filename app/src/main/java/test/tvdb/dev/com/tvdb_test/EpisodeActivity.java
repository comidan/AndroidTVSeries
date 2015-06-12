package test.tvdb.dev.com.tvdb_test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.omertron.thetvdbapi.model.Episode;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;

public class EpisodeActivity extends ActionBarActivity
{
    private Episode episode;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_activity);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        episode=(Episode)getIntent().getExtras().getSerializable("EPISODE");
        if(toolbar!=null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(episode.getEpisodeName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(episode.getEpisodeName());
        new DownloadEpisodeImage().execute(episode.getFilename());
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

    private class DownloadEpisodeImage extends AsyncTask<String,Void,Bitmap>
    {
        private String outputDate;

        @Override
        protected Bitmap doInBackground(String... params)
        {
            Bitmap image=null;
            try
            {
                InputStream in=new java.net.URL(params[0]).openStream();
                image=BitmapFactory.decodeStream(in);
            }
            catch (MalformedURLException ex)
            {
                ex.printStackTrace();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            String[] splitDate=episode.getFirstAired().split("-");
            Date currentDate = new Date();
            Calendar episodeDate = Calendar.getInstance();
            episodeDate.set(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]) - 1, Integer.parseInt(splitDate[2]));
            if (currentDate.compareTo(episodeDate.getTime()) < 0)
                outputDate = "Next air on ";
            else
                outputDate = "Aired on ";
            outputDate += splitDate[2] + "/" + splitDate[1] + "/" + splitDate[0];
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ((ImageView)findViewById(R.id.poster_episode)).setImageBitmap(bitmap);
            ((TextView)findViewById(R.id.description_episode)).setText(episode.getOverview());
            ((TextView)findViewById(R.id.episode_date)).setText(outputDate);
            PieGraph pg = (PieGraph)findViewById(R.id.graph);
            pg.setThickness(20);
            PieSlice slice=new PieSlice();
            slice.setColor(Color.WHITE);
            float tmp_rating;
            slice.setValue(tmp_rating=Float.parseFloat(episode.getRating()));
            pg.addSlice(slice);
            PieSlice _slice=new PieSlice();
            _slice.setColor(Color.parseColor("#00FFFFFF"));
            _slice.setValue(11-tmp_rating);
            pg.addSlice(_slice);
            ((TextView)findViewById(R.id.textView)).setText(episode.getRating()+"/10");
        }
    }
}
