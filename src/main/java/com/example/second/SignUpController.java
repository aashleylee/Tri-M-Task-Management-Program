package com.example.second;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    private String[] memberCategories = {"Stage Management", "PR", "Communications"};
    private String[] execCategories = {"President", "Vice President", "Stage Management Exec",
            "PR Exec", "Communications Exec"};
    @FXML
    private RadioButton memberRadioButton;
    @FXML
    private RadioButton execRadioButton;
    @FXML
    private ChoiceBox categoryChoiceBox;
    @FXML
    private Label categoryLabel;
    @FXML
    private TextField fullName;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    private User user;
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
        //sets the Category ChoiceBox to invisible until Account Type is specified
        categoryChoiceBox.setVisible(false);
        categoryLabel.setVisible(false);
        //based on which Account Type RadioButton is clicked, will run the corresponding method
        memberRadioButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //clears all components in the ChoiceBox and adds with memberCategories
                categoryChoiceBox.getItems().clear();
                categoryChoiceBox.getItems().addAll(memberCategories);
                //makes the ChoiceBox visible
                categoryChoiceBox.setVisible(true);
                categoryLabel.setVisible(true);
            }
        });
        execRadioButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //clears all components in the ChoiceBox and adds with execCategories
                categoryChoiceBox.getItems().clear();
                categoryChoiceBox.getItems().addAll(execCategories);
                //makes the ChoiceBox visible
                categoryChoiceBox.setVisible(true);
                categoryLabel.setVisible(true);
            }
        });
    }
    public void signUpUser(ActionEvent event){
        Connection connection = null;
        PreparedStatement psCheckUsername = null;
        PreparedStatement psInsert = null;
        PreparedStatement psSelect = null;
        ResultSet resultSet = null;
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager", "root", "java1234");
            psCheckUsername = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUsername.setString(1, username.getText());
            resultSet = psCheckUsername.executeQuery();
            if(resultSet.isBeforeFirst()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Username taken");
                alert.show();
            }
            else{
                psInsert = connection.prepareStatement("INSERT INTO users (full_name, username, password, account_type, category) VALUES (?, ?, ?, ?, ?)");
                psInsert.setString(1, fullName.getText());
                psInsert.setString(2, username.getText());
                psInsert.setString(3, password.getText());
                if(memberRadioButton.isSelected()){
                    psInsert.setInt(4, 1);
                }
                else if(execRadioButton.isSelected()){
                    psInsert.setInt(4,2);
                }

                if(categoryChoiceBox.getValue().equals("Stage Management")){
                    psInsert.setInt(5,1);
                }
                else if(categoryChoiceBox.getValue().equals("PR")){
                    psInsert.setInt(5,2);
                }
                else if(categoryChoiceBox.getValue().equals("Communications")){
                    psInsert.setInt(5,3);
                }
                else if(categoryChoiceBox.getValue().equals("President")){
                    psInsert.setInt(5,4);
                }
                else if(categoryChoiceBox.getValue().equals("Vice President")){
                    psInsert.setInt(5,5);
                }
                else if(categoryChoiceBox.getValue().equals("Stage Management Exec")){
                    psInsert.setInt(5,6);
                }
                else if(categoryChoiceBox.getValue().equals("PR Exec")){
                    psInsert.setInt(5,7);
                }
                else if(categoryChoiceBox.getValue().equals("Communications Exec")){
                    psInsert.setInt(5,8);
                }
                psInsert.executeUpdate();
                psSelect = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
                psSelect.setString(1, username.getText());
                resultSet = psSelect.executeQuery();
                while(resultSet.next()){
                    user = new User(resultSet.getInt("id"), resultSet.getString("full_name"), resultSet.getString("username"), resultSet.getString("password"), resultSet.getInt("account_type"), resultSet.getInt("category"));
                }
                if(execRadioButton.isSelected()){
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
                else if(memberRadioButton.isSelected()){
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("member-home.fxml"));

                        MemberHomeController memberHomeController = new MemberHomeController(user);
                        loader.setController(memberHomeController);

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
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psCheckUsername != null) {
                try {
                    psCheckUsername.close();
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
            if (psInsert != null) {
                try {
                    psInsert.close();
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
