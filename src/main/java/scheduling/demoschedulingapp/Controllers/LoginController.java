package scheduling.demoschedulingapp.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scheduling.demoschedulingapp.Classes.User;
import scheduling.demoschedulingapp.SchedulingApplication;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Controls all Login functionality.
 */
public class LoginController {
    @FXML
    TextField nameText;
    @FXML
    PasswordField passwordText;
    @FXML
    Label loginWelcome, loginSubLabel, loginUsername, loginPass, regionLbl;
    @FXML
    Button loginBtn, exitBtn;


    @FXML
    public void initialize(){
        dbUtils.establishConnection();
        regionLbl.setText(String.format(" %s  TimeZone: %s",Locale.getDefault().getCountry(),User.getInstance().getUserTimeZone() ));
        setLanguage();
    }

    /**
     * submits login request. opens the main page if successful.
     * displays error message if not.
     * @throws IOException
     */
    public void login() throws IOException {
        String name = nameText.getText();
        String password = passwordText.getText();
        if (checkLogin(name, password)) {
            User.getInstance().setUserName(name);

            SchedulingApplication mainStage = new SchedulingApplication();
            mainStage.changeScene("main.fxml", "Scheduling App", 780, 400);
        } else {
            Alert loginFail = new Alert(Alert.AlertType.ERROR);
            loginFail.setTitle(User.getInstance().getSystemLanguage() != "fr" ? "Login Failed." : "Échec de la connexion");
            loginFail.setContentText( User.getInstance().getSystemLanguage() != "fr" ? "Username or password incorrect." : "Nom d'utilisateur ou mot de passe incorrect.");
            loginFail.showAndWait();
        }
    }

    /**
     * checks the entered credentials against the database. horrible security to do it this way of course.
     *
     * @param name
     * @param password
     * @return true if successful false otherwise.
     */
    private boolean checkLogin(String name, String password) {

        String requestString = "select User_name, Password from users where User_name = \"test\"";
//        String requestString = String.format("select User_name, Password from users where User_name = \"%s\"", name);
        String usersPasswordInDb = "";

        try {
            ResultSet answer = dbUtils.connStatement.executeQuery(requestString);
            answer.next();
            usersPasswordInDb = answer.getString("Password");

            dbUtils.connStatement.close();
            return usersPasswordInDb.equals("test");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * sets labels to french if the host system is using french.
     * Contains Lambda
     */
    private void setLanguage(){
        HashMap<Label, String> FrenchLogin = new HashMap<Label, String>(){
            {
                put(loginWelcome,"Bienvenu");
                put(loginSubLabel,"Veuillez vous connecter");
                put(loginUsername,"Nom d'utilisateur");
                put(loginPass,"le mot de passe");
            }
        };

        if(User.getInstance().getSystemLanguage().equals("fr")){
            FrenchLogin.forEach((k,v) -> k.setText(v) );
            loginBtn.setText("connexion");
            exitBtn.setText("Annuler");
        }
    }

    /**
     * closes application.
     */
    public void exit(){
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

}
