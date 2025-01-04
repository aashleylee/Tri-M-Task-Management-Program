package com.example.second;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SelectedMembersHolder {
    private ArrayList<User> selectedMembers = new ArrayList<User>();
    private final static SelectedMembersHolder INSTANCE = new SelectedMembersHolder();
    private SelectedMembersHolder(){
    }
    public static SelectedMembersHolder getInstance(){
        return INSTANCE;
    }
    public void setSelectedMembers(ArrayList<User> sm){
        this.selectedMembers = sm;
    }
    public ArrayList<User> getSelectedMembers(){
        return this.selectedMembers;
    }
}
