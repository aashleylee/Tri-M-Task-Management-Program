package com.example.second;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ExecTaskInfoController implements Initializable {
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
    private Task task;
    public ExecTaskInfoController(Task task){
        this.task = task;
    }

    public void markTaskComplete(ActionEvent event){
        Connection connection = null;
        PreparedStatement psUpdate = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager",
                    "root", "java1234");
            //changing from deletion of task to updating status for progress chart
            psUpdate = connection.prepareStatement("UPDATE tasks SET status = 3 WHERE id = ?");
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
        Stage stage = (Stage) completeButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleLabel.setText(task.getTitle());
        descriptionLabel.setText(task.getDescription());
        categoryLabel.setText(task.getCategories());
        ArrayList<String> assignedMembersNames = new ArrayList<String>();
        for(User member : task.getAssignedMembers()){
            assignedMembersNames.add(member.getFullName());
        }
        assignedMembersLabel.setText(String.join(", ", assignedMembersNames));
        dueDateLabel.setText(task.getDueDate().toString());
    }
}
