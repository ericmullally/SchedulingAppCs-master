package scheduling.demoschedulingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Eric  Mullally
 */


public class SchedulingApplication extends Application {
    private static Stage stg;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        stg = stage;
        Parent root = FXMLLoader.load(SchedulingApplication.class.getResource("login.fxml"));
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Login");
        stage.show();
    }

    /**
     * Sets the Main scene once user successfully logs in.
     *
     * @param fxml fxml File Name. String
     * @param title Title of the window to open. String
     * @param width Width of new stage. int
     * @param height Height of new stage. int
     * @throws IOException
     */
    public void changeScene(String fxml, String title, int width, int height) throws IOException {

        Parent loader = FXMLLoader.load(getClass().getResource(fxml));
        stg.getScene().setRoot(loader);
        stg.setWidth(width);
        stg.setHeight(height);
        stg.setTitle(title);
    }

    /**
     * opens a new window on top of the original application.
     *
     * @param fxml  fxml File Name.
     * @param title Title of the window to open.
     * @throws IOException
     */
    public static void showNewWindow(String fxml, String title) throws IOException {

        Parent loader = FXMLLoader.load(SchedulingApplication.class.getResource(fxml));
        Stage addWindow = new Stage();
        addWindow.setScene(new Scene(loader));
        addWindow.setTitle(title);
        addWindow.initModality(Modality.APPLICATION_MODAL);
        addWindow.show();
    }


}