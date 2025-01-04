package com.example.second;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private Label loginLabel; @FXML private TextField username; @FXML private PasswordField password;
    private User user;//later assigned to currently logged-in user
    private boolean isMember; //checks whether clicked member or not
    public LoginController(boolean isMember) {
        this.isMember = isMember;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (isMember) {
            loginLabel.setText("Member Login");
        } else {
            loginLabel.setText("    Exec Login");
        }
    }

    public void openChooseLoginScreen(ActionEvent event) {
        try { //loads choose-user.fxml for user to indicate either member, exec, or sign in
            Parent root = FXMLLoader.load(getClass().getResource("choose-user.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginUser(ActionEvent event) {
        Connection connection = null; //Connection: session between Java and database
        PreparedStatement psSelect = null; //PreparedStatement: simplifies queries using methods
        ResultSet resultSet = null; //set of data returned from database, rows and columns

        try {
            //connection from code to MySQL database through '.getConnection()' method with given url
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trim-task-manager",
                    "root", "java1234");
            psSelect = connection.prepareStatement("SELECT * FROM users where username = ?");
            psSelect.setString(1, username.getText());
            resultSet = psSelect.executeQuery();

            if (!resultSet.isBeforeFirst()) { //in the case that resultSet query does not exist - user info doesn't exist
                //error message when user information does not exist
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Unrecognized username or password");
                alert.show();
            } else {
                while (resultSet.next()) { //user info exists -> attempting login
                    String storedPassword = resultSet.getString("password"); //queries the password in database
                    int accountType = resultSet.getInt("account_type"); //queries the account_type in database
                    if (isMember && accountType == 2) { //check + return if Exec chose Member login
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Unrecognized username or password");
                        alert.show();
                    } else if (!isMember && accountType == 1) { //check + return if Member chose Exec login
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Unrecognized username or password");
                        alert.show();
                    } else { //runs when user exists + chose right login
                        if (storedPassword.equals(password.getText())) {
                            //when password is correct, current User object is assigned the user that is currrently logged in
                            user = new User(resultSet.getInt("id"), resultSet.getString("full_name"),
                                    resultSet.getString("username"), resultSet.getString("password"),
                                    resultSet.getInt("account_type"), resultSet.getInt("category"));
                            try {
                                if (isMember) { //if user is a Member, loads the member-home.fxml page
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("member-home.fxml"));

                                    MemberHomeController memberHomeController = new MemberHomeController(user);
                                    loader.setController(memberHomeController);

                                    AnchorPane anchorPane = loader.load();
                                    Scene scene = new Scene(anchorPane);
                                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    stage.setScene(scene);
                                    stage.show();
                                } else { //if user is an Exec, loads the exec-home.fxml page
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("exec-council-home.fxml"));

                                    ExecCouncilHomeController execCouncilHomeController = new ExecCouncilHomeController(user);
                                    loader.setController(execCouncilHomeController);

                                    AnchorPane anchorPane = loader.load();
                                    Scene scene = new Scene(anchorPane);
                                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    stage.setScene(scene);
                                    stage.show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else { //error message when password is incorrect
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Unrecognized username or password");
                            alert.show();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally { //closing connection with SQL
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
    }
}
