package com.example.second;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;


public class AddNewTaskController {
    @FXML
    private TextField title;
    @FXML
    private TextArea description;
    @FXML
    private CheckBox stageManagement;
    @FXML
    private CheckBox PR;
    @FXML
    private CheckBox communications;
    @FXML
    private DatePicker dueDate;
    private ArrayList<User> assignedMembers;
    @FXML
    private TextField assignedMembersTextField;
    private User user;
    public AddNewTaskController(User user){
        this.user = user;
    }
    public void addNewTask(ActionEvent event){
        Connection connection = null;
        PreparedStatement psInsertTask = null;
        PreparedStatement psInsertCategory = null;
        int taskId = 0;
        PreparedStatement psInsertAssignedMembers = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager",
                    "root", "java1234");
            psInsertTask = connection.prepareStatement("INSERT INTO tasks (title, description, " +
                    "due_date, status, revisions, feedback) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            psInsertTask.setString(1, title.getText());
            psInsertTask.setString(2, description.getText());
            psInsertTask.setDate(3, Date.valueOf(dueDate.getValue()));
            psInsertTask.setInt(4, 1); //active = 1, review status becomes 2
            psInsertTask.setInt(5, 0); //revision is first 0
            psInsertTask.setString(6, ""); //empty default
            int affectedRows = psInsertTask.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating task failed");
            }

            try (ResultSet generatedKeys = psInsertTask.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    taskId = generatedKeys.getInt(1);
                }
            }
            if(stageManagement.isSelected()){
                psInsertCategory = connection.prepareStatement("INSERT INTO tasks_categories " +
                        "(task_id, category_id) VALUES (?, ?)");
                psInsertCategory.setInt(1, taskId);
                psInsertCategory.setInt(2, 1);
                psInsertCategory.executeUpdate();
            }
            if(PR.isSelected()){
                psInsertCategory = connection.prepareStatement("INSERT INTO tasks_categories " +
                        "(task_id, category_id) VALUES (?, ?)");
                psInsertCategory.setInt(1, taskId);
                psInsertCategory.setInt(2, 2);
                psInsertCategory.executeUpdate();
            }
            if(communications.isSelected()){
                psInsertCategory = connection.prepareStatement("INSERT INTO tasks_categories " +
                        "(task_id, category_id) VALUES (?, ?)");
                psInsertCategory.setInt(1, taskId);
                psInsertCategory.setInt(2, 3);
                psInsertCategory.executeUpdate();
            }
            for(User member : assignedMembers){
                psInsertAssignedMembers = connection.prepareStatement("INSERT INTO tasks_users " +
                        "(task_id, user_id) VALUES (?, ?)");
                psInsertAssignedMembers.setInt(1, taskId);
                psInsertAssignedMembers.setInt(2, member.getId());
                psInsertAssignedMembers.executeUpdate();
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("exec-council-home.fxml"));
                ExecCouncilHomeController execCouncilHomeController = new ExecCouncilHomeController(user);
                loader.setController(execCouncilHomeController);

                AnchorPane anchorPane = loader.load();
                Scene scene = new Scene(anchorPane);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            if (psInsertTask != null) {
                try {
                    psInsertTask.close();
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
    }
    public void openSelectMembersWindow(ActionEvent event){
        try {

            ArrayList<Integer> selectCategories = new ArrayList<>();
            if(stageManagement.isSelected()){
                selectCategories.add(1); //1 is number for SM
            }
            if(PR.isSelected()){
                selectCategories.add(2);
            }
            if(communications.isSelected()){
                selectCategories.add(3);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("select-members.fxml"));
            SelectMembersController selectMembersController = new SelectMembersController(selectCategories);
            loader.setController(selectMembersController);

            Stage stage = new Stage();
            AnchorPane anchorPane = loader.load();
            stage.setScene(new Scene(anchorPane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            SelectedMembersHolder holder = SelectedMembersHolder.getInstance();
            assignedMembers = holder.getSelectedMembers();
            ArrayList<String> assignedMembersNames = new ArrayList<>();

            for (User member : assignedMembers) {
                assignedMembersNames.add(member.getFullName());
            }

            assignedMembersTextField.setText(String.join(", ", assignedMembersNames));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void openExecCouncilHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("exec-council-home.fxml"));
            ExecCouncilHomeController execCouncilHomeController = new ExecCouncilHomeController(user);
            loader.setController(execCouncilHomeController);

            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
