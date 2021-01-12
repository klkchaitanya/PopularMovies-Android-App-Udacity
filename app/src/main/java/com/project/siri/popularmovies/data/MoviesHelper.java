package com.project.siri.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesHelper extends SQLiteOpenHelper {

    static String DATABASE_NAME = "favourite_movies.db";
    static int DATABASE_VERSION = 1;

    public MoviesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_MOVIES + "(" + MoviesContract.MovieEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_MOVIEID + " TEXT NOT NULL,"+
                MoviesContract.MovieEntry.COLUMN_TITLE +" TEXT NOT NULL,"+
                MoviesContract.MovieEntry.COLUMN_SYNOPSIS +" TEXT NOT NULL,"+
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE +" TEXT NOT NULL,"+
                MoviesContract.MovieEntry.COLUMN_RATING +" TEXT NOT NULL,"+
                MoviesContract.MovieEntry.COLUMN_IMG_URL+ " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_MOVIES);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                MoviesContract.MovieEntry.TABLE_MOVIES + "'");

        // re-create database
        onCreate(db);
    }
}
