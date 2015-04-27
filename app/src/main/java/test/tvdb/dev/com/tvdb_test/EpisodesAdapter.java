package test.tvdb.dev.com.tvdb_test;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Episode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniele on 22/03/2015.
 */

public class EpisodesAdapter extends ArrayAdapter<String>
{
    private final Context context;
    private final String[] values;
    private TheTVDBApi tvDB;
    private List<Episode> episodeList;
    private String id;
    private ArrayList<String> updatedWatches;
    private ArrayList<Boolean> watches;
    private String[] IDs;

    public EpisodesAdapter(Context context, String[] values, String id,ArrayList<String> updatedWatches,ArrayList<Boolean> watches,String[] IDs) {
        super(context,R.layout.episode_row, values);
        this.context = context;
        this.values = values;
        this.id = id;
        this.updatedWatches=updatedWatches;
        if(this.updatedWatches==null)
            this.updatedWatches=new ArrayList<>();
        this.watches=watches;
        this.IDs=IDs;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.episode_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.episode_label);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.episode_seen);
        checkBox.setChecked(watches.get(position));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                watches.set(position,isChecked);
                updatedWatches.add(IDs[position] + " " + (isChecked ? 1 : 0));
            }
        });
        textView.setText(values[position]);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchEpisode(rowView).execute();
            }
        });
        return rowView;
    }

    private class FetchEpisode extends AsyncTask<Void,Void,Void>
    {
        private Episode episode;
        private String title;

        public FetchEpisode(View view)
        {
            title=((TextView)view.findViewById(R.id.episode_label)).getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(episodeList==null)
            {
                tvDB=new TheTVDBApi("2C8BD989F33B0C84");
                try
                {
                    episodeList=tvDB.getAllEpisodes(id,"en");
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
            Intent intent=new Intent(context,EpisodeActivity.class);
            intent.putExtra("EPISODE",episode);
            context.startActivity(intent);
        }
    }
}