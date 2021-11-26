package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;


import scheduling.demoschedulingapp.Classes.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.TemporalField;
import java.util.HashMap;


public class AddAppointmentController {

    @FXML
    TextField appointmentIdTxt, typeTxt, titleTxt, locationTxt;
    @FXML
    ChoiceBox cusNameBox, contactBox;
    @FXML
    Spinner<Integer> startSpinnerHr, startSpinnerMin, endSpinnerHr, endSpinnerMin;
    @FXML
    DatePicker datePic;
    @FXML
    TextArea descTxt;
    @FXML
    Button submitBtn, cancelBtn;
    @FXML
    Label appLbl, titleLbl, descLbl, cusNameLbl, locLbl, typeLbl, dateLbl, startLbl, endLbl, contactLbl ;

    int appointmentID;
    Boolean isEdit = false;

    @FXML
    public void initialize(){
        setLanguage();
        setAppointmentId();
        setCustomerNamesBox();
        setTimeBoxes();
        setContactBox();

    }

    /**
     * sets Labels and buttons to french if the users computer is using french.
     */
    private void setLanguage(){
       HashMap<Label, String> frenchLabels = new HashMap<>(){{
           put(appLbl, "Identifiant de rendez-vous"); put(titleLbl, "Titre"); put(descLbl, "La description");
           put(cusNameLbl, "Nom du client"); put(locLbl, "Emplacement"); put(typeLbl, "Le genre");
           put(dateLbl, "Date"); put(startLbl, "Début"); put(endLbl, "Finir");
           put(contactLbl, "Contact");
       }};
       HashMap<Button, String> frenchButtons = new HashMap<>(){{ put(submitBtn, "Soumettre"); put(cancelBtn, "Annuler"); }};

       if(User.getInstance().getSystemLanguage().equals("fr")){
           frenchLabels.forEach((k,v)-> k.setText(v)  );
           frenchButtons.forEach((k,v)-> k.setText(v));
       }
    }

    /**
     * sets appointment ID based on the number of appointments in the system.
     */
    private void setAppointmentId(){
        try{
            dbUtils.establishConnection();
            ResultSet appCount = dbUtils.connStatement.executeQuery("select max(Appointment_ID) from appointments");
            appCount.next();
            appointmentID = appCount.getInt("max(Appointment_ID)") + 1;
            appCount.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * sets The customer drop down options.
     */
    private void setCustomerNamesBox(){
        dbUtils.establishConnection();
        ObservableList<String> customers = FXCollections.observableArrayList();

        try{
            ResultSet customerResult = dbUtils.connStatement.executeQuery("select * from customers");
            while(customerResult.next()){
                customers.add(customerResult.getString("Customer_Name"));
            }
            customerResult.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        customers.forEach(item -> cusNameBox.getItems().add(item));
    }

    /**
     * sets value factory for all time spinners and uses filter
     * on event handler to ensure only numbers are set.
     */
    private void setTimeBoxes(){
        SpinnerValueFactory<Integer> startHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24);
        SpinnerValueFactory<Integer> startMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        SpinnerValueFactory<Integer> endHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24);
        SpinnerValueFactory<Integer> endMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        startHours.setValue(0);
        startMinutes.setValue(00);
        endHours.setValue(0);
        endMinutes.setValue(00);

        startSpinnerHr.setValueFactory(startHours);
        startSpinnerMin.setValueFactory(startMinutes);
        endSpinnerHr.setValueFactory(endHours);
        endSpinnerMin.setValueFactory(endMinutes);
        String numberPattern = "\\d*";

        ObservableList<Spinner> spinners = FXCollections.observableArrayList(startSpinnerHr, startSpinnerMin, endSpinnerHr, endSpinnerMin);

        spinners.forEach(spinner-> spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches(numberPattern)){
                spinner.getEditor().setText(oldValue);
            }
        })
        );

    }

    /**
     * sets the contact dropdown box.
     */
    private void setContactBox(){
        dbUtils.establishConnection();
        ObservableList<String> contacts = FXCollections.observableArrayList();

        try{
            ResultSet contactResults = dbUtils.connStatement.executeQuery("select * from contacts");
            while(contactResults.next()){
                contacts.add(contactResults.getString("Contact_Name"));
            }
            contactResults.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        contacts.forEach(item -> contactBox.getItems().add(item));
    }

    public void createAppointment(){
        ZonedDateTime utcTime = ZonedDateTime.of(LocalDateTime.now( ZoneId.of("UTC")), ZoneId.of("UTC"));

        if(!checkSchedulingErrors()){
            return;
        }


    }

    /**
     * checks that the customer name has been provided. then passes the customer id to the check Time method
     * which checks for scheduling conflicts.
     * @return false if there is an error true otherwise.
     */
    private Boolean checkSchedulingErrors(){
        dbUtils.establishConnection();
        try {
                int customerId;
                if(cusNameBox.getValue() == null || contactBox.getValue() == null){
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    String frenchMessage =  cusNameBox.getValue() == null ? "Veuillez sélectionner un client.":"Veuillez sélectionner un contact.";
                    String englishMessage =  cusNameBox.getValue() == null ? "Please select a customer." : "Please select a contact.";
                    error.setTitle(User.getInstance().getSystemLanguage().equals("en") ?  "Submission Error" : "Erreur de soumission");
                    error.setContentText(User.getInstance().getSystemLanguage().equals("en") ? englishMessage : frenchMessage);
                    error.show();
                    return false;
                }else{
                    ResultSet customerIdRes = dbUtils.connStatement.executeQuery(String.format("select Customer_ID from customers where" +
                            " Customer_Name = \"%s\"", cusNameBox.getValue()));
                    customerIdRes.next();
                    customerId = customerIdRes.getInt("Customer_ID");
                }
                return checkTime(customerId);

        }catch(SQLException e){
            System.out.println(e.getMessage());
            return true;
        }
    }

    /**
     * checks that the appointment being created does not conflict with the customers
     * other appointments and that the appointment is within business hours.
     * @param customerID used to check the customers schedule.
     * @return false if there is a conflict true otherwise.
     */
    private Boolean checkTime(int customerID) {
        dbUtils.establishConnection();
        ZoneOffset cooperateOffset = ZonedDateTime.of(LocalDateTime.now(ZoneId.of("US/Eastern")), ZoneId.of("US/Eastern")).getOffset();
        LocalTime businessStart = LocalTime.of(8, 00).plusSeconds(cooperateOffset.getTotalSeconds());
        LocalTime businessEnd = LocalTime.of(22, 00).plusSeconds(cooperateOffset.getTotalSeconds());

        LocalDate enteredDate = LocalDate.of(datePic.getValue().getYear(), datePic.getValue().getMonth(), datePic.getValue().getDayOfMonth());
        LocalDateTime enteredStart = LocalDateTime.of(enteredDate, LocalTime.of(startSpinnerHr.getValue(), startSpinnerMin.getValue()));
        LocalDateTime enteredEnd = LocalDateTime.of(enteredDate, LocalTime.of(endSpinnerHr.getValue(), endSpinnerMin.getValue()));

        try {
            ResultSet customerAppointmentsRes = dbUtils.connStatement.executeQuery(String.format("select Start, End from appointments " +
                                                                                                        "where Customer_ID = %s", customerID));
            while (customerAppointmentsRes.next()) {
                LocalDateTime start = customerAppointmentsRes.getDate("Start").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime end = customerAppointmentsRes.getDate("End").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                if (enteredStart.isAfter(start) || enteredEnd.isBefore(end)) {
                    Alert scheduleError = new Alert(Alert.AlertType.ERROR);
                    scheduleError.setTitle("Conflicting appointment");
                    scheduleError.setContentText(String.format("%s Already has an appointment within this time frame.", cusNameBox.getValue()));
                    scheduleError.showAndWait();
                    return false;
                }
            }
            customerAppointmentsRes.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (LocalTime.of(enteredStart.getHour(), enteredStart.getMinute()).isBefore(businessStart) ||
                LocalTime.of(enteredEnd.getHour(), enteredEnd.getMinute()).isAfter(businessEnd)) {
            Alert scheduleError = new Alert(Alert.AlertType.ERROR);
            scheduleError.setTitle("Outside Business Hours");
            scheduleError.setContentText(String.format("You have selected a time outside of business hours. " +
                                                        "Business hours for your time zone are from: %s to:%s" +
                                                        "Yes it would be smarter to make these times unavailable, but that wasn't the assignment. ", businessStart, businessEnd));
            scheduleError.showAndWait();
            return false;
        }
        return true;

    }
}
