package com.example.second;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
public class MemberHomeController implements Initializable {
    private User user;
    @FXML
    private Label fullNameLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private ListView<Task> reviewTaskListView;
    public MemberHomeController(User user){
        this.user = user;
    }

    public void openChooseLoginScreen(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("choose-user.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //assigns the label to name and category to later show "Welcome (name) \n (category)"
        fullNameLabel.setText(user.getFullName());
        categoryLabel.setText(user.getCategory());
        queryTasksList(); //calling method to query tasks, task category, task status
        taskListView.setOnMouseClicked(new EventHandler<MouseEvent>() { //new event in normal task list view
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) { //if double-clicked
                    Task currentTask = taskListView.getSelectionModel().getSelectedItem(); //create a task object for current
                    try {
                        //load details of a task (only components visible to a member)
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("member-task-info.fxml"));
                        MemberTaskInfoController memberTaskInfoController = new MemberTaskInfoController(currentTask);
                        loader.setController(memberTaskInfoController);

                        AnchorPane anchorPane = loader.load();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(anchorPane));
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.showAndWait();
                        queryTasksList(); //call again because status changed, so it moves to correct column
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        reviewTaskListView.setOnMouseClicked(new EventHandler<MouseEvent>() { //new event in requested review task list view
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) { //double-clicked
                    //new event in review task list view
                    Task currentTask = reviewTaskListView.getSelectionModel().getSelectedItem();
                    try {
                        //load details of a task (only components visible to a member)
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("member-task-info.fxml"));
                        MemberTaskInfoController memberTaskInfoController = new MemberTaskInfoController(currentTask);
                        loader.setController(memberTaskInfoController);

                        AnchorPane anchorPane = loader.load();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(anchorPane));
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.showAndWait();
                        //don't need to call queryTaskList again
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void queryTasksList(){
        //this method queries tasks
        //clear both list views
        taskListView.getItems().clear();
        reviewTaskListView.getItems().clear();

        Connection connection = null;
        PreparedStatement psSelectCurrentUserTaskIds = null;
        PreparedStatement psSelectCurrentUserTasks = null;
        PreparedStatement psSelectCategories = null;
        PreparedStatement psSelectAssignedMembers = null;
        PreparedStatement psSelectUser = null;
        ResultSet taskIdResultSet = null;
        ResultSet taskResultSet = null;
        ResultSet categoriesResultSet = null;
        ResultSet assignedMembersResultSet = null;
        ResultSet userResultSet = null;
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Integer> categories = new ArrayList<>();
        ArrayList<User> assignedMembers = new ArrayList<>();

        //looking for tasks assigned to current user
        try { //looking for current user in database
            //get connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager",
                    "root", "java1234");
            //queries the tasks_users table to find the current user in database with user_id
            //question mark is parameter that is later replaced (Source 1)
            psSelectCurrentUserTaskIds = connection.prepareStatement("SELECT * FROM tasks_users WHERE user_id = ?");

            //gets all task_id assigned to current user and assigns to psSelectCurrentUserTaskIds
            psSelectCurrentUserTaskIds.setInt(1, user.getId());
            //executes query
            taskIdResultSet = psSelectCurrentUserTaskIds.executeQuery();

            //while the query above executes
            while (taskIdResultSet.next()) { //looking for tasks assigned to current user's id
                //queries the tasks table with task id
                psSelectCurrentUserTasks = connection.prepareStatement("SELECT * FROM tasks WHERE id = ?");
                /*
                uses the taskIdResultSet variable to get all task_ids assigned to current user and assigns
                them to psSelectCurrentUserTasks
                 */
                psSelectCurrentUserTasks.setInt(1, taskIdResultSet.getInt("task_id"));
                //executes query
                taskResultSet = psSelectCurrentUserTasks.executeQuery();

                //while the query above executes
                while (taskResultSet.next()) {
                    //taskId variable is assigned to the task_id of current user's tasks
                    int taskId = taskResultSet.getInt("id");
                    //queries the tasks_categories table with task_id
                    psSelectCategories = connection.prepareStatement("SELECT * FROM tasks_categories WHERE task_id = ?");
                    //sets psSelectCategories variable to categories of each task_id
                    psSelectCategories.setInt(1, taskId);
                    //executes query
                    categoriesResultSet = psSelectCategories.executeQuery();

                    //while the query above executes
                    while (categoriesResultSet.next()) {
                        //category ids of each task is added to categories ArrayList<Integer>
                        categories.add(categoriesResultSet.getInt("category_id"));
                    }
                    //queries tasks_users table with task_ids
                    psSelectAssignedMembers = connection.prepareStatement("SELECT * FROM tasks_users WHERE task_id = ?");
                    //psSelectAssignedMembers variable set to users with given taskId
                    psSelectAssignedMembers.setInt(1, taskId);
                    assignedMembersResultSet = psSelectAssignedMembers.executeQuery();

                    //while the query above executes
                    while (assignedMembersResultSet.next()) {
                        //query users table with task id
                        psSelectUser = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
                        //get assigned members from a task and assign to selected users
                        psSelectUser.setInt(1, assignedMembersResultSet.getInt("user_id"));
                        userResultSet = psSelectUser.executeQuery();

                        //while the query above executes
                        while (userResultSet.next()) {
                            //create a new user and add to assignedMembers ArrayList<User> with the information from current user
                            assignedMembers.add(new User(userResultSet.getInt("id"),
                                    userResultSet.getString("full_name"),
                                    userResultSet.getString("username"),
                                    userResultSet.getString("password"),
                                    userResultSet.getInt("account_type"),
                                    userResultSet.getInt("category")));
                        }
                    }
                    //create a new task and add to tasks ArrayList<Task>
                    tasks.add(new Task(taskResultSet.getInt("id"), taskResultSet.getString("title"),
                            taskResultSet.getString("description"), categories,
                            taskResultSet.getDate("due_date").toLocalDate(), assignedMembers,
                            taskResultSet.getInt("status"), taskResultSet.getInt("revisions"),
                            taskResultSet.getString("feedback")));
                    assignedMembers = new ArrayList<>();
                    categories = new ArrayList<>();
                }
            }
            //adding tasks to list view, add based on status
            for (Task task : tasks) {
                if(task.getStatus() ==1 ){
                    // if status is 1 : tasks on-going or to-do then show up in 'Not completed'
                    taskListView.getItems().add(task);
                }
                else if(task.getStatus() == 2){
                    // if status is 2 : finished + request for review then show up in 'Requested for Review'
                    reviewTaskListView.getItems().add(task);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (taskIdResultSet != null) {
                try {
                    taskIdResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (taskResultSet != null) {
                try {
                    taskResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (categoriesResultSet != null) {
                try {
                    categoriesResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (assignedMembersResultSet != null) {
                try {
                    assignedMembersResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (userResultSet != null) {
                try {
                    userResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psSelectUser != null) {
                try {
                    psSelectUser.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psSelectCategories != null) {
                try {
                    psSelectCategories.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psSelectAssignedMembers != null) {
                try {
                    psSelectAssignedMembers.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psSelectCurrentUserTasks != null) {
                try {
                    psSelectCurrentUserTasks.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psSelectCurrentUserTaskIds != null) {
                try {
                    psSelectCurrentUserTaskIds.close();
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
}
