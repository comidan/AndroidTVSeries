package test.tvdb.dev.com.tvdb_test;

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

    public boolean addEpisode(Episode e){
        if(episodes.add(e))
            //TODO Ordinare dopo inserimento per episodenumber, non serve se già ordinati, informarsi
            return true;
        return false;
    }

    public String getID(){ return id; }

    public int getSeasonNumber(){ return seasonNumber; }

    public ArrayList<Episode> getEpisodesList(){ return episodes; }

    public int getTotEpisodes(){ return episodes.size(); }
}
