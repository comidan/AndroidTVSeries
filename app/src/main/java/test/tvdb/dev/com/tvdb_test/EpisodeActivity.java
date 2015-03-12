package test.tvdb.dev.com.tvdb_test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.omertron.thetvdbapi.model.Episode;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class EpisodeActivity extends Activity
{
    private Episode episode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_activity);
        episode=(Episode)getIntent().getExtras().getSerializable("EPISODE");
        setTitle(episode.getEpisodeName());
        new DownloadEpisodeImage().execute(episode.getFilename());
    }

    private class DownloadEpisodeImage extends AsyncTask<String,Void,Bitmap>
    {
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
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ((ImageView)findViewById(R.id.poster_episode)).setImageBitmap(bitmap);
            ((TextView)findViewById(R.id.description_episode)).setText(episode.getOverview());
        }
    }
}
