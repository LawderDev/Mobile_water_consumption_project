package com.example.water_consumption_project.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.water_consumption_project.DataBase.DBWaterConsumption;
import com.example.water_consumption_project.Models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderController extends DBWaterConsumption {

    public ReminderController(Context context) {
        super(context);
    }

    public long insertReminder(int idUser, String hour, boolean isMissing) {
        ContentValues values = new ContentValues();
        values.put("idUser", idUser);
        values.put("hour", hour);
        values.put("isMissing", String.valueOf(isMissing));
        return db.insert("Reminder", null, values);
    }

    public List<Reminder> getRemindersByIdUser(int idUser) {
        String[] attributes = new String[]{"id", "idUser", "hour", "isMissing"};
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

    public int updateIsMissingById(int reminderId, boolean isMissing) {
        ContentValues values = new ContentValues();
        values.put("isMissing", String.valueOf(isMissing));

        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(reminderId)};

        return db.update("Reminder", values, whereClause, whereArgs);
    }

    private List<Reminder> cursorToReminders(Cursor cursor) {
        List<Reminder> reminders = new ArrayList<>();

        if (cursor.getCount() == 0 || !cursor.moveToFirst())
            return reminders;

        do {
            int idCursor = cursor.getColumnIndex("id");
            int idUserCursor = cursor.getColumnIndex("idUser");
            int hourCursor = cursor.getColumnIndex("hour");
            int isMissingCursor = cursor.getColumnIndex("isMissing");

            if (idCursor < 0 && idUserCursor < 0 && hourCursor < 0) return null;

            int id = cursor.getInt(idCursor);
            int idUser = cursor.getInt(idUserCursor);
            String hour = cursor.getString(hourCursor);
            boolean isMissing = Boolean.parseBoolean(cursor.getString(isMissingCursor));

            Reminder reminder = new Reminder(id, idUser, hour, isMissing);

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
