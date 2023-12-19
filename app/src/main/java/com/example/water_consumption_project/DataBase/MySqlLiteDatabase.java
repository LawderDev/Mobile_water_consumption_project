package com.example.water_consumption_project.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqlLiteDatabase extends SQLiteOpenHelper {


    private final String CREATE_TABLE_USER =
            "CREATE TABLE " + "User" + " (" +
                    "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username" + " INTEGER, " +
                    "targetConsumption" + " TEXT);";

    private final static String CREATE_TABLE_REMINDER =
            "CREATE TABLE " + "Reminder" + " (" +
                    "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "idUser" + " INTEGER, " +
                    "hour" + " TEXT);";

    private final String CREATE_TABLE_CONSUMPTION =
            "CREATE TABLE " + "Consumption" + " (" +
                    "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "idUser" + " INTEGER, " +
                    "date" + " LONG, " +
                    "currentConsumption" + " INTEGER);";

    public MySqlLiteDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_REMINDER);
        db.execSQL(CREATE_TABLE_CONSUMPTION);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_REMINDER);
        db.execSQL(CREATE_TABLE_CONSUMPTION);
        onCreate(db);
    }
}
