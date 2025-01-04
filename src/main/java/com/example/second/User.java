package com.example.second;

import javafx.scene.control.CheckBox;

public class User {
    private int id;
    private String fullName;
    private String username;
    private String password;
    private int accountType;
    private int category;
    private CheckBox select;
    public User(int id, String fullName, String username, String password, int accountType, int category){
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.accountType = accountType;
        this.category = category;
        this.select = new CheckBox();
    }
    public String getFullName(){
        return fullName;
    }
    public String getCategory(){
        if(category == 1){
            return "Stage Management";
        }
        else if(category == 2){
            return "PR";
        }
        else if(category == 3){
            return "Communications";
        }
        else if(category == 4){
            return "President";
        }
        else if(category == 5){
            return "Vice President";
        }
        else if(category == 6){
            return "Stage Management Exec";
        }
        else if(category == 7){
            return "PR Exec";
        }
        else if(category == 8){
            return "Communications Exec";
        }
        return "";
    }
    public CheckBox getSelect(){
        return select;
    }
    public void setSelect(CheckBox select){
        this.select = select;
    }
    public int getId(){
        return id;
    }
}

