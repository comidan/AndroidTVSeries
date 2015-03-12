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
import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<MyTVSeries> data;
    private ImageView imageView;

    public GridViewAdapter(Context context, int layoutResourceId,ArrayList<MyTVSeries> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
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
                System.out.println(data.get(position).getTitle()+" "+data.get(position).getID());
                intent.putExtra("ID",data.get(position).getID());
                intent.putStringArrayListExtra("EPISODES",data.get(position).getEpisodes());
                //context.startActivity(intent);
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