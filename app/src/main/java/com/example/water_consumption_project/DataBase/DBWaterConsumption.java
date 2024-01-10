package com.example.water_consumption_project.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DBWaterConsumption {
    private final static int VERSION_BDD = 1;
    protected SQLiteDatabase db;
    protected MySqlLiteDatabase mySqlLiteDatabase;

    public DBWaterConsumption(Context context){
        mySqlLiteDatabase = new MySqlLiteDatabase(context,"db_water_consumption",null, VERSION_BDD);
    }

    public void open(){
        db = mySqlLiteDatabase.getWritableDatabase();
    }
    public void close(){
        db.close();
    }
    public SQLiteDatabase getDb(){
        return db;
    }
}
