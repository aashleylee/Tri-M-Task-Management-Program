package com.example.second;

import java.time.LocalDate;
import java.util.ArrayList;

public class Task {
    private int id;
    private String title;
    private String description;
    private ArrayList<Integer> categories;
    private LocalDate dueDate;
    private ArrayList<User> assignedMembers;
    private int status;
    private int revisions;
    private String feedback;
    public Task(int id, String title, String description, ArrayList<Integer> categories, LocalDate dueDate,
                ArrayList<User> assignedMembers, int status, int revisions, String feedback){
        this.id = id;
        this.title = title;
        this.description = description;
        this.categories = categories;
        this.dueDate = dueDate;
        this.assignedMembers = assignedMembers;
        this.status = status;
        this.revisions = revisions;
        this.feedback = feedback;
    }
    public String toString(){
        return title;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getCategories(){
        ArrayList<String> categoryNames = new ArrayList<String>();
        for(int category : categories) {
            if (category == 1) {
                categoryNames.add("Stage Management");
            } else if (category == 2) {
                categoryNames.add("PR");
            } else if (category == 3) {
                categoryNames.add("Communications");
            }
        }
        return String.join(", ", categoryNames);
    }
    public LocalDate getDueDate(){
        return dueDate;
    }
    public int getId(){
        return id;
    }
    public int getRevisions(){
        return revisions;
    }
    public String getFeedback(){
        return feedback;
    }
    public ArrayList<User> getAssignedMembers(){
        return assignedMembers;
    }
    public int getStatus(){
        return status;
    }
}
