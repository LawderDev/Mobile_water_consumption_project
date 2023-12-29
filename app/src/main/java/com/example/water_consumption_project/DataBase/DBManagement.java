package com.example.water_consumption_project.DataBase;

import android.content.Context;

import com.example.water_consumption_project.Controllers.ConsumptionController;
import com.example.water_consumption_project.Controllers.ReminderController;
import com.example.water_consumption_project.Controllers.UserController;
import com.example.water_consumption_project.Models.User;

public class DBManagement {

    private static DBManagement _instance = null;
    private final UserController userController;
    private final ReminderController reminderController;
    private final ConsumptionController consumptionController;

    private User user;

    protected DBManagement(Context context){
        this.userController = new UserController(context);
        this.reminderController = new ReminderController(context);
        this.consumptionController = new ConsumptionController(context);
        this.user = getFirstUser();
    }

    public User getFirstUser(){
        userController.open();
        User retrievingUser = null;
        if(this.user == null) {
            userController.insertUser("User", 2000);
        }

        retrievingUser = userController.getFirstUser();

        userController.close();
        return retrievingUser;
    }

    public void updateUser(){
        this.user = getFirstUser();
    }

    public static DBManagement getInstance(Context context){
        if(_instance == null){
            _instance = new DBManagement(context);
        }
        return _instance;
    }

    public User getUser() {
        return user;
    }

    public UserController getUserController() {
        return userController;
    }

    public ReminderController getReminderController() {
        return reminderController;
    }

    public ConsumptionController getConsumptionController() {
        return consumptionController;
    }
}
