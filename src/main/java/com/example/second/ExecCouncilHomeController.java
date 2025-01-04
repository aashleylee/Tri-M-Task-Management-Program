package com.example.second;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ExecCouncilHomeController implements Initializable {
    @FXML
    private ListView<Task> stageManagementListView;
    @FXML
    private ListView<Task> PRListView;
    @FXML
    private ListView<Task> communicationsListView;
    private User user;
    @FXML
    private Label fullNameLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private ListView<Task> myTasksListView;
    @FXML
    private ListView<Task> reviewTasksListView;
    @FXML
    private StackedBarChart<String, Number> stageManagementChart;
    @FXML
    private StackedBarChart<String, Number> prChart;
    @FXML
    private StackedBarChart<String, Number> communicationsChart;
    @FXML
    private StackedBarChart<String, Number> execCouncilChart;
    public ExecCouncilHomeController(User user) {
        this.user = user;
    }

    public void openAddNewTaskScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-new-task.fxml"));
            AddNewTaskController addNewTaskController = new AddNewTaskController(user);
            loader.setController(addNewTaskController);

            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void openChooseLoginScreen(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("choose-user.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //show exec's name and position
        fullNameLabel.setText(user.getFullName());
        categoryLabel.setText(user.getCategory());
        //calls all tasks
        queryAllTasksList();
        //'All Tasks' tab
        //stage management column
        stageManagementListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    Task currentTask = stageManagementListView.getSelectionModel().getSelectedItem();
                    openTaskInfoScreen(currentTask);
                }
            }
        });
        //PR column
        PRListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    Task currentTask = PRListView.getSelectionModel().getSelectedItem();
                    openTaskInfoScreen(currentTask);
                }
            }
        });
        //communications column
        communicationsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    Task currentTask = communicationsListView.getSelectionModel().getSelectedItem();
                    openTaskInfoScreen(currentTask);
                }
            }
        });
        //'My Tasks' tab
        myTasksListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    Task currentTask = myTasksListView.getSelectionModel().getSelectedItem();
                    openTaskInfoScreen(currentTask);
                }
            }
        });
        //'Requested for Review' tab
        reviewTasksListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    Task currentTask = reviewTasksListView.getSelectionModel().getSelectedItem();
                    openFeedbackTaskInfoScreen(currentTask);
                }
            }
        });
    }

    public void queryAllTasksList() {
        //method that runs everytime something is updated
        //when refreshing, clear items first so it doesn't continually add
        reviewTasksListView.getItems().clear();
        myTasksListView.getItems().clear();
        stageManagementListView.getItems().clear();
        PRListView.getItems().clear();
        communicationsListView.getItems().clear();
        //when refreshing, clear data first so it doesn't continually add
        stageManagementChart.getData().clear();
        prChart.getData().clear();
        communicationsChart.getData().clear();
        execCouncilChart.getData().clear();
        Connection connection = null;
        PreparedStatement psSelectAllTasks = null;
        PreparedStatement psSelectCategories = null;
        PreparedStatement psSelectAssignedMembers = null;
        PreparedStatement psSelectUser = null;
        ResultSet allTasksResultSet = null;
        ResultSet categoriesResultSet = null;
        ResultSet assignedMembersResultSet = null;
        ResultSet userResultSet = null;
        ArrayList<Integer> categories = new ArrayList<>();
        ArrayList<User> assignedMembers = new ArrayList<>();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager",
                    "root", "java1234");
            //query all tasks
            psSelectAllTasks = connection.prepareStatement("SELECT * FROM tasks");
            allTasksResultSet = psSelectAllTasks.executeQuery();
            //while the query above executes
            while (allTasksResultSet.next()) {
                int taskId = allTasksResultSet.getInt("id");
                psSelectCategories = connection.prepareStatement
                        ("SELECT * FROM tasks_categories WHERE task_id = ?");
                psSelectCategories.setInt(1, taskId);
                categoriesResultSet = psSelectCategories.executeQuery();

                while (categoriesResultSet.next()) {
                    categories.add(categoriesResultSet.getInt("category_id"));
                }

                psSelectAssignedMembers = connection.prepareStatement
                        ("SELECT * FROM tasks_users WHERE task_id = ?");
                psSelectAssignedMembers.setInt(1, taskId);
                assignedMembersResultSet = psSelectAssignedMembers.executeQuery();

                while (assignedMembersResultSet.next()) {
                    psSelectUser = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
                    psSelectUser.setInt(1, assignedMembersResultSet.getInt("user_id"));
                    userResultSet = psSelectUser.executeQuery();

                    while (userResultSet.next()) {
                        assignedMembers.add(new User(userResultSet.getInt("id"),
                                userResultSet.getString("full_name"),
                                userResultSet.getString("username"),
                                userResultSet.getString("password"),
                                userResultSet.getInt("account_type"),
                                userResultSet.getInt("category")));
                    }
                }
                Task task = new Task(allTasksResultSet.getInt("id"),
                        allTasksResultSet.getString("title"),
                        allTasksResultSet.getString("description"),
                        categories,
                        allTasksResultSet.getDate("due_date").toLocalDate(),
                        assignedMembers,
                        allTasksResultSet.getInt("status"),
                        allTasksResultSet.getInt("revisions"),
                        allTasksResultSet.getString("feedback"));

                assignedMembers = new ArrayList<>();
                for (int category : categories) {
                    //not completed: added because now we don't deleted completed tasks from database
                    if (category == 1 && task.getStatus()!=3) {
                        stageManagementListView.getItems().add(task);
                    } else if (category == 2 && task.getStatus()!=3) {
                        PRListView.getItems().add(task);
                    } else if (category == 3 && task.getStatus()!=3) {
                        communicationsListView.getItems().add(task);
                    }
                }
                for(User assignedMember: task.getAssignedMembers()){
                    if(assignedMember.getId() == user.getId() && task.getStatus() != 3){ //revised code
                        //check if current user is assigned member and then add this task to myTaskListView
                        myTasksListView.getItems().add(task);
                    }
                }
                if(task.getStatus() == 2){//check if current task status is 2 then add to review
                    reviewTasksListView.getItems().add(task);
                }categories = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (allTasksResultSet != null) {
                try {
                    allTasksResultSet.close();
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
            if (psSelectAllTasks != null) {
                try {
                    psSelectAllTasks.close();
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
        //create progress chart
        ArrayList<String> stageManagementNames = getNames(1);
        createProgressChart(stageManagementChart, stageManagementNames, getTasksForNames(stageManagementNames));
        ArrayList<String> prNames = getNames(2);
        createProgressChart(prChart, prNames, getTasksForNames(prNames));
        ArrayList<String> communicationsNames = getNames(3);
        createProgressChart(communicationsChart, communicationsNames, getTasksForNames(communicationsNames));
        //because execs are all different categories need to combine
        ArrayList<String> execNames = getNames(4);
        execNames.addAll(getNames(5));
        execNames.addAll(getNames(6));
        execNames.addAll(getNames(7));
        execNames.addAll(getNames(8));
        createProgressChart(execCouncilChart, execNames, getTasksForNames(execNames));

    }

    public void openTaskInfoScreen(Task currentTask) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("exec-task-info.fxml"));

            ExecTaskInfoController taskInfoController = new ExecTaskInfoController(currentTask);
            loader.setController(taskInfoController);

            AnchorPane anchorPane = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(anchorPane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            queryAllTasksList();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void openFeedbackTaskInfoScreen(Task currentTask) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("exec-feedback-task-info.fxml"));

            ExecFeedbackTaskInfoController taskInfoController = new ExecFeedbackTaskInfoController(currentTask);
            loader.setController(taskInfoController);

            AnchorPane anchorPane = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(anchorPane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            queryAllTasksList();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getNames(int category){
        //returns arraylist of names in that category
        ArrayList<String> names = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement psSelect = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager", "root", "java1234");
            psSelect = connection.prepareStatement("SELECT * FROM users");
            resultSet = psSelect.executeQuery();
            while (resultSet.next()) {
                int userCategory = resultSet.getInt("category");
                if(userCategory == category){
                    names.add(resultSet.getString("full_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psSelect != null) {
                try {
                    psSelect.close();
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
        return names;
    }

    public ArrayList<Task> getTasksForNames(ArrayList<String> names){
        //getting tasks that contain the names
        ArrayList<Task> tasks = new ArrayList<Task>();
        Connection connection = null;
        PreparedStatement psSelectAllTasks = null;
        PreparedStatement psSelectCategories = null;
        PreparedStatement psSelectAssignedMembers = null;
        PreparedStatement psSelectUser = null;
        ResultSet allTasksResultSet = null;
        ResultSet categoriesResultSet = null;
        ResultSet assignedMembersResultSet = null;
        ResultSet userResultSet = null;
        ArrayList<Integer> categories = new ArrayList<>();
        ArrayList<User> assignedMembers = new ArrayList<>();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager", "root", "java1234");
            psSelectAllTasks = connection.prepareStatement("SELECT * FROM tasks");
            allTasksResultSet = psSelectAllTasks.executeQuery();
            while (allTasksResultSet.next()) {
                int taskId = allTasksResultSet.getInt("id");
                psSelectCategories = connection.prepareStatement("SELECT * FROM tasks_categories WHERE task_id = ?");
                psSelectCategories.setInt(1, taskId);
                categoriesResultSet = psSelectCategories.executeQuery();

                while (categoriesResultSet.next()) {
                    categories.add(categoriesResultSet.getInt("category_id"));
                }

                psSelectAssignedMembers = connection.prepareStatement("SELECT * FROM tasks_users WHERE task_id = ?");
                psSelectAssignedMembers.setInt(1, taskId);
                assignedMembersResultSet = psSelectAssignedMembers.executeQuery();

                while (assignedMembersResultSet.next()) {
                    psSelectUser = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
                    psSelectUser.setInt(1, assignedMembersResultSet.getInt("user_id"));
                    userResultSet = psSelectUser.executeQuery();

                    while (userResultSet.next()) {
                        assignedMembers.add(new User(userResultSet.getInt("id"), userResultSet.getString("full_name"), userResultSet.getString("username"), userResultSet.getString("password"), userResultSet.getInt("account_type"), userResultSet.getInt("category")));
                    }
                }
                boolean isAssignedTask = false;
                for(User member : assignedMembers){
                    if(names.contains(member.getFullName())) {
                        isAssignedTask = true;
                        break;
                    }
                }
                if(isAssignedTask){
                    tasks.add(new Task(allTasksResultSet.getInt("id"), allTasksResultSet.getString("title"), allTasksResultSet.getString("description"), categories, allTasksResultSet.getDate("due_date").toLocalDate(), assignedMembers, allTasksResultSet.getInt("status"), allTasksResultSet.getInt("revisions"), allTasksResultSet.getString("feedback")));
                }
                assignedMembers = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (allTasksResultSet != null) {
                try {
                    allTasksResultSet.close();
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
            if (psSelectAllTasks != null) {
                try {
                    psSelectAllTasks.close();
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
        return tasks;
    }

    public void createProgressChart(StackedBarChart<String, Number> chart, ArrayList<String> names,
                                    ArrayList<Task> tasks){
        //set x-axis to be names of members
        ((CategoryAxis) chart.getXAxis()).setCategories(FXCollections.observableArrayList(names));
        //hash map-- key: names, value: number of tasks
        Map<String, Integer> completedWithoutRevisions = new HashMap<>();
        Map<String, Integer> completedWithRevisions = new HashMap<>();
        Map<String, Integer> notCompleted = new HashMap<>();
        Map<String, Integer> overdue = new HashMap<>();
        //loop through tasks list and add task to relevant map
        for(Task task : tasks){
            //check for completed w/o revisions
            //instead of deleting tasks -> store with new status 1 is active, 2 in-review, 3 is completed
            if(task.getStatus() == 3 && task.getRevisions() == 0){
                //loop through assigned members of that task and add it to map
                for(User user : task.getAssignedMembers()){
                    if(completedWithoutRevisions.containsKey(user.getFullName())){
                        completedWithoutRevisions.put(user.getFullName(),
                                completedWithoutRevisions.get(user.getFullName())+1);
                    } else{
                        completedWithoutRevisions.put(user.getFullName(),1);
                    }
                }
            } else if(task.getStatus() == 3 && task.getRevisions() > 0){
                for(User user : task.getAssignedMembers()){
                    if(completedWithRevisions.containsKey(user.getFullName())){
                        completedWithRevisions.put(user.getFullName(),
                                completedWithRevisions.get(user.getFullName())+1);
                    } else{
                        completedWithRevisions.put(user.getFullName(),1);
                    }
                }
            } else if((task.getStatus() == 1 || task.getStatus() == 2) &&
                    LocalDate.now().isAfter(task.getDueDate())){ // check for overdue first
                for(User user : task.getAssignedMembers()){
                    if(overdue.containsKey(user.getFullName())){
                        overdue.put(user.getFullName(),overdue.get(user.getFullName())+1);
                    } else{
                        overdue.put(user.getFullName(),1);
                    }
                }
            } else if(task.getStatus() == 1 || task.getStatus() == 2){
                for(User user : task.getAssignedMembers()){
                    if(notCompleted.containsKey(user.getFullName())){
                        notCompleted.put(user.getFullName(),notCompleted.get(user.getFullName())+1);
                    } else{
                        notCompleted.put(user.getFullName(),1);
                    }
                }
            }
        }
        //bar chart
        XYChart.Series<String, Number> completedWithoutRevisionsSeries = new XYChart.Series<>();
        completedWithoutRevisionsSeries.setName("Tasks Completed (No revisions)");
        for(Map.Entry<String, Integer> entry : completedWithoutRevisions.entrySet()){
            String name = entry.getKey();
            Integer numberOfTasks = entry.getValue();
            completedWithoutRevisionsSeries.getData().add(new XYChart.Data<>(name, numberOfTasks));
        }
        XYChart.Series<String, Number> completedWithRevisionsSeries = new XYChart.Series<>();
        completedWithRevisionsSeries.setName("Tasks Completed (1+ Revisions)");
        for(Map.Entry<String, Integer> entry : completedWithRevisions.entrySet()){
            String name = entry.getKey();
            Integer numberOfTasks = entry.getValue();
            completedWithRevisionsSeries.getData().add(new XYChart.Data<>(name, numberOfTasks));
        }
        XYChart.Series<String, Number> notCompletedSeries = new XYChart.Series<>();
        notCompletedSeries.setName("Not Completed");
        for(Map.Entry<String, Integer> entry : notCompleted.entrySet()){
            String name = entry.getKey();
            Integer numberOfTasks = entry.getValue();
            notCompletedSeries.getData().add(new XYChart.Data<>(name, numberOfTasks));
        }
        XYChart.Series<String, Number> overdueSeries = new XYChart.Series<>();
        overdueSeries.setName("Overdue");
        for(Map.Entry<String, Integer> entry : overdue.entrySet()){
            String name = entry.getKey();
            Integer numberOfTasks = entry.getValue();
            overdueSeries.getData().add(new XYChart.Data<>(name, numberOfTasks));
        }
        chart.getData().addAll(completedWithoutRevisionsSeries, completedWithRevisionsSeries,
                notCompletedSeries, overdueSeries);
        //mouse hover events
        for(XYChart.Series<String, Number> series : chart.getData()){
            for(XYChart.Data<String, Number> item : series.getData()){
                Node node = item.getNode();
                node.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //glowing when hovered
                        node.setEffect(new Glow(0.8));
                        Tooltip.install(node, new Tooltip(series.getName()+": "
                                + item.getYValue().toString()));
                    }
                });
                node.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //un-glow when not hovered
                        node.setEffect(null);
                    }
                });
            }
        }
    }
}