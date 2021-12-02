package scheduling.demoschedulingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Eric  Mullally
 *  Project for Software II  Advanced Java Concepts  C195
 *  Student ID #003294500
 *
 * <h2>Lambdas:</h2>
 *           <h3>Location:</h3> MainController.setLanguage
 *              <h4>justification:<h4/>
*                    <p> the map of french tabs is iterable and could be operated on with
*                        a normal for loop. however the built in method forEach uses the
*                        same time cost and is much cleaner. though a little harder to read,
*                        it substantially cuts down on the functions size. </p>
 *
 *          <h3>Location:</h3> ManageAppointmentController.setLanguage
 *                 <h4>justification:<h4/>
 *                 <p>containing both hash maps of frenchBtns and french labels, to use
 *                 a simple for loop would have taken much more space and been just as
 *                 time costly. the use of lambdas here cuts down significantly not only on the
 *                 repetiiton. but also the clutter.<p/>
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



}