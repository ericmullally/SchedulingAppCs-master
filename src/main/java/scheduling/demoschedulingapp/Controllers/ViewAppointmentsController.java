package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import scheduling.demoschedulingapp.Classes.Appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class ViewAppointmentsController {

    @FXML
    RadioButton viewAll, viewMonth,viewWeek;
    @FXML
    TableView<Appointment> viewAppTable;
    @FXML
    TableColumn<Appointment, String> appointmentIDCol, titleCol, descriptCol, startCol, contCol,
                                        endCol, locCol, cusIdCol, userIdCol, typeCol;

    private static ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startString"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endString"));
        locCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        cusIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        contCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        viewAll.selectedProperty().setValue(true);
        buildList();
        setView("viewAll");
    }

    /**
     * refreshes the table of appointments and sets the view all radio button to selected.
     */
    public void refreshBtnClick(){
        viewAll.selectedProperty().setValue(true);
        buildList();
        setView("viewAll");
    }

    /**
     * decides which radio button was selected.
     * then tells setView what to send filterView.
     * @param e the radio button being selected.
     */
    public void handleChange(ActionEvent e){
        String buttonName =  ((RadioButton) e.getTarget()).getId();
        setView(buttonName);
    }

    /**
     * Tells filterView What to set.
     * not Super necessary but filterView is messy
     * enough without the extra logic.
     * @param btnName Name of the button that was selected.
     */
    public void setView(String btnName){
        switch(btnName){
            case "viewAll":
            case "refreshBtn":
                filterView("all");
                break;
            case "viewMonth":
                filterView("month");
                break;
            case "viewWeek":
                filterView("week");
                break;
            default:
                System.out.println("you got the wrong name. in ViewAppointments setView");
        }

    }

    /**
     * gets all the Appointments from the database.
     * Then puts them in a list.
     */
    public static void buildList(){
        try {
            appointments.clear();
            dbUtils.establishConnection();
            ResultSet answer = dbUtils.connStatement.executeQuery("select * from appointments");
            while (answer.next()) {
                int appointmentID = answer.getInt("Appointment_ID");
                String title = answer.getString("Title");
                String description = answer.getString("Description");
                String location = answer.getString("Location");
                String type = answer.getString("Type");
                String start = answer.getString("Start");
                String end = answer.getString("End");
                String createDate = answer.getString("Create_Date");
                String createdBy = answer.getString("Created_By");
                String lastUpdate = answer.getString("Last_Update");
                String lastUpdatedBy = answer.getString("Last_Updated_By");
                int customerID = answer.getInt("Customer_ID");
                int userID = answer.getInt("User_ID");
                int contactID = answer.getInt("Contact_ID");

                Appointment appointment = new Appointment(appointmentID, title, description, location, type, start, end,
                        createDate, createdBy, lastUpdate, lastUpdatedBy, customerID, userID, contactID);

                appointments.add(appointment);
            }
            dbUtils.connStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * CONTAINS STREAM AND FILTER.
     * filters the appointments list and then displays  the in the table.
     * @param timeFrame
     */
    private void filterView(String timeFrame){
        viewAppTable.getItems().clear();
        buildList();
        ZonedDateTime thisDate = ZonedDateTime.now();
        Month thisMonth = thisDate.getMonth();

        if(timeFrame == "month"){
            List<Appointment> monthList = appointments.stream().filter(a-> a.getStart().getMonth() == thisMonth  ).collect(Collectors.toList());
            if(monthList.size() > 0){
                viewAppTable.getItems().addAll( monthList);
            }else{
                Label placeHolderLbl = new Label("No Appointments this month.");
                placeHolderLbl.setTextFill(Color.WHITE);
                viewAppTable.setPlaceholder(placeHolderLbl);
            }

        }else if(timeFrame == "week"){
            List<Appointment> weekList = appointments.stream().filter(a-> a.getStart().getDayOfMonth() >= thisDate.getDayOfMonth() &&
                                                                          a.getStart().getDayOfMonth() <= thisDate.plusDays(7).getDayOfMonth()).collect(Collectors.toList());
            if (weekList.size() > 0) {
                viewAppTable.getItems().addAll(weekList);
            } else {
                Label placeHolderLbl = new Label("No Appointments this week");
                placeHolderLbl.setTextFill(Color.WHITE);
                viewAppTable.setPlaceholder(placeHolderLbl);
            }

        }else{
            if(appointments.size() >= 1){
                viewAppTable.getItems().addAll(appointments);
            }else{
                Label placeHolderLbl = new Label("No Appointments Available");
                placeHolderLbl.setTextFill(Color.WHITE);
                viewAppTable.setPlaceholder(placeHolderLbl);
            }

        }
    }


}
