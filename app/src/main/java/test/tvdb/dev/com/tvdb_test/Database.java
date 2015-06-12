package test.tvdb.dev.com.tvdb_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.omertron.thetvdbapi.model.Episode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emil on 04/04/2015.
 */
public class Database  extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "TVSeries.db";
    public static final int DATABASE_VERSON = 2;

    private static final String SERIES_TABLE = "SERIES";
    private static final String SQL_CREATE_SERIES = "CREATE TABLE " + SERIES_TABLE + " ( ID_SERIES TEXT NOT NULL UNIQUE, TITLE TEXT NOT NULL, "
            + "DESCRIPTION TEXT, RELEASE_DATE TEXT, LANGUAGE TEXT, POSTER BLOB )";
    private static final String SEASONS_TABLE = "SEASONS";
    private static final String SQL_CREATE_SEASONS = "CREATE TABLE " + SEASONS_TABLE + " ( ID_SEASONS TEXT NOT NULL UNIQUE, NUMBER INTEGER NOT NULL,"
            + "id_series TEXT NOT NULL )";
    private static final String EPISODES_TABLE = "EPISODES";
    private static final String SQL_CREATE_EPISODES = "CREATE TABLE " + EPISODES_TABLE + " (ID_EPISODES TEXT NOT NULL UNIQUE, TITLE TEXT NOT NULL, "
            + "OVERVIEW TEXT, NUMBER INTEGER NOT NULL, SEEN INTEGER, RELEASE_DATE TEXT, id_season TEXT NOT NULL)";
    private static final String ACTORS_TABLE = "ACTORS";
    private static final String SQL_CREATE_ACTORS = "CREATE TABLE " + ACTORS_TABLE + " ( NAME TEXT NOT NULL, id_series TEXT NOT NULL)";

    /*
	 * private static final String SQL_DELETE_SERIES = "DROP TABLE IF EXISTS " +
	 * SERIES_TABLE;
	 */

    //TODO If something being modified in here, modify also DATABASE_VERSION

    public Database(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSON); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SERIES);
        db.execSQL(SQL_CREATE_SEASONS);
        db.execSQL(SQL_CREATE_EPISODES);
        db.execSQL(SQL_CREATE_ACTORS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_SERIES);
        //onCreate(db);
    }

    //TODO
    public boolean storeSerie(MyTVSeries serie){
        return true;
    }

    public boolean storeSeries(ArrayList<MyTVSeries> series) throws ArrayIndexOutOfBoundsException{
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(SERIES_TABLE, null, null);
            db.delete(SEASONS_TABLE, null, null);
            db.delete(EPISODES_TABLE, null, null);
            db.delete(ACTORS_TABLE, null, null);
            for (MyTVSeries myserie : series) {
                ContentValues values = new ContentValues();
                values.put("ID_SERIES", myserie.getID());
                values.put("TITLE", myserie.getTitle());
                values.put("DESCRIPTION", myserie.getDescription());
                values.put("RELEASE_DATE", myserie.getFirstAired());
                values.put("LANGUAGE", "en"); //temporaly only english
                values.put("POSTER", myserie.getPoster());
                for (int i = myserie.getFirstSeasonNumber(); i < myserie.getTotSeasons(); i++) {
                    Season s = myserie.getSeason(i);
                    storeSeason(s,myserie.getID());
                    /*ContentValues values1 = new ContentValues();
                    values1.put("ID_SEASONS", s.getID());
                    values1.put("NUMBER", s.getSeasonNumber());
                    values1.put("id_series", myserie.getID()); */
                    for (int j = 1; j <= s.getTotEpisodes(); j++)
                        storeEpisode(s.getEpisode(j),s.getID());
                        /*{ContentValues values2 = new ContentValues();
                        Episode e = s.getEpisode(j);
                        values2.put("ID_EPISODES", e.getId());
                        values2.put("TITLE", e.getEpisodeName());
                        values2.put("OVERVIEW", e.getOverview());
                        values2.put("NUMBER", e.getEpisodeNumber());
                        values2.put("SEEN", 0);
                        values2.put("RELEASE_DATE", e.getFirstAired());
                        values2.put("id_season", s.getID());
                        db.insert(EPISODES_TABLE, null, values2); }*/
                    //db.insert(SEASONS_TABLE, null, values1);
                }
                ArrayList<String> actors = myserie.getActors();
                for(int k = 0; k < actors.size(); k++){
                    ContentValues values1 = new ContentValues();
                    values1.put("NAME", actors.get(k));
                    values1.put("id_series",myserie.getID());
                    db.insert(ACTORS_TABLE, null, values1);
                }
                db.insert(SERIES_TABLE, null, values);
            }
            db.close();
            return true;
        } catch(SQLiteException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //TODO
    public ArrayList<MyTVSeries> getSeries(){
        ArrayList<MyTVSeries> series = new ArrayList<MyTVSeries>();
        final String SELECT_SERIES = "SELECT ID_SERIES, TITLE, DESCRIPTION, RELEASE_DATE, LANGUAGE, POSTER"
                + " FROM "+SERIES_TABLE+" ORDER BY TITLE";
        try{
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(SELECT_SERIES, null);
            while(cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex("ID_SERIES"));
                String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                String release_date = cursor.getString(cursor.getColumnIndex("RELEASE_DATE"));
                String language = cursor.getString(cursor.getColumnIndex("LANGUAGE"));
                byte[] poster = cursor.getBlob(cursor.getColumnIndex("POSTER"));
                ArrayList<Season> seasons = new ArrayList<Season>();

                ArrayList<String> episodesTitle = new ArrayList<String>();

                final String SELECT_SEASONS = "SELECT ID_SEASONS, NUMBER FROM "+SEASONS_TABLE
                        + " WHERE id_series="+id+" ORDER BY NUMBER";
                Cursor cursor1 = db.rawQuery(SELECT_SEASONS, null);
                while (cursor1.moveToNext()){
                    String seasonID = cursor1.getString(cursor1.getColumnIndex("ID_SEASONS"));
                    int seasonNumber = cursor1.getInt(cursor1.getColumnIndex("NUMBER"));

                    ArrayList<Episode> episodes = new ArrayList<Episode>();
                    final String SELECT_EPISODES = "SELECT ID_EPISODES, TITLE, OVERVIEW, NUMBER, SEEN, RELEASE_DATE FROM "+EPISODES_TABLE
                            + " WHERE id_season="+seasonID+" ORDER BY NUMBER";
                    Cursor cursor2 = db.rawQuery(SELECT_EPISODES, null);
                    while(cursor2.moveToNext()){
                        String episodeID = cursor2.getString(cursor2.getColumnIndex("ID_EPISODES"));
                        String episodeTitle = cursor2.getString(cursor2.getColumnIndex("TITLE"));
                        String episodeOverview = cursor2.getString(cursor2.getColumnIndex("OVERVIEW"));
                        int episodeNumber = cursor2.getInt(cursor2.getColumnIndex("NUMBER"));
                        int seen = cursor2.getInt(cursor2.getColumnIndex("SEEN"));
                        boolean _seen;
                        if(seen==0)
                            _seen=false;
                        else _seen = true;
                        String episodeRelease_date = cursor2.getString(cursor2.getColumnIndex("RELEASE_DATE"));
                        episodes.add(new MyEpisode(episodeID, episodeTitle, episodeOverview, episodeNumber, _seen, episodeRelease_date));

                        episodesTitle.add(episodeTitle);

                    }
                    seasons.add(new Season(seasonID, seasonNumber, episodes));
                }
                ArrayList<String> actors = new ArrayList<>();
                final String SELECT_ACTORS = "SELECT NAME FROM "+ACTORS_TABLE+ " WHERE id_series="+id;
                cursor1 = db.rawQuery(SELECT_ACTORS, null);
                while (cursor1.moveToNext()){
                    actors.add(cursor1.getString(cursor1.getColumnIndex("NAME")));
                }
                series.add(new MyTVSeries(title, description, episodesTitle, id, release_date, poster, seasons, actors));
            }
            db.close();
            return series;
        } catch (SQLiteException e){
            e.printStackTrace();
            return null;
        }
    }

    private boolean storeEpisode(Episode e, String ID_SEASON){
        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("ID_EPISODES", e.getId());
            cv.put("TITLE", e.getEpisodeName());
            cv.put("OVERVIEW", e.getOverview());
            cv.put("NUMBER", e.getEpisodeNumber());
            cv.put("SEEN", 0);  //episode seen or not, 0 false and 1 true. FOR NOW IS SET TO 0
            cv.put("RELEASE_DATE", e.getFirstAired());
            cv.put("id_season", ID_SEASON);
            db.insert(EPISODES_TABLE, null, cv);
            return true;

        } catch (SQLiteException ex){
            ex.printStackTrace();
            return false;
        }
    }

    private boolean storeSeason(Season s, String ID_SERIE){
        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("ID_SEASONS", s.getID());
            cv.put("NUMBER", s.getSeasonNumber());
            cv.put("id_series", ID_SERIE);
            db.insert(SEASONS_TABLE, null, cv);
            return true;
        } catch (SQLiteException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateSeries(ArrayList<MyTVSeries> series){
        try {
            ArrayList<MyTVSeries> stored = getSeries();
            for (int i = 0; i < series.size(); i++) {
                MyTVSeries tmp = series.get(i);
                MyTVSeries tmp2 = stored.get(stored.indexOf(tmp));
                System.out.println("Updated : "+tmp.getLastSeason().getTotEpisodes()+" !Updated : "+tmp2.getLastSeason().getTotEpisodes());
                //Se l' ultima stagione presente coincide con quella aggiornata
                if (tmp.getLastSeasonNumber() == tmp2.getLastSeasonNumber()) {
                    //Se gli episodi dell' ultima stagione non coincidono con gli episodi aggiornati vengono salvati
                    if (tmp.getLastSeason().getTotEpisodes() != tmp2.getLastSeason().getTotEpisodes()) {
                        //Aggiungere episodi mancanti al database
                        List<Episode> missing = tmp.getLastSeason().getEpisodesList().subList(tmp2.getLastSeason().getTotEpisodes(), tmp.getLastSeason().getTotEpisodes());
                        for (Episode e : missing)
                            storeEpisode(e, tmp.getLastSeason().getID());
                    }
                //Se non coincidono invece
                } else {
                    //Salvo prima gli eventuali episodi mancanti dell' ultima stagione presente nel database in modo che la stagione sia completa
                    int x = tmp2.getLastSeasonNumber();
                    if (tmp2.getLastSeason().getTotEpisodes() != tmp.getSeason(x).getTotEpisodes()) {
                        List<Episode> missing = tmp.getSeason(x).getEpisodesList().subList(tmp2.getLastSeason().getTotEpisodes(), tmp.getSeason(x).getTotEpisodes());
                        for (Episode e : missing)
                            storeEpisode(e, tmp.getLastSeason().getID());
                    }
                    //Una volta controllato, aggiungo le stagioni mancanti e i loro episodi
                    for (int j = 1 + tmp2.getLastSeasonNumber(); j <= tmp.getLastSeasonNumber(); j++) {
                        Season s = tmp.getSeason(j);
                        storeSeason(s, tmp.getID());
                        for (int k = 1; k <= s.getTotEpisodes(); k++)
                            storeEpisode(s.getEpisode(k), s.getID());
                    }

                }
                //Per le stagioni speciali(Season 0)
                if(tmp2.getSeason(0)!=null && tmp.getSeason(0)!=null && tmp2.getSeason(0).getTotEpisodes()!=tmp.getSeason(0).getTotEpisodes()){
                    List<Episode> missing = tmp.getSeason(0).getEpisodesList().subList(tmp2.getSeason(0).getTotEpisodes(), tmp.getSeason(0).getTotEpisodes());
                    for(Episode e : missing)
                        storeEpisode(e, tmp.getSeason(0).getID());
                }
            }
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

    }

    public boolean deleteSerie(String id){
        ArrayList<String> seasonsID = new ArrayList<String>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "SELECT ID_SEASONS FROM " + SEASONS_TABLE + " WHERE id_series=" + id;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()){
                seasonsID.add(cursor.getString(cursor.getColumnIndex("ID_SEASONS")));
            }
            for(int i=0; i<seasonsID.size(); i++){
                if(db.delete(EPISODES_TABLE, "id_season=?",new String[] {seasonsID.get(i)}) == 0)
                    return false;
            }
            db.delete(ACTORS_TABLE, "id_series=?",new String[] {id});
            db.delete(SEASONS_TABLE,"id_series=?",new String[] {id});
            db.delete(SERIES_TABLE, "ID_SERIES=?",new String[] {id});
            db.close();
        } catch (SQLiteException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
