package com.example.water_consumption_project.Models;

import java.util.Date;

public class Reminder {
    private int id;
    private int idUser;
    private int hour;

    public Reminder(int id, int idUser, int hour){
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

    public int getHour(){
        return hour;
    }
}
