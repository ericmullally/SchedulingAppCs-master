package scheduling.demoschedulingapp.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scheduling.demoschedulingapp.Classes.TimeConversion;
import scheduling.demoschedulingapp.Classes.User;
import scheduling.demoschedulingapp.SchedulingApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
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
            addLog(name, "success");
            checkUpcomingAppointment();
            SchedulingApplication mainStage = new SchedulingApplication();
            mainStage.changeScene("main.fxml", "Scheduling App", 780, 400);
        } else {
            addLog(name, "failed");
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
        dbUtils.establishConnection();
        String requestString = String.format("select User_name, Password from users where User_name = \"%s\"", name);
        String usersPasswordInDb = "";

        try {
            ResultSet answer = dbUtils.connStatement.executeQuery(requestString);
            answer.next();
            usersPasswordInDb = answer.getString("Password");

            dbUtils.connStatement.close();
            return usersPasswordInDb.equals(password);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * checks if the user has an appointment within 15 minutes of login.
     * @return true if they do false otherwise.
     */
    private void checkUpcomingAppointment(){
        dbUtils.establishConnection();

        ZonedDateTime now = ZonedDateTime.now();
        try{
            ResultSet appointmentRes = dbUtils.connStatement.executeQuery("select * from appointments");
            String ID;
            String Date;
            String Time;
            String Message = "No Upcoming Appointments";
            while(appointmentRes.next()){
                ZonedDateTime start = ZonedDateTime.of(LocalDateTime.parse(appointmentRes.getString("Start").replace(" ", "T")), ZoneId.of("UTC"));

                ZonedDateTime adjustedAppointmentTime = TimeConversion.convertTimes(start, User.getInstance().getUserTimeZone());
                Boolean isMonth = adjustedAppointmentTime.getMonth() == now.getMonth();
                Boolean isDay = adjustedAppointmentTime.getDayOfYear() == now.getDayOfYear();

                if(isMonth && isDay ){
                    if(adjustedAppointmentTime.isBefore(now.plusMinutes(15)) && adjustedAppointmentTime.isAfter(now.minusMinutes(1))){
                         ID = appointmentRes.getString("Appointment_ID");
                         Date = LocalDate.of(adjustedAppointmentTime.getYear(), adjustedAppointmentTime.getMonth(), adjustedAppointmentTime.getDayOfMonth()).toString();
                         Time = LocalTime.of(adjustedAppointmentTime.getHour(), adjustedAppointmentTime.getMinute()).toString();
                         Message =String.format("Upcoming Appointment.\n AppointmentID: %s \n Date: %s \n Time: %s",ID, Date, Time );
                    }
                }
            }
            Alert upComingAppointment = new Alert(Alert.AlertType.INFORMATION);
            upComingAppointment.setTitle("Appointment");
            upComingAppointment.setContentText(Message);
            upComingAppointment.show();

        }catch(SQLException e){
            System.out.println(e.getMessage());
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
     * adds a log entry into login_activity.txt
     * @param name name the user used
     * @param success String rather or not the attempt was successful.
     */
    private void addLog(String name, String success){
        try{
            BufferedWriter fw = new BufferedWriter(new FileWriter("login_activity.txt", true));
            ZonedDateTime now = ZonedDateTime.now();
            String content = String.format("%s_%s_%s\n", name, success, now);
            fw.write(content);
            fw.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
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
