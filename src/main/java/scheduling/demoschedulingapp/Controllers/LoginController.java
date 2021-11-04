package scheduling.demoschedulingapp.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import scheduling.demoschedulingapp.SchedulingApplication;

import java.io.IOException;


public class LoginController {
    @FXML
    TextField nameText;
    @FXML
    PasswordField passwordText;


    public void login() throws IOException {
        String name = nameText.getText();
        String password = nameText.getText();
        if(checkLogin(name, password )){
            SchedulingApplication mainStage = new SchedulingApplication();
            mainStage.changeScene("main.fxml", "Manage Customers");
        }
    }

    private boolean checkLogin(String name, String password){
        return true;
    }

}
