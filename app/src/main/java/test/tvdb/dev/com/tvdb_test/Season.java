package test.tvdb.dev.com.tvdb_test;

import android.util.Log;
import com.omertron.thetvdbapi.model.Episode;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Emil on 10/04/2015.
 */
public class Season implements Serializable{

    private String id;
    private int seasonNumber;
    private ArrayList<Episode> episodes;

    public Season(String id, int seasonNumber){
        this.id = id;
        this.seasonNumber = seasonNumber;
        episodes = new ArrayList<Episode>();
    }

    public Season(String id, int seasonNumber, ArrayList<Episode> episodes){
        this.id = id;
        this.seasonNumber = seasonNumber;
        this.episodes = episodes;
    }

    public boolean addEpisode(Episode e){
        if(episodes.add(e))
            return true;
        return false;
    }

    public String getID(){ return id; }

    public int getSeasonNumber(){ return seasonNumber; }

    public ArrayList<Episode> getEpisodesList(){ return episodes; }

    public int getTotEpisodes(){ return episodes.size(); }

    //minus one because episodes start from number one but in the arraylist they start from 0
    public Episode getEpisode(int episodeNumber){
        try {
            return episodes.get(episodeNumber - 1);
        } catch (IndexOutOfBoundsException ex){
            Log.v("Emil","episodes start from number one but in the arraylist they start from 0");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        return (this.id.equals(((Season)o).getID()));

    }
}
