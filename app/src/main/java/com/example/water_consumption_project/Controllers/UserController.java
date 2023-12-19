package com.example.water_consumption_project.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.water_consumption_project.DataBase.DBWaterConsumption;
import com.example.water_consumption_project.Models.User;

public class UserController extends DBWaterConsumption {

    public UserController(Context context){
        super(context);
    }

    public long insertUser(String username, int targetConsumption){
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("targetConsumption", targetConsumption);
        return db.insert("User", null, values);
    }

    public User getFirstUser(){
        String[] attributes = new String[]{"id", "username", "targetConsumption"};

        Cursor cursor = db.query(
                "User",
                attributes,
                null,
                null,
                null,
                null,
                null);

        return cursorToUser(cursor);
    }

    public User cursorToUser(Cursor cursor){
        if (cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        int idCursor = cursor.getColumnIndex("id");
        int usernameCursor = cursor.getColumnIndex("username");
        int targetConsumptionCursor = cursor.getColumnIndex("targetConsumption");

        if(usernameCursor < 0 && targetConsumptionCursor < 0) return null;

        int id = cursor.getInt(idCursor);
        String username = cursor.getString(usernameCursor);
        int targetConsumption = cursor.getInt(targetConsumptionCursor);

        User user = new User(id, username, targetConsumption);
        cursor.close();
        return user;
    }
}
