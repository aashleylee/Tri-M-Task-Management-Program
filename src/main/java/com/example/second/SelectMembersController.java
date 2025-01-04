package com.example.second;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SelectMembersController implements Initializable {
    private ObservableList<User> allMembers;
    @FXML
    private TableView<User> membersTable;
    @FXML
    private TableColumn<User, String> nameCol;
    @FXML
    private TableColumn<User, String> categoryCol;
    @FXML
    private TableColumn<User, String> selectCol;
    @FXML
    private Button addSelectedButton;
    private ArrayList<Integer> selectedCategories;

    public SelectMembersController(ArrayList<Integer> selectedCategories){
        this.selectedCategories = selectedCategories;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<User> allMembersArray = new ArrayList<>();
        Connection connection = null;
        PreparedStatement psSelect = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager",
                    "root", "java1234");
            psSelect = connection.prepareStatement("SELECT * FROM users");
            resultSet = psSelect.executeQuery();
            while (resultSet.next()) {
                int category = resultSet.getInt("category");
                if(selectedCategories.indexOf(category) != -1 || category>=4){
                    //indexOf returns index of object in arraylist, if not in arraylist then returns -1
                    allMembersArray.add(new User(resultSet.getInt("id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getInt("account_type"),
                            resultSet.getInt("category")));
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
        allMembers = FXCollections.observableArrayList(allMembersArray);
        nameCol.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<User, String>("category"));
        selectCol.setCellValueFactory(new PropertyValueFactory<User, String>("select"));
        membersTable.setItems(allMembers);
    }
    public void addSelectedMembers(ActionEvent event){
        ArrayList<User> selectedMembers = new ArrayList<User>();
        for(User member : allMembers){
            if(member.getSelect().isSelected()){
                selectedMembers.add(member);
            }
        }
        SelectedMembersHolder holder = SelectedMembersHolder.getInstance();
        holder.setSelectedMembers(selectedMembers);
        Stage stage = (Stage) addSelectedButton.getScene().getWindow();
        stage.close();
    }

}
