package test.tvdb.dev.com.tvdb_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Actor;
import com.omertron.thetvdbapi.model.Episode;
import java.util.ArrayList;
import java.util.List;

public class SeriesActivity extends ActionBarActivity
{
    private ImageView image;
    private TextView description;
    private Bundle extras;
    private TheTVDBApi tvDB;
    private List<Episode> episodeList;
    private Toolbar toolbar;
    private Database db;
    private ArrayList<MyTVSeries> series;
    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        extras=getIntent().getExtras();
        final String title=extras.getString("TITLE");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        image=(ImageView)findViewById(R.id.poster);
        boolean add=extras.getBoolean("ADD");
        if(!add&&extras.getBoolean("IS_SEARCHED"))
        {
            ImageButton addSeries=(ImageButton)findViewById(R.id.add_series);
            addSeries.setVisibility(View.VISIBLE);
            addSeries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SeriesActivity.this,"Saving your TV Series",Toast.LENGTH_SHORT).show();
                    new DownloadEpisodes().execute();
                }
            });
        }
        ViewCompat.setTransitionName(image,"SeriesActivity:image");
        byte[] bitmap=(byte[])extras.getSerializable("BITMAP");
        new DecodeByteArray().execute(bitmap);
        Button button=(Button)findViewById(R.id.more);
        if(extras.getBoolean("IS_SEARCHED"))
            button.setVisibility(View.GONE);
        else
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(SeriesActivity.this,SeriesInfo.class);  //you should call this following 3 lines on the post
                    intent.putExtras(extras);                                        //execute of aTask after downloading actorsList or
                    startActivity(intent);                                           //wait for the actor implementation in the DB
                }
            });
        description=(TextView)findViewById(R.id.description);
        db = new Database(this);
        series = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                showDeleteDialog();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(!extras.getBoolean("IS_SEARCHED"))
            getMenuInflater().inflate(R.menu.menu_series_activity, menu);
        return true;
    }

    public void showDeleteDialog(){
        AlertDialog.Builder ad = new Builder(this);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTVSeries();
                handler.sendEmptyMessage(0);
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.setTitle("Delete TVSeries");
        ad.setMessage("Are you sure you want to delete this TVSeries?");
        AlertDialog dlg = ad.create();
        dlg.show();
    }

    public void deleteTVSeries(){
        final Toast t = Toast.makeText(this, "Unable to delete the TVSeries", Toast.LENGTH_SHORT);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                if(db.deleteSerie(extras.getString("ID")))
                    return true;
                else return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result){
                    //TODO RETURN TO THE GRID
                }
                else
                    t.show();
            }
        };
        task.execute();
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
                    Toast.makeText(SeriesActivity.this,"Added successfully", Toast.LENGTH_SHORT).show();
                else Toast.makeText(SeriesActivity.this,"Problem while adding", Toast.LENGTH_SHORT).show();
            }
        };
        task.execute();

    }

    private ArrayList<MyTVSeries> read()
    {
        /*AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if((series = db.getSeries()) != null)
                    return true;
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(!result)
                    Toast.makeText(SeriesActivity.this,"Could not read from database", Toast.LENGTH_SHORT).show();
            }
        };
        task.execute();*/
        return db.getSeries();

    }

    private class FetchEpisode extends AsyncTask<Void,Void,Void>
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
                    episodeList=tvDB.getAllEpisodes(getIntent().getExtras().getString("ID"),"en");
                }
                catch(TvDbException exc)
                {
                    finish();
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
            Intent intent=new Intent(SeriesActivity.this,EpisodeActivity.class);
            intent.putExtra("EPISODE",episode);
            startActivity(intent);
        }
    }

    private class DownloadEpisodes extends AsyncTask<Void,Void,Void>
    {
        private ArrayList<String> actors;
        private String serieFirstAired;

        @Override
        protected Void doInBackground(Void... params) {
            if(tvDB==null)
            {
                tvDB=new TheTVDBApi("2C8BD989F33B0C84");
                try
                {
                    episodeList=tvDB.getAllEpisodes(getIntent().getExtras().getString("ID"),"en");
                    serieFirstAired = tvDB.getSeries(getIntent().getExtras().getString("ID"),"en").getFirstAired();
                    List<Actor> tmpList=tvDB.getActors(getIntent().getExtras().getString("ID"));
                    System.out.println("Actors size "+tmpList.size());
                    actors=new ArrayList<>();
                    for(int i=0;i<tmpList.size();i++)
                        actors.add(tmpList.get(i).getName());
                }
                catch(TvDbException exc)
                {
                    finish();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            series=read();
            //read();
            Bitmap bitmap=((BitmapDrawable)image.getDrawable()).getBitmap();
            if(series==null)
                series=new ArrayList<MyTVSeries>();
            ArrayList<String> _episodes=new ArrayList();
            for(int i=0;i<episodeList.size();i++)
                _episodes.add(episodeList.get(i).getEpisodeName());
            //if(extras.getStringArrayList("ACTORS").size()==0)
            extras.putSerializable("ACTORS",actors);
            MyTVSeries tmp=new MyTVSeries(extras.getString("TITLE"),description.getText().toString(),bitmap,_episodes,extras.getString("ID"),
                                          serieFirstAired, extras.getStringArrayList("ACTORS"), episodeList);
            extras.putSerializable("EPISODES",tmp.getSeasons());
            series.add(tmp);
            write(series);
        }
    }

    private class LoginTVDB extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            tvDB=new TheTVDBApi("2C8BD989F33B0C84");
            try
            {
                episodeList=tvDB.getAllEpisodes(getIntent().getExtras().getString("ID"),"en");
            }
            catch(TvDbException exc)
            {
                finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(SeriesActivity.this, "You can now search a TV Series", Toast.LENGTH_SHORT).show();
        }
    }

    public static void launchAndAnimate(Activity activity,View transitionView,Intent intent,Handler _handler)
    {
        handler=_handler;
        ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(activity,transitionView,"SeriesActivity:image");
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private class DecodeByteArray extends AsyncTask<byte[],Void,Void>
    {
        private Bitmap bitmap;

        @Override
        protected Void doInBackground(byte[][] params)
        {
            bitmap=BitmapFactory.decodeByteArray(params[0],0,params[0].length);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            image.setImageBitmap(bitmap);
            description.setText(extras.getString("DESCRIPTION"));
        }
    }
}
