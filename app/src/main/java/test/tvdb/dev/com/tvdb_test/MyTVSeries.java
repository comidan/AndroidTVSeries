package test.tvdb.dev.com.tvdb_test;

import android.graphics.Bitmap;
import android.util.Log;

import com.omertron.thetvdbapi.model.Episode;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyTVSeries implements Serializable
{
    private String title,description,id,firstAired;
    private ArrayList<String> episodes,actors;
    private byte[] image;
    private List<Episode> episodeList;
    private ArrayList<Season> seasons;

    public MyTVSeries(String title, String description, Bitmap poster, ArrayList<String> episodes,String id, String firstAired, ArrayList<String> actors,List<Episode> episodeList)
    {
        //TODO PARAMETRI PER EPISODI RIDONDANTI, DA FIXARE
        this.title = title;
        this.description = description;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        poster.compress(Bitmap.CompressFormat.PNG, 0, bos);
        image=bos.toByteArray();
        this.episodes = episodes;
        this.id=id;
        this.actors=actors;
        this.firstAired = firstAired;
        this.episodeList = episodeList;
        if(this.episodeList != null) {
            manageSeasons();
            //Log.v("Emil","Seasons size: "+seasons.size());
        }
        //else Log.v("Emil", "EpisodeList null");
    }

    //STILL TO IMPROVE. THIS CONSTRUCTOR IS CALLED BY DATABASE CLASS
    public MyTVSeries(String title, String description, ArrayList<String> episodes, String id, String firstAired, byte[] image, ArrayList<Season> seasons){
        this.title = title;
        this.description = description;
        this.image = image;
        this.episodes = episodes;
        this.id = id;
        this.firstAired = firstAired;
        //this.episodeList = episodeList;
        this.seasons = seasons;
        actors = new ArrayList<String>();
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

    public String getFirstAired() {return firstAired;}

    public ArrayList<String> getActors()
    {
        return actors;
    }

    public int getTotSeasons(){ return seasons.size(); }

    public Season getSeason(int seasonNumber){ return seasons.get(seasonNumber);}

    public ArrayList<Season> getSeasons(){ return seasons; }

    private void manageSeasons(){
        seasons = new ArrayList<Season>();
        //Log.v("Emil","Seasons size: "+seasons.size());
        for(int i=0, j=0; i<episodeList.size(); i++){
           Episode e = episodeList.get(i);
           if(e.getSeasonNumber() == j){
               if(seasons.size() <= j) {   //se non è ancora stata creata la stagione
                   seasons.add(new Season(e.getSeasonId(), j));
                   //Log.v("Emil","Seasons size: "+seasons.size());
                   //Log.v("Emil","SeasonID and number: "+e.getSeasonId()+ " "+e.getSeasonNumber());
               }
               seasons.get(j).addEpisode(e);
               //Log.v("Emil","Episode ID,number, name: "+e.getId()+ " "+e.getEpisodeNumber()+" "+e.getEpisodeName());
           }
           else {
               j++;
               i--;
           }
        }
    }
}
