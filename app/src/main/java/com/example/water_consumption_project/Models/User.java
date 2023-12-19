package com.example.water_consumption_project.Models;

public class User {
    private int id;
    private String username;
    private int targetConsumption;

    public User(int id, String username, int targetConsumption){
        this.id = id;
        this.username = username;
        this.targetConsumption = targetConsumption;
    }

    public int getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public int getTargetConsumption(){
        return targetConsumption;
    }
}
