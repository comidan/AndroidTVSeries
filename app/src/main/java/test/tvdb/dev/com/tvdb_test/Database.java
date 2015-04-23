package test.tvdb.dev.com.tvdb_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.omertron.thetvdbapi.model.Episode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Emil on 04/04/2015.
 */
public class Database  extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "TVSeries.db";
    public static final int DATABASE_VERSON = 1;

    private static final String SERIES_TABLE = "SERIES";
    private static final String SQL_CREATE_SERIES = "CREATE TABLE " + SERIES_TABLE + " ( ID_SERIES TEXT NOT NULL UNIQUE, TITLE TEXT NOT NULL, "
            + "DESCRIPTION TEXT, RELEASE_DATE TEXT, LANGUAGE TEXT, POSTER BLOB )";
    private static final String SEASONS_TABLE = "SEASONS";
    private static final String SQL_CREATE_SEASONS = "CREATE TABLE " + SEASONS_TABLE + " ( ID_SEASONS TEXT NOT NULL UNIQUE, NUMBER INTEGER NOT NULL,"
            + "id_series TEXT NOT NULL )";
    private static final String EPISODES_TABLE = "EPISODES";
    private static final String SQL_CREATE_EPISODES = "CREATE TABLE " + EPISODES_TABLE + " (ID_EPISODES TEXT NOT NULL UNIQUE, TITLE TEXT NOT NULL, "
            + "OVERVIEW TEXT, NUMBER INTEGER NOT NULL, SEEN INTEGER, RELEASE_DATE TEXT, id_season TEXT NOT NULL)";

    /*
	 * private static final String SQL_DELETE_SERIES = "DROP TABLE IF EXISTS " +
	 * SERIES_TABLE;
	 */


    //TODO add poster

    public Database(Context context) { super(context, DATABASE_NAME, null ,DATABASE_VERSON); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SERIES);
        db.execSQL(SQL_CREATE_SEASONS);
        db.execSQL(SQL_CREATE_EPISODES);
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

    public boolean storeSeries(ArrayList<MyTVSeries> series){
        Log.v("Emil", "MyTVSeries arraylist size: " + series.size());
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(SERIES_TABLE, null, null);
            db.delete(SEASONS_TABLE, null, null);
            db.delete(EPISODES_TABLE, null, null);
            for (MyTVSeries myserie : series) {
                ContentValues values = new ContentValues();
                values.put("ID_SERIES", myserie.getID());
                values.put("TITLE", myserie.getTitle());
                values.put("DESCRIPTION", myserie.getDescription());
                values.put("RELEASE_DATE", myserie.getFirstAired());
                values.put("LANGUAGE", "en"); //temporaly only english
                values.put("POSTER", myserie.getPoster());
                for (int i = 0; i < myserie.getTotSeasons(); i++) {
                    ContentValues values1 = new ContentValues();
                    Season s = myserie.getSeason(i);
                    values1.put("ID_SEASONS", s.getID());
                    values1.put("NUMBER", s.getSeasonNumber());
                    values1.put("id_series", myserie.getID());
                    for (int j = 1; j <= s.getTotEpisodes(); j++) {
                        ContentValues values2 = new ContentValues();
                        Episode e = s.getEpisode(j);
                        values2.put("ID_EPISODES", e.getId());
                        values2.put("TITLE", e.getEpisodeName());
                        values2.put("OVERVIEW", e.getOverview());
                        values2.put("NUMBER", e.getEpisodeNumber());
                        values2.put("SEEN", 0); //episode seen or not, 0 false and 1 true. FOR NOW IS SET TO 0
                        values2.put("RELEASE_DATE", e.getFirstAired());
                        values2.put("id_season", s.getID());
                        db.insert(EPISODES_TABLE, null, values2);
                    }
                    db.insert(SEASONS_TABLE, null, values1);
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
        Log.v("Emil","Reading TVseries from database");
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
                series.add(new MyTVSeries(title, description, episodesTitle, id, release_date, poster, seasons));
            }
            db.close();
            Log.v("Emil", "MyTVSeries arraylist size: " + series.size());
            return series;
        } catch (SQLiteException e){
            e.printStackTrace();
            return null;
        }
    }

    //TODO
    public boolean deleteSerie(MyTVSeries serie){
        return true;
    }
}
