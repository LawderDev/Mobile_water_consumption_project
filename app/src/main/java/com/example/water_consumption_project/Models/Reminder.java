package com.example.water_consumption_project.Models;

import java.util.Date;

public class Reminder {
    private int id;
    private int idUser;
    private String hour;

    public Reminder(int id, int idUser, String hour){
        this.id = id;
        this.idUser = idUser;
        this.hour = hour;
    }

    public int getId(){
        return id;
    }

    public int getIdUser(){
        return idUser;
    }

    public String getHour(){
        return hour;
    }
}
