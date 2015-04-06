package test.tvdb.dev.com.tvdb_test;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class MyTVSeries implements Serializable
{
    private String title,description,id;
    private ArrayList<String> episodes,actors;
    private byte[] image;

    public MyTVSeries(String title, String description, Bitmap poster, ArrayList<String> episodes,String id,ArrayList<String> actors)
    {
        this.title = title;
        this.description = description;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        poster.compress(Bitmap.CompressFormat.PNG, 0, bos);
        image=bos.toByteArray();
        this.episodes = episodes;
        this.id=id;
        this.actors=actors;
    }

    public void setEpisodes(ArrayList<String> episodes)
    {
        this.episodes=episodes;
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

    public ArrayList<String> getActors()
    {
        return actors;
    }
}
