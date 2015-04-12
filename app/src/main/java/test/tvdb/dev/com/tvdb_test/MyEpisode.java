package test.tvdb.dev.com.tvdb_test;

import com.omertron.thetvdbapi.model.Episode;

/**
 * Created by Emil on 12/04/2015.
 */
public class MyEpisode extends Episode{

    String id, title, overview, release_date;
    int number;
    boolean seen;

    public MyEpisode(String id, String title, String overview, int number, boolean seen, String Release_date){
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.number = number;
        this.seen = seen;
        this.release_date = release_date;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEpisodeName() {
        return title;
    }

    @Override
    public String getOverview() {
        return overview;
    }

    @Override
    public int getEpisodeNumber() {
        return number;
    }

    public boolean isSeen(){
        return seen;
    }

    @Override
    public String getFirstAired() {
        return release_date;
    }
}
