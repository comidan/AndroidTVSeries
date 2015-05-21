package test.tvdb.dev.com.tvdb_test;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Episode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by daniele on 22/03/2015.
 */

public class EpisodesAdapter extends BaseExpandableListAdapter
{
    private final Context context;
    private final String[] values;
    private TheTVDBApi tvDB;
    private List<Episode> episodeList;
    private String id;
    private ArrayList<String> updatedWatches;
    private ArrayList<ArrayList<Boolean>> watches;
    private ArrayList<ArrayList<String>> IDs;
    private ArrayList<Season> fullValues;

    public EpisodesAdapter(Context context, String[] values, String id,ArrayList<String> updatedWatches,ArrayList<ArrayList<Boolean>> watches,
                           ArrayList<ArrayList<String>> IDs,ArrayList<Season> fullValues) {
        //super(context,R.layout.episode_row, values);
        this.context = context;
        this.values = values;
        this.id = id;
        this.updatedWatches=updatedWatches;
        if(this.updatedWatches==null)
            this.updatedWatches=new ArrayList<>();
        this.watches=watches;
        this.IDs=IDs;
        this.fullValues=fullValues;

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
                             final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.episode_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.episode_label);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.episode_seen);
        checkBox.setChecked(watches.get(groupPosition).get(childPosition));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                watches.get(groupPosition).set(childPosition,isChecked);
                updatedWatches.add(IDs.get(groupPosition).get(childPosition) + " " + (isChecked ? 1 : 0));
            }
        });
        textView.setText(fullValues.get(groupPosition).getEpisodesList().get(childPosition).getEpisodeName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchEpisode(rowView).execute();
            }
        });
        return rowView;
    }

    @Override
    public int getGroupCount() {
        return fullValues.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return fullValues.get(groupPosition).getTotEpisodes();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return fullValues.get(groupPosition).getSeasonNumber();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return fullValues.get(groupPosition).getEpisodesList().get(childPosition).getEpisodeName();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }


    private static final class ViewHolder {
        TextView textLabel;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View resultView = convertView;
        ViewHolder holder;

        if (resultView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            resultView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            holder = new ViewHolder();
            holder.textLabel = (TextView) resultView.findViewById(android.R.id.text1);
            resultView.setTag(holder);
        } else {
            holder = (ViewHolder) resultView.getTag();
        }

        holder.textLabel.setText("Season "+fullValues.get(groupPosition).getSeasonNumber());

        return resultView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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