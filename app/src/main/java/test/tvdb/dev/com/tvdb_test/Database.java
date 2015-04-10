package test.tvdb.dev.com.tvdb_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Emil on 04/04/2015.
 */
public class Database  extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "TVSeries.db";
    public static final int DATABASE_VERSON = 1;

    private static final String SERIES_TABLE = "SERIES";
    private static final String SQL_CREATE_SERIES = "CREATE TABLE " + SERIES_TABLE + " ( ID_SERIES TEXT NOT NULL UNIQUE, TITLE TEXT NOT NULL, "
            + "DESCRIPTION TEXT, RELEASE_DATE INTEGER, LANGUAGE TEXT )";
    private static final String SEASONS_TABLE = "SEASONS";
    private static final String SQL_CREATE_SEASONS = "CREATE TABLE" + SEASONS_TABLE + " ( ID_SEASONS INTEGER PRIMARY KEY, NUMBER INTEGER NOT NULL,"
            + "id_series INTEGER NOT NULL )";
    private static final String EPISODES_TABLE = "EPISODES";
    private static final String SQL_CREATE_EPISODES = "CREATE TABLE" + EPISODES_TABLE + " (ID_EPISODES INTEGER PRIMARY KEY, TITLE TEXT NOT NULL, "
            + "OVERVIEW TEXT, SEEN INTEGER, RELEASE_DATE INTEGER, id_season INTEGER NOT NULL)";

    /*
	 * private static final String SQL_DELETE_SERIES = "DROP TABLE IF EXISTS " +
	 * SERIES_TABLE;
	 */


    //TODO add poster and episode image

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
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(SERIES_TABLE, null, null);
            db.delete(SEASONS_TABLE, null, null);
            db.delete(EPISODES_TABLE, null, null);
            for (MyTVSeries myserie : series) {
                ContentValues values = new ContentValues();
                values.put("ID_SERIES", myserie.getID()); //ID per ora intero, anche se in MyTVSeries è stringa, da rivedere
                values.put("TITLE", myserie.getTitle());
                values.put("DESCRIPTION", myserie.getDescription());
                //values.put("RELEASE_DATE", myserie.getReleaseDate()) metodo da aggiungere
                //values.put("LANGUAGE", myserie.getLanguage()); metodo da aggiungere
                /*for (int i = 1; i <= myserie.getLastSeasonNumber(); i++) {
                    ContentValues values1 = new ContentValues();
                    values1.put("ID_SEASONS", i);
                    values1.put("NUMBER", i); //rindondante, da rivedere
                    values1.put("id_series", Integer.parseInt(myserie.getID()));
                    for (int j = 1; i <= myserie.getLastEpisodeNumberForSeason(i); j++) {
                        ContentValues values2 = new ContentValues();
                        values2.put("ID_EPISODES", j);
                        //values2.put("TITLE", );   //episode title
                        //values2.put("OVERVIEW", ); //episode overview
                        //values2.put("RELEASE_DATE", ); //episode release date
                        //values2.put("SEEN", ); //episode seen or not, 0 false and 1 true
                        values2.put("id_season", i);
                        db.insert(EPISODES_TABLE, null, values2);
                    }
                    db.insert(SEASONS_TABLE, null, values1);
                } */
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
        return null;
    }

    //TODO
    public boolean deleteSerie(MyTVSeries serie){
        return true;
    }
}
