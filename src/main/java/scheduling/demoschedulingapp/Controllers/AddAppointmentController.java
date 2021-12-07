package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.Stage;
import scheduling.demoschedulingapp.Classes.Appointment;
import scheduling.demoschedulingapp.Classes.TimeConversion;
import scheduling.demoschedulingapp.Classes.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.HashMap;

/**
 * controls the add appointment form.
 */
public class AddAppointmentController {

    //region form items
    @FXML
    TextField appointmentIdTxt, titleTxt, locationTxt;
    @FXML
    ChoiceBox cusNameBox, contactBox, typeBox;
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
    //endregion
    int appointmentID;
    Boolean isEdit = false;

    @FXML
    public void initialize(){
        setLanguage();
        setAppointmentId();
        setCustomerNamesBox();
        setTypeBox();
        setTimeBoxes();
        setContactBox();
        appointmentIdTxt.setText(String.valueOf(appointmentID));
    }

    /**
     * changes this controller to function as an appointment editor.
     * @param app the Appointment to edit.
     */
    public void makeEdit(Appointment app){
        isEdit = true;
        appointmentID = app.getAppointmentID();

        int sHour = app.getStart().getHour();
        int sMin = app.getStart().getMinute();
        int eHour = app.getEnd().getHour();
        int eMin = app.getEnd().getMinute();

        titleTxt.setText(app.getTitle());
        descTxt.setText(app.getDescription());
        cusNameBox.getSelectionModel().select(getName("Customer_Name", "customers", "Customer_ID", app.getCustomerID()));
        contactBox.getSelectionModel().select(getName("Contact_Name", "contacts", "Contact_ID", app.getContactID()));
        locationTxt.setText(app.getLocation());
        typeBox.getSelectionModel().select(app.getType());
        datePic.setValue(app.getStart().toLocalDate());
        setTimeBoxes(sHour, sMin, eHour, eMin);

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
        SpinnerValueFactory<Integer> startHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
        SpinnerValueFactory<Integer> startMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        SpinnerValueFactory<Integer> endHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
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
     * sets value factory for all time spinners and uses filter
     * on event handler to ensure only numbers are set.
     * @param sHour the hour to initialize the start hour spinner to.
     * @param sMin the minute to initialize the start minute spinner to.
     * @param eHour the hour to initialize the end hour to.
     * @param eMin the hour to initialize the end minute to.
     */
    private void setTimeBoxes(int sHour, int sMin, int eHour, int eMin){
        SpinnerValueFactory<Integer> startHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24);
        SpinnerValueFactory<Integer> startMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        SpinnerValueFactory<Integer> endHours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24);
        SpinnerValueFactory<Integer> endMinutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        startHours.setValue(sHour);
        startMinutes.setValue(sMin);
        endHours.setValue(eHour);
        endMinutes.setValue(eMin);

        startSpinnerHr.setValueFactory(startHours);
        startSpinnerMin.setValueFactory(startMinutes);
        endSpinnerHr.setValueFactory(endHours);
        endSpinnerMin.setValueFactory(endMinutes);
        String numberPattern = "\\d*";

        ObservableList<Spinner> spinners = FXCollections.observableArrayList(startSpinnerHr, startSpinnerMin, endSpinnerHr, endSpinnerMin);

        //adds a listener to each spinner making sure the data entered is am appropriate time
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
     * sets the contact dropdown box.
     */
    private void setTypeBox(){
        ObservableList<String> types = FXCollections.observableArrayList("Stand up", "Sit Down", "Scrum", "Interview");
        types.forEach(item -> typeBox.getItems().add(item));
    }

    /**
     * adds the appointment to the database.
     */
    public void createAppointment(){
        if(checkFields() || checkTime() || checkBusinessHrs() || checkForOverlap()){
            return;
        }

        String createStatement = buildRequestString();
        String title = titleTxt.getText();

        try{
            dbUtils.connStatement.execute(createStatement);
            dbUtils.connStatement.close();
            Alert success = new Alert(Alert.AlertType.CONFIRMATION);
            success.setTitle("Success!");
            String message = isEdit ? String.format("Appointment %s has been updated.", title) : String.format("Appointment %s has been added to the schedule.", title);
            success.setContentText(message);
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
     * <p>builds the string for the make request function to use.</p>
     * <p><p>adds appointmentID, title,description, location, type, start, end, createdDate,createdBy, lastUpdate, lastUpdatedBy,customerID, userID, contactID;
     * this includes the user id as required in the task: </p>
     * <p>A3a bullet 4: "When adding and updating an appointment, record the following data: Appointment_ID, title, description, location, contact, type,
     * start date and time, end date and time, Customer_ID, and User_ID."</p></p>
     * @return a string with the database command.
     */
    private String buildRequestString(){
        ZonedDateTime businessStart = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.of(datePic.getValue(), LocalTime.of(8,0)), ZoneId.of("US/Eastern")), User.getInstance().getUserTimeZone()) ;
        ZonedDateTime businessEnd = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.of(datePic.getValue(), LocalTime.of(22,0)), ZoneId.of("US/Eastern")), User.getInstance().getUserTimeZone()) ;
        Boolean changeDays = businessStart.getDayOfMonth() != businessEnd.getDayOfMonth() && startSpinnerHr.getValue() > endSpinnerHr.getValue() ;

        LocalDateTime localStartTime = LocalDateTime.of(datePic.getValue(), LocalTime.of(startSpinnerHr.getValue() , startSpinnerMin.getValue()));
        LocalDateTime localEndTime = LocalDateTime.of( changeDays ? datePic.getValue().plusDays(1) : datePic.getValue() , LocalTime.of(endSpinnerHr.getValue(), endSpinnerMin.getValue()));

        ZonedDateTime zonedStart = TimeConversion.convertTimes( ZonedDateTime.of(localStartTime, ZoneId.of(User.getInstance().getUserTimeZone().toString())), ZoneId.of("UTC"));
        ZonedDateTime zonedEnd = TimeConversion.convertTimes( ZonedDateTime.of(localEndTime, ZoneId.of(User.getInstance().getUserTimeZone().toString())), ZoneId.of("UTC"));;
        ZonedDateTime zoneNow = TimeConversion.convertTimes(ZonedDateTime.now(), ZoneId.of("UTC"));

        ObservableList<Integer> ids = getIds();
        String title = titleTxt.getText();
        String description = descTxt.getText();
        String location = locationTxt.getText();
        String type = typeBox.getValue().toString();
        LocalDateTime start = LocalDateTime.of(LocalDate.of(zonedStart.getYear(), zonedStart.getMonth(), zonedStart.getDayOfMonth()), LocalTime.of(zonedStart.getHour(), zonedStart.getMinute()));
        LocalDateTime end =  LocalDateTime.of(LocalDate.of(zonedEnd.getYear(), zonedEnd.getMonth(), zonedEnd.getDayOfMonth()), LocalTime.of(zonedEnd.getHour(), zonedEnd.getMinute()));

        LocalDateTime createdDate = LocalDateTime.of(LocalDate.of(zoneNow.getYear(), zoneNow.getMonth(), zoneNow.getDayOfMonth()), LocalTime.of(zoneNow.getHour(), zoneNow.getMinute()));
        String createdBy = User.getInstance().getUserName();
        LocalDateTime lastUpdate =  LocalDateTime.of(LocalDate.of(zoneNow.getYear(), zoneNow.getMonth(), zoneNow.getDayOfMonth()), LocalTime.of(zoneNow.getHour(), zoneNow.getMinute()));;
        String lastUpdatedBy = User.getInstance().getUserName();
        int customerID = ids.get(0);
        int contactID = ids.get(1);
        int userID = ids.get(2);



        if(!isEdit){
            return String.format( "insert into appointments (Appointment_ID, Title, Description, Location, " +
                            "Type, Start, End, Create_Date, Created_By, Last_Update," +
                            "Last_Updated_By, Customer_ID, User_ID, Contact_ID)" +
                            "values(%d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"," +
                            "\"%s\", \"%s\", \"%s\", \"%s\", %d, %d, %d )", appointmentID, title,
                    description, location, type, start, end, createdDate,
                    createdBy, lastUpdate, lastUpdatedBy,
                    customerID, userID, contactID);
        }else{
            return String.format( "update  appointments set  Title =\"%s\", Description =\"%s\", Location =\"%s\", " +
                            "Type = \"%s\", Start =\"%s\", End =\"%s\", Last_Update =\"%s\"," +
                            "Last_Updated_By = \"%s\", Customer_ID = %d, User_ID = %d, Contact_ID = %d where Appointment_ID = %d", title,
                            description, location, type, start, end, lastUpdate, lastUpdatedBy, customerID, userID, contactID, appointmentID);
        }


    }

    /**
     * checks that the customer name, a contact, and a type
     * has been provided.
     * @return true if there is an error false otherwise.
     */
    private Boolean checkFields(){
        if(cusNameBox.getValue() == null || contactBox.getValue() == null || typeBox.getValue() == null){
            String message = "";
            if(cusNameBox.getValue() == null ){
                message = "Please Select a customer.";
            }else if(contactBox.getValue() == null){
                message = "Please select a contact.";
            }else{
                message = "Please select a type.";
            }
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Submission Error" );
            error.setContentText(message);
            error.show();
            return true;
        }else{
            return false;
        }

    }

    /**
     * first checks that the appointment date is not in the past.
     * then proceeds to check the appointment start and end times make sense
     * @return true if there is a conflict false otherwise.
     */
    private Boolean checkTime() {
        ZonedDateTime today = ZonedDateTime.now();
        ZonedDateTime businessStart = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.of(datePic.getValue(), LocalTime.of(8,0)), ZoneId.of("US/Eastern")), User.getInstance().getUserTimeZone()) ;
        ZonedDateTime businessEnd = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.of(datePic.getValue(), LocalTime.of(22,0)), ZoneId.of("US/Eastern")), User.getInstance().getUserTimeZone()) ;
        Boolean changeDays = businessStart.getDayOfMonth() != businessEnd.getDayOfMonth() && startSpinnerHr.getValue() > endSpinnerHr.getValue() ;

        LocalDate enteredDate = LocalDate.of(datePic.getValue().getYear(), datePic.getValue().getMonth(), datePic.getValue().getDayOfMonth());
        LocalDateTime enteredStart = LocalDateTime.of(enteredDate, LocalTime.of(startSpinnerHr.getValue() , startSpinnerMin.getValue())); // needed a way to check if the time entered was for midnight
        LocalDateTime enteredEnd = LocalDateTime.of( changeDays ? enteredDate.plusDays(1) : enteredDate   , LocalTime.of(endSpinnerHr.getValue(), endSpinnerMin.getValue())); //if the end is at midnight due to user timezone constraint we need to set the end date to the next day.

        DayOfWeek appointmentDay = datePic.getValue().getDayOfWeek();

        if(today.isAfter(ZonedDateTime.of(enteredStart, ZoneId.of(User.getInstance().getUserTimeZone().toString())))){
            Alert dayError = new Alert(Alert.AlertType.ERROR);
            dayError.setContentText("Incorrect Day");
            dayError.setContentText("The day you have selected has already passed.");
            dayError.show();
            return true;
        }else if(appointmentDay.equals(DayOfWeek.SATURDAY) || appointmentDay.equals(DayOfWeek.SUNDAY)) {
            Alert dayError = new Alert(Alert.AlertType.ERROR);
            dayError.setTitle("WeekEnd Selected");
            dayError.setContentText("The appointment cannot be scheduled on saturday or sunday.");
            dayError.showAndWait();
            return true;
        }else if(enteredStart.isAfter(enteredEnd) || enteredEnd.isBefore(enteredStart)) {
            Alert timeMaths = new Alert(Alert.AlertType.ERROR);
            timeMaths.setTitle("Scheduling Error");
            timeMaths.setContentText(enteredStart.isAfter(enteredEnd) ? "Start time is after end time." : "End time is after start time.");
            timeMaths.showAndWait();
            return true;
        }else if(enteredStart.isEqual(enteredEnd)){
            Alert sameTimes = new Alert(Alert.AlertType.ERROR);
            sameTimes.setTitle("Time Error");
            sameTimes.setContentText("The start and end times cannot be the same.");
            sameTimes.showAndWait();
            return true;

        }else{
            return false;
        }
    }

    /**
     * checks that the customer doesn't already have an appointment at this time.
     * @return true if they do false if not
     */
    private Boolean checkForOverlap(){
        dbUtils.establishConnection();
        ZonedDateTime businessStart = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.of(datePic.getValue(), LocalTime.of(8,0)), ZoneId.of("US/Eastern")), User.getInstance().getUserTimeZone()) ;
        ZonedDateTime businessEnd = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.of(datePic.getValue(), LocalTime.of(22,0)), ZoneId.of("US/Eastern")), User.getInstance().getUserTimeZone()) ;
        Boolean changeDays = businessStart.getDayOfMonth() != businessEnd.getDayOfMonth() && startSpinnerHr.getValue() > endSpinnerHr.getValue() ; // if the user is in a time zone that requires appointments

        LocalDate enteredDate = LocalDate.of(datePic.getValue().getYear(), datePic.getValue().getMonth(), datePic.getValue().getDayOfMonth());
        LocalDateTime enteredStart = LocalDateTime.of(enteredDate, LocalTime.of(startSpinnerHr.getValue() == 24? 0 : startSpinnerHr.getValue(), startSpinnerMin.getValue()));
        LocalDateTime enteredEnd = LocalDateTime.of( changeDays ? enteredDate.plusDays(1) : enteredDate , LocalTime.of(endSpinnerHr.getValue(), endSpinnerMin.getValue()));

        ZonedDateTime zonedStart = ZonedDateTime.of(enteredStart, User.getInstance().getUserTimeZone());
        ZonedDateTime zonedEnd = ZonedDateTime.of(enteredEnd, User.getInstance().getUserTimeZone());
        try {

            ResultSet customerIdRes = dbUtils.connStatement.executeQuery(String.format("select Customer_ID from customers where" +
                    " Customer_Name = \"%s\"", cusNameBox.getValue()));
            customerIdRes.next();
            int customerID = customerIdRes.getInt("Customer_ID");
            ResultSet customerAppointmentsRes = dbUtils.connStatement.executeQuery(String.format("select Start, End, Appointment_ID from appointments " +
                    "where Customer_ID = %s", customerID));
            while (customerAppointmentsRes.next()) {
                ZonedDateTime start = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.parse(customerAppointmentsRes.getString("Start").replace(" ", "T")), ZoneId.of("UTC")), User.getInstance().getUserTimeZone());
                ZonedDateTime end = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.parse(customerAppointmentsRes.getString("End").replace(" ", "T")), ZoneId.of("UTC")), User.getInstance().getUserTimeZone());

                if(start.getDayOfYear() == zonedEnd.getDayOfYear() ){
                    Boolean startOverlap =  zonedStart.isBefore(end);
                    Boolean endOverlap = end.isAfter(zonedStart);
                    Boolean differentAppointment = customerAppointmentsRes.getInt("Appointment_ID") != appointmentID; // in case we are editing then we can allow this.

                    if (( startOverlap || endOverlap ) && differentAppointment) {
                        Alert scheduleError = new Alert(Alert.AlertType.ERROR);
                        scheduleError.setTitle("Conflicting appointment");
                        scheduleError.setContentText(String.format("%s Already has an appointment within this time frame.", cusNameBox.getValue()));
                        scheduleError.showAndWait();
                        return true;
                    }
                }
            }
            customerAppointmentsRes.close();
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Checks That the entered time falls within cooperate business hours.
     * @return true if it does not false if it does.
     */
    private Boolean checkBusinessHrs(){
        LocalDate enteredDate = LocalDate.of(datePic.getValue().getYear(), datePic.getValue().getMonth(), datePic.getValue().getDayOfMonth());
        LocalDateTime enteredStart = LocalDateTime.of(enteredDate, LocalTime.of(startSpinnerHr.getValue() == 24? 0 : startSpinnerHr.getValue(), startSpinnerMin.getValue()));
        LocalDateTime enteredEnd = LocalDateTime.of( endSpinnerHr.getValue() == 24 ?  enteredDate.plusDays(1) : enteredDate , LocalTime.of(endSpinnerHr.getValue() == 24? 0 : endSpinnerHr.getValue(), endSpinnerMin.getValue()));


        ZonedDateTime zonedStart = ZonedDateTime.of(enteredStart, User.getInstance().getUserTimeZone());
        ZonedDateTime zonedEnd = ZonedDateTime.of(enteredEnd, User.getInstance().getUserTimeZone());

        ZonedDateTime businessStart = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.of(enteredStart.toLocalDate(), LocalTime.of(8,0)), ZoneId.of("US/Eastern")), User.getInstance().getUserTimeZone()) ;
        ZonedDateTime businessEnd = TimeConversion.convertTimes(ZonedDateTime.of(LocalDateTime.of(enteredEnd.toLocalDate(), LocalTime.of(22,0)), ZoneId.of("US/Eastern")), User.getInstance().getUserTimeZone()) ;

        if(zonedStart.isBefore(businessStart) || zonedEnd.isAfter(businessEnd)){
            if(businessStart.getDayOfMonth() != businessEnd.getDayOfMonth()){
                String[] startStringList = businessStart.toString().split("T");
                String[] endStringList = businessEnd.toString().split("T");
                String businessStartString = startStringList[1].contains("-") ? startStringList[1].split("-")[0] : startStringList[1].split("\\+")[0] ;
                String businessEndString = endStringList[1].contains("-") ? endStringList[1].split("-")[0] : endStringList[1].split("\\+")[0] ;

                Alert scheduleError = new Alert(Alert.AlertType.ERROR);
                scheduleError.setTitle("Outside Business Hours");
                scheduleError.setContentText(String.format("You have selected a time outside of business hours. " +
                        "Business hours for your time zone are \nfrom: %s \nto: %s the following day.", businessStartString, businessEndString));
                scheduleError.showAndWait();
                return true;
            }else{
                String[] startStringList = businessStart.toString().split("T");
                String[] endStringList = businessEnd.toString().split("T");
                String businessStartString = startStringList[1].contains("-") ? startStringList[1].split("-")[0] : startStringList[1].split("\\+")[0] ;
                String businessEndString = endStringList[1].contains("-") ? endStringList[1].split("-")[0] : endStringList[1].split("\\+")[0] ;

                Alert scheduleError = new Alert(Alert.AlertType.ERROR);
                scheduleError.setTitle("Outside Business Hours");
                scheduleError.setContentText(String.format("You have selected a time outside of business hours. " +
                        "Business hours for your time zone are from: %s to:%s", businessStartString, businessEndString));
                scheduleError.showAndWait();
                return true;
            }
        }
        return false;
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

    /**
     * retrieves the name of the client, user, or contact
     * depending on the input.
     * @param columnName name of the column in the database table.(example "Customer_Name")
     * @param tableName name of the table to look in (example customers)
     * @param typeID the type of Id to use for comparison in the query (example "Customer_ID")
     * @param id the ID to Look for
     * @return String the name of the person owning the ID.
     */
    private String getName( String columnName, String tableName, String typeID, int id){
        dbUtils.establishConnection();
        String name = "";
        try{
            ResultSet nameRes = dbUtils.connStatement.executeQuery(String.format("select %s from %s where %s = %s", columnName, tableName, typeID, id));
            nameRes.next();
            name = nameRes.getString(columnName);
            nameRes.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return name;
    }
}
