package test.tvdb.dev.com.tvdb_test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MyTVSeries implements Serializable
{
    private String title,description,id;
    private ArrayList<String> episodes;
    private byte[] image;

    public MyTVSeries(String title, String description, Bitmap poster, ArrayList<String> episodes,String id)
    {
        this.title = title;
        this.description = description;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        poster.compress(Bitmap.CompressFormat.PNG, 0, bos);
        image=bos.toByteArray();
        this.episodes = episodes;
        this.id=id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getPoster() {
        return image;
    }

    public byte[] getBitmapArray()
    {
        return image;
    }

    public ArrayList<String> getEpisodes() {
        return episodes;
    }

    public String getID() {return id;}
}
