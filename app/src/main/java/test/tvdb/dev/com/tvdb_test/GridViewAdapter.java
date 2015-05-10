package test.tvdb.dev.com.tvdb_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.omertron.thetvdbapi.model.Episode;
import com.omertron.thetvdbapi.model.Series;
import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<MyTVSeries> data,backup;
    private boolean isCalledFromSearch;

    public GridViewAdapter(Context context,int layoutResourceId,ArrayList<MyTVSeries> data)
    {
        super(context, layoutResourceId, data);
        isCalledFromSearch=false;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public GridViewAdapter(Context context,int layoutResourceId,List<Series> series,List<Episode>[] episodes,Bitmap[] posters,
                           ArrayList<MyTVSeries> backup)
    {
        super(context,layoutResourceId,series);
        isCalledFromSearch=true;
        this.backup=backup;
        this.layoutResourceId=layoutResourceId;
        this.context=context;
        data=new ArrayList<>();
        ArrayList<String>[] _episodes=new ArrayList[episodes.length];
        for(int i=0;i<episodes.length;i++)
        {
            _episodes[i]=new ArrayList<>();
            System.out.println(i);
            for (int j=0;j<episodes[i].size();j++)
                _episodes[i].add(episodes[i].get(j).getEpisodeName());
        }
        for(int i=0;i<series.size();i++)
        {
            ArrayList<String> actors=new ArrayList<>();
            List<String> _actors=series.get(i).getActors();
            for(int j=0;j<series.get(i).getActors().size();j++)
                actors.add(_actors.get(j));
            data.add(new MyTVSeries(series.get(i).getSeriesName(), series.get(i).getOverview(), posters[i], _episodes[i], series.get(i).getId(), series.get(i).getFirstAired(),actors,episodes[i]));
        }
    }

    public GridViewAdapter(Context context,int layoutResourceId,List<Series> series,Bitmap[] posters,
                           ArrayList<MyTVSeries> backup)
    {
        super(context,layoutResourceId,series);
        isCalledFromSearch=true;
        this.backup=backup;
        this.layoutResourceId=layoutResourceId;
        this.context=context;
        data=new ArrayList<>();
        for(int i=0;i<series.size();i++) {
            ArrayList<String> actors = new ArrayList<>();
            List<String> _actors = series.get(i).getActors();
            for (int j = 0; j < series.get(i).getActors().size(); j++)
                actors.add(_actors.get(j));
            data.add(new MyTVSeries(series.get(i).getSeriesName(), series.get(i).getOverview(), posters[i], null, series.get(i).getId(), series.get(i).getFirstAired(),actors, null));
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder=null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        MyTVSeries item = data.get(position);
        holder.imageTitle.setText(item.getTitle());
        final Container container=new Container();
        container.data=item.getPoster();
        container.imageView=holder.image;
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SeriesActivity.class);
                intent.putExtra("BITMAP",container.data);
                intent.putExtra("TITLE",data.get(position).getTitle());
                intent.putExtra("DESCRIPTION",data.get(position).getDescription());
                intent.putExtra("ID",data.get(position).getID());
                intent.putExtra("EPISODES",data.get(position).getSeasons());
                intent.putStringArrayListExtra("ACTORS",data.get(position).getActors());
                intent.putExtra("IS_SEARCHED",isCalledFromSearch);
                if(isCalledFromSearch)
                {
                    boolean exists=false;
                    for(int i=0;i<backup.size();i++)
                        if(backup.get(i).getID().equals(data.get(position).getID()))
                        {
                            exists=true;
                            break;
                        }
                    intent.putExtra("ADD",exists);
                }
                SeriesActivity.launchAndAnimate((Activity)context,container.imageView,intent);
            }
        });
        new DecodeByteArray().execute(container);
        return row;
    }

    private class Container
    {
        byte[] data;
        ImageView imageView;
    }


    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }

    private class DecodeByteArray extends AsyncTask<Container,Void,Void>
    {
        private Bitmap bitmap;
        private Container container;
        @Override
        protected Void doInBackground(Container[] params)
        {
            container=params[0];
            bitmap=BitmapFactory.decodeByteArray(container.data,0,container.data.length);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            container.imageView.setImageBitmap(bitmap);
        }
    }
}