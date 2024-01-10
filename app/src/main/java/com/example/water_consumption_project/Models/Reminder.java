package com.example.water_consumption_project.Models;

public class Reminder {
    private int id;
    private int idUser;
    private String hour;
    private boolean isMissing;

    public Reminder(int id, int idUser, String hour, boolean isMissing){
        this.id = id;
        this.idUser = idUser;
        this.hour = hour;
        this.isMissing = isMissing;
    }

    public int getId(){
        return id;
    }

    public boolean getIsMissing(){
        return isMissing;
    }

    public int getIdUser(){
        return idUser;
    }

    public String getHour(){
        return hour;
    }
}
