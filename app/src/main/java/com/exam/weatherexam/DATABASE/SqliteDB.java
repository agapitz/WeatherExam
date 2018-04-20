package com.exam.weatherexam.DATABASE;

/**
 * Created by Maverick on 3/12/2015.
 */


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "information";
    private Context context;
    private SQLiteDatabase sqlDatabase;

    public SqliteDB(Context context) {
        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        sqlDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        try {
            db.execSQL(Tables.weather);
        } catch (Exception x) {
            Log.d("sqlite error", x.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CLIENT");
        this.onCreate(db);
    }

    public void executeQuery(String query) {
        try {
            if (sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }
            sqlDatabase = this.getWritableDatabase();
            sqlDatabase.execSQL(query);
        } catch (Exception e) {
            System.out.println("DATABASE ERROR " + e);
        }
    }

    public Cursor SelectQuery(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            return cursor;
        } else {
            return null;
        }
    }
}