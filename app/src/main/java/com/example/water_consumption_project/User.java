package com.example.water_consumption_project;

public class User {
    private int id;
    private String username;

    public User(int id, String username){
        this.id = id;
        this.username = username;
    }

    public int getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }
}
