package scheduling.demoschedulingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;


public class SchedulingApplication extends Application {
    private static Stage stg;


    @Override
    public void start(Stage stage) throws IOException {
        stg = stage;
        Parent root = FXMLLoader.load(SchedulingApplication.class.getResource("login.fxml"));
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Login");
        stage.show();
    }


    public void changeScene(String fxml, String title) throws IOException{
        Parent loader = FXMLLoader.load(getClass().getResource(fxml));
        stg.getScene().setRoot(loader);
        stg.setTitle(title);
    }

    public static void main(String[] args) {
        launch();
    }
}