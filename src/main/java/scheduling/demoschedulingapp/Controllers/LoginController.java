package scheduling.demoschedulingapp.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import scheduling.demoschedulingapp.SchedulingApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * still might need an actual login with encryption.
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
        regionLbl.setText(Locale.getDefault().getCountry() +" " + Calendar.getInstance().getTimeZone().getDisplayName());
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
            SchedulingApplication mainStage = new SchedulingApplication();
            mainStage.changeScene("main.fxml", "Scheduling App", 780, 400);
        } else {
            Alert loginFail = new Alert(Alert.AlertType.ERROR);
            loginFail.setTitle("Login Failed.");
            loginFail.setContentText("the credentials you entered could not be found. Please try again.");
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
        dbUtils connection = new dbUtils();
        Statement connector = connection.connStatement;
        String requestString = "select User_name, Password from users where User_name = \"test\"";
//        String requestString = String.format("select User_name, Password from users where User_name = \"%s\"", name);
        String usersPasswordInDb = "";

        try {
            ResultSet answer = connector.executeQuery(requestString);
            answer.next();
            usersPasswordInDb = answer.getString("Password");

            connector.close();
            return usersPasswordInDb.equals("test");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * sets labels to french if the host system is using french.
     */
    private void setLanguage(){
        HashMap<String, String> FrenchLogin = new HashMap<String, String>(){
            {
                put("welcome","Bienvenu");
                put("subText","Veuillez vous connecter");
                put("usernameLbl","Nom d'utilisateur");
                put("passwordLbl","le mot de passe");
                put("loginBtn","connexion");
                put("cancelBtn", "Annuler");
            }
        };

        if(Locale.getDefault().getLanguage() == "fr"){
            loginWelcome.setText(FrenchLogin.get("welcome"));
            loginSubLabel.setText(FrenchLogin.get("subText"));
            loginUsername.setText(FrenchLogin.get("usernameLbl"));
            loginPass.setText(FrenchLogin.get("passwordLbl"));
            loginBtn.setText(FrenchLogin.get("loginBtn"));
            exitBtn.setText(FrenchLogin.get("cancelBtn"));
        }
    }

}
