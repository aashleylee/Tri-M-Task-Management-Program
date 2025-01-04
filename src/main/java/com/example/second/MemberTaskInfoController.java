package com.example.second;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MemberTaskInfoController implements Initializable {
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label dueDateLabel;
    @FXML
    private Button completeButton;
    @FXML
    private Label assignedMembersLabel;
    @FXML
    private Button reviewButton;
    @FXML
    private Label revisionNumberLabel;
    @FXML
    private Label feedbackLabel;
    private Task task;

    public MemberTaskInfoController(Task task){
        this.task = task;
    }
    public void requestReview(ActionEvent event){
        Connection connection = null;
        PreparedStatement psUpdate = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager",
                    "root", "java1234");
            //update tasks table and set status to 2 where id is the task
            psUpdate = connection.prepareStatement("UPDATE tasks SET status = 2 WHERE id = ?");
            psUpdate.setInt(1, task.getId());
            psUpdate.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            if (psUpdate != null) {
                try {
                    psUpdate.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        Stage stage = (Stage) reviewButton.getScene().getWindow();
        stage.close();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(task.getStatus() ==2){ //if the status of the current task is 2, meaning 'under review'
            reviewButton.setVisible(false); //then hide the 'request for review' button
        }
        //putting in all the description texts for the task
        titleLabel.setText(task.getTitle());
        descriptionLabel.setText(task.getDescription());
        categoryLabel.setText(task.getCategories());
        ArrayList<String> assignedMembersNames = new ArrayList<String>();
        for(User member : task.getAssignedMembers()){
            assignedMembersNames.add(member.getFullName());
        }
        assignedMembersLabel.setText(String.join(", ", assignedMembersNames));
        dueDateLabel.setText(task.getDueDate().toString());
        revisionNumberLabel.setText(String.valueOf(task.getRevisions())); //converts int to string
        feedbackLabel.setText(task.getFeedback().toString());
    }
}
