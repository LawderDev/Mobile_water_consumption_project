package com.example.water_consumption_project.Models;

import java.util.Date;

public class Consumption {
    private int id;
    private int idUser;
    private long date;
    private int currentConsumption;

    public Consumption(int id, int idUser, long date, int currentConsumption){
        this.id = id;
        this.idUser = idUser;
        this.date = date;
        this.currentConsumption = currentConsumption;
    }

    public int getCurrentConsumption(){
        return this.currentConsumption;
    }

}
