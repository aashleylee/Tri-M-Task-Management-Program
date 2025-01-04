package com.example.second;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ChooseUserController {
    public void openSignUpScreen(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("sign-up.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    } //runs when 'Sign in' HyperLink clicked
    public void openMemberLoginScreen(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            LoginController loginController = new LoginController(true);
            loader.setController(loginController);

            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    } //runs when 'Member' Button clicked
    public void openExecLoginScreen(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            LoginController loginController = new LoginController(false);
            loader.setController(loginController);

            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    } //runs when 'Exec' Button clicked
}

