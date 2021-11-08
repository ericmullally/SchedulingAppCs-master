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

    public static void main(String[] args) {
        launch();
    }


    /**
     * Changes scene on stage and changes size.
     * @param fxml
     * @param title
     * @param width
     * @param height
     * @throws IOException
     */
    public void changeScene(String fxml, String title, int width , int height ) throws IOException{
        Parent loader = FXMLLoader.load(getClass().getResource(fxml));
        stg.getScene().setRoot(loader);
        stg.setWidth(width);
        stg.setHeight(height);
        stg.setTitle(title);
    }
}