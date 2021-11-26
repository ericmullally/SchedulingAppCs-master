package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;


import javafx.stage.Stage;
import scheduling.demoschedulingapp.Classes.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.TemporalField;
import java.util.Arrays;
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
        appointmentIdTxt.setText(String.valueOf(appointmentID));
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

    /**
     * adds the appointment to the database.
     */
    public void createAppointment(){
        if(checkFields()){
            return;
        }
        if(checkTime()){
            return;
        }

        String createStatement = buildRequestString();
        String title = titleTxt.getText();

        try{
            dbUtils.connStatement.execute(createStatement);
            dbUtils.connStatement.close();
            Alert success = new Alert(Alert.AlertType.CONFIRMATION);
            success.setTitle("Success!");
            success.setContentText(String.format("Appointment %s has been added to the schedule.", title));
            success.show();

            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            String[] className = this.getClass().getName().split("\\.");
            MainController.refreshList(className[className.length -1 ]);
            stage.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * builds the string for the make request function to use.
     * @return a string with the database command.
     */
    private String buildRequestString(){
        ZonedDateTime utcTime = ZonedDateTime.of(LocalDateTime.now( ZoneId.of("UTC")), ZoneId.of("UTC"));

        ObservableList<Integer> ids = getIds();
        String title = titleTxt.getText();
        String description = descTxt.getText();
        String location = locationTxt.getText();
        String type = typeTxt.getText();
        LocalDateTime start =  LocalDateTime.of(datePic.getValue(), LocalTime.of(startSpinnerHr.getValue(),
                startSpinnerMin.getValue())).plusSeconds(utcTime.getOffset().getTotalSeconds());
        LocalDateTime end = LocalDateTime.of(datePic.getValue(), LocalTime.of(endSpinnerHr.getValue(),
                endSpinnerMin.getValue())).plusSeconds(utcTime.getOffset().getTotalSeconds());
        LocalDateTime createdDate = LocalDateTime.now().plusSeconds(utcTime.getOffset().getTotalSeconds());
        String createdBy = User.getInstance().getUserName();
        LocalDateTime lastUpdate =  LocalDateTime.now().plusSeconds(utcTime.getOffset().getTotalSeconds());
        String lastUpdatedBy = User.getInstance().getUserName();
        int customerID = ids.get(0);
        int contactID = ids.get(1);
        int userID = ids.get(2);

        return String.format( "insert into appointments (Appointment_ID, Title, Description, Location, " +
                        "Type, Start, End, Create_Date, Created_By, Last_Update," +
                        "Last_Updated_By, Customer_ID, User_ID, Contact_ID)" +
                        "values(%d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"," +
                        "\"%s\", \"%s\", \"%s\", \"%s\", %d, %d, %d )", appointmentID, title,
                description, location, type, start, end, createdDate,
                createdBy, lastUpdate, lastUpdatedBy,
                customerID, userID, contactID);

    }

    /**
     * checks that the customer name has been provided.
     * @return false if there is an error true otherwise.
     */
    private Boolean checkFields(){
        if(cusNameBox.getValue() == null || contactBox.getValue() == null){
            Alert error = new Alert(Alert.AlertType.ERROR);
            String frenchMessage =  cusNameBox.getValue() == null ? "Veuillez sélectionner un client.":"Veuillez sélectionner un contact.";
            String englishMessage =  cusNameBox.getValue() == null ? "Please select a customer." : "Please select a contact.";
            error.setTitle(User.getInstance().getSystemLanguage().equals("en") ?  "Submission Error" : "Erreur de soumission");
            error.setContentText(User.getInstance().getSystemLanguage().equals("en") ? englishMessage : frenchMessage);
            error.show();
            return true;
        }else{
            return false;
        }

    }

    /**
     * checks that the appointment being created does not conflict with the customers
     * other appointments and that the appointment is within business hours.
     * @return false if there is a conflict true otherwise.
     */
    private Boolean checkTime() {
        dbUtils.establishConnection();
        ZoneOffset cooperateOffset = ZonedDateTime.of(LocalDateTime.now(ZoneId.of("US/Eastern")), ZoneId.of("US/Eastern")).getOffset();
        LocalTime businessStart = LocalTime.of(8, 00).plusSeconds(cooperateOffset.getTotalSeconds());
        LocalTime businessEnd = LocalTime.of(22, 00).plusSeconds(cooperateOffset.getTotalSeconds());

        LocalDate enteredDate = LocalDate.of(datePic.getValue().getYear(), datePic.getValue().getMonth(), datePic.getValue().getDayOfMonth());
        LocalDateTime enteredStart = LocalDateTime.of(enteredDate, LocalTime.of(startSpinnerHr.getValue(), startSpinnerMin.getValue()));
        LocalDateTime enteredEnd = LocalDateTime.of(enteredDate, LocalTime.of(endSpinnerHr.getValue(), endSpinnerMin.getValue()));
        DayOfWeek appointmentDay = datePic.getValue().getDayOfWeek();

        try {

            ResultSet customerIdRes = dbUtils.connStatement.executeQuery(String.format("select Customer_ID from customers where" +
                    " Customer_Name = \"%s\"", cusNameBox.getValue()));
            customerIdRes.next();
            int customerID = customerIdRes.getInt("Customer_ID");
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
                    return true;
                }
            }
            customerAppointmentsRes.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if(appointmentDay.equals(DayOfWeek.SATURDAY) || appointmentDay.equals(DayOfWeek.SUNDAY)) {
            Alert dayError = new Alert(Alert.AlertType.ERROR);
            dayError.setTitle("WeekEnd Selected");
            dayError.setContentText("The appointment cannot be scheduled on saturday or sunday.");
            dayError.showAndWait();
            return true;
        }else if (LocalTime.of(enteredStart.getHour(), enteredStart.getMinute()).isBefore(businessStart) ||
                    LocalTime.of(enteredEnd.getHour(), enteredEnd.getMinute()).isAfter(businessEnd)) {

            Alert scheduleError = new Alert(Alert.AlertType.ERROR);
            scheduleError.setTitle("Outside Business Hours");
            scheduleError.setContentText(String.format("You have selected a time outside of business hours. " +
                                                        "Business hours for your time zone are from: %s to:%s" +
                                                        "Yes it would be smarter to make these times unavailable, but that wasn't the assignment. ", businessStart, businessEnd));
            scheduleError.showAndWait();
            return true;
        }else if(enteredStart.isAfter(enteredEnd) || enteredEnd.isBefore(enteredStart)){
            Alert timeMaths = new Alert(Alert.AlertType.ERROR);
            timeMaths.setTitle("Scheduling Error");
            timeMaths.setContentText(enteredStart.isAfter(enteredEnd) ? "Start time is after end time." : "End time is after start time.");
            timeMaths.showAndWait();
            return true;
        }else{
            return false;
        }
    }

    /**
     * IMPLEMENTS ADVANCED ERROR HANDLING
     *
     * Gets the required IDs to place in the appointment.
     * @return Observable list of ids in the following order :
     * customer, contact, user
     */
    private ObservableList<Integer> getIds(){
        dbUtils.establishConnection();
        ObservableList<Integer> ids = FXCollections.observableArrayList();
        try{
            ObservableList<ObservableList<String>> requests = FXCollections.observableArrayList(
                    FXCollections.observableArrayList(cusNameBox.getValue().toString(),"Customer_Name", "customers", "Customer_ID"),
                    FXCollections.observableArrayList(contactBox.getValue().toString(),"Contact_Name", "contacts", "Contact_ID"),
                    FXCollections.observableArrayList(User.getInstance().getUserName(),"User_Name", "users", "User_ID" )
            );
            for(int i =0; i < requests.size(); i++){
                ResultSet id = dbUtils.connStatement.executeQuery(String.format("select %s from %s where %s = \"%s\"", requests.get(i).get(3),
                        requests.get(i).get(2),requests.get(i).get(1), requests.get(i).get(0)));
                id.next();
                ids.add(id.getInt(requests.get(i).get(3)));
            }
            return ids;

        }catch(SQLException | IndexOutOfBoundsException e){
            System.out.println(e.getMessage());
        }
        return ids;
    }

    /**
     * closes the add appointment window.
     */
    public void cancelAdd(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
