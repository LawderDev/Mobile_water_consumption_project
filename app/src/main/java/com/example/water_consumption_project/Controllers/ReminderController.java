package com.example.water_consumption_project.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.water_consumption_project.DataBase.DBWaterConsumption;
import com.example.water_consumption_project.Models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderController extends DBWaterConsumption {

    public ReminderController(Context context) {
        super(context);
    }

    public long insertReminder(int idUser, String hour) {
        ContentValues values = new ContentValues();
        values.put("idUser", idUser);
        values.put("hour", hour);
        return db.insert("Reminder", null, values);
    }

    public List<Reminder> getRemindersByIdUser(int idUser) {
        String[] attributes = new String[]{"id", "idUser", "hour"};
        String[] whereTab = new String[]{String.valueOf(idUser)};

        Cursor cursor = db.query(
                "Reminder",
                attributes,
                "idUser = ?",
                whereTab,
                null,
                null,
                null);

        return cursorToReminders(cursor);
    }

    private List<Reminder> cursorToReminders(Cursor cursor) {
        List<Reminder> reminders = new ArrayList<>();

        if (cursor.getCount() == 0 || !cursor.moveToFirst())
            return reminders;

        do {
            int idCursor = cursor.getColumnIndex("id");
            int idUserCursor = cursor.getColumnIndex("idUser");
            int hourCursor = cursor.getColumnIndex("hour");

            if (idCursor < 0 && idUserCursor < 0 && hourCursor < 0) return null;

            int id = cursor.getInt(idCursor);
            int idUser = cursor.getInt(idUserCursor);
            String hour = cursor.getString(hourCursor);

            Reminder reminder = new Reminder(id, idUser, hour);

            reminders.add(reminder);
        } while (cursor.moveToNext());

        cursor.close();

        return reminders;
    }
    public void removeReminderById(int id){
        String[] whereTab = new String[]{String.valueOf(id)};
        db.delete("Reminder","id = ?", whereTab);
    }
}
