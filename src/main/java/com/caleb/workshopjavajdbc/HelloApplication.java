package com.caleb.workshopjavajdbc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private static Scene scene;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MainView.fxml"));
        ScrollPane scrollPane = fxmlLoader.load();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scene = new Scene(scrollPane, 600, 400);
        stage.setTitle("Sample JavaFx application");
        stage.setScene(scene);
        stage.show();
    }

    public static Scene getScene(){
        return scene;
    }

    public static void main(String[] args) {
        launch();
    }
}