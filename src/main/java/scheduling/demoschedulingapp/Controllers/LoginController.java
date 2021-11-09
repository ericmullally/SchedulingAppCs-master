package scheduling.demoschedulingapp.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import scheduling.demoschedulingapp.SchedulingApplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.IOException;

/**
 *
 * still might need an actual login with encryption.
 */

public class LoginController {
    @FXML
    TextField nameText;
    @FXML
    PasswordField passwordText;


    public void login() throws IOException {
        String name = nameText.getText();
        String password = passwordText.getText();
        if(checkLogin(name, password )){
            SchedulingApplication mainStage = new SchedulingApplication();
            mainStage.changeScene("main.fxml", "Scheduling App", 780,400);
        }else{
            Alert loginFail = new Alert(Alert.AlertType.ERROR);
            loginFail.setTitle("Login Failed.");
            loginFail.setContentText("the credentials you entered could not be found. Please try again.");
            loginFail.showAndWait();
        }
    }

    /**
     * checks the entered credentials against the database. horrible security to do it this way of course.
     * @param name
     * @param password
     * @return true if successful false otherwise.
     */
    private boolean checkLogin (String name, String password){
        dbUtils connection = new dbUtils();
        Statement connector =  connection.connStatement;
        String requestString = "select User_name, Password from users where User_name = \"test\"";
//        String requestString = String.format("select User_name, Password from users where User_name = \"%s\"", name);
        String usersPasswordInDb = "";

        try{
            ResultSet answer =  connector.executeQuery(requestString);
            while(answer.next()){
                usersPasswordInDb = answer.getString("Password");
            }
            connector.close();
            return usersPasswordInDb.equals("test");

            }catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

}
