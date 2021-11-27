package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduling.demoschedulingapp.Classes.Appointment;
import scheduling.demoschedulingapp.Classes.Customer;
import scheduling.demoschedulingapp.Classes.User;
import scheduling.demoschedulingapp.SchedulingApplication;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;


public class ManageAppointmentsController {

    @FXML
    TableView<Appointment> appointmentTable;
    @FXML
    TableColumn<Appointment, String> appointmentIdCol, titleCol, descriptionCol, startCol,
                                        endCol, locationCol, customerIdCol, contactIDCol;
    @FXML
    Button addAppointmentBtn, editAppointmentBtn, deleteAppointmentBtn;

    private static ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        setLanguage();
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("end"));
        locationCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("customerID"));
        contactIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactID"));
        buildAppointmentList();
        fillTable();
    }

    /**
     * sets language to french if it detects the host computer is using french.
     * contains lambda.
     */
    private void setLanguage(){
        HashMap<Button, String> frenchBtns= new HashMap<Button, String>(){
            {
                put(addAppointmentBtn, "Ajouter un rendez-vous");
                put(editAppointmentBtn, "Éditer un rendez-vous");
                put(deleteAppointmentBtn, "effacer un rendez-vous");
            }
        };
        HashMap<TableColumn<Appointment, String>, String> frenchLables = new HashMap<TableColumn<Appointment, String>, String>(){
            {
                put(appointmentIdCol, "identifiant de rendez-vous");
                put(titleCol,"Titre");
                put(descriptionCol,"la description");
                put(startCol, "début");
                put(endCol, "finir");
                put(locationCol, "emplacement");
                put(customerIdCol, "N ° de client");
                put(contactIDCol, "identifiant de contact");
            }
        };

        if(User.getInstance().getSystemLanguage().equals("fr")){
            frenchBtns.forEach((k,v)-> k.setText(v));
            frenchLables.forEach((k,v) -> k.setText(v));
        }
    }

    /**
     * Builds and refreshes the Appointments table being displayed.
     */
    public static void buildAppointmentList(){
        try {
            dbUtils.establishConnection();
            appointments.clear();
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
     * fills the appointment table with the appointments in appointments hashTable.
     */
    private void fillTable() {
        try {
            appointmentTable.setItems(appointments);

        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Opens the add Appointment window.
     */
    public void openAddAppointment(){
        try {
            String title = User.getInstance().getSystemLanguage().equals("en") ? "Add Appointment" : "Ajouter un rendez-vous";
            Parent loader = FXMLLoader.load(SchedulingApplication.class.getResource("addAppointment.fxml"));
            Stage addWindow = new Stage();
            addWindow.setScene(new Scene(loader));
            addWindow.setTitle(title);
            addWindow.initModality(Modality.APPLICATION_MODAL);
            addWindow.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Opens the add appointment form in edit mode.
     */
    public void openEditAppointments() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if(selectedAppointment == null){
            String messageEnglish = "Please select an Appointment to edit.";
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle( "No Appointment Selected.");
            noneSelected.setContentText(messageEnglish);
            noneSelected.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(SchedulingApplication.class.getResource("addAppointment.fxml"));
            Parent root = loader.load();
            AddAppointmentController controller =  loader.getController();
            controller.makeEdit(selectedAppointment);

            Stage addWindow = new Stage();
            addWindow.setScene(new Scene(root));
            addWindow.setTitle("Edit Appointment");
            addWindow.initModality(Modality.APPLICATION_MODAL);
            addWindow.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes the selected appointment from the database.
     */
    public void deleteAppointment(){
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if(selectedAppointment == null){
            String messageEnglish = "Please select an Appointment to delete.";
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle( "No Appointment Selected.");
            noneSelected.setContentText(messageEnglish);
            noneSelected.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText(String.format("Are you sure you want to delete appointment: %s", selectedAppointment.getTitle()));
        Optional<ButtonType> response = confirm.showAndWait();

        if(response.isPresent() && response.equals(ButtonType.OK)){
            dbUtils.establishConnection();
            try{
                dbUtils.connStatement.execute(String.format("delete from appointments where Appointment_ID = %d", selectedAppointment.getAppointmentID()));
                dbUtils.connStatement.close();
                buildAppointmentList();
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }


    }

    /**
     * Deletes all appointments associated with the customer that is passed in.
     * @param customer customer who's appointments are to  be removed.
     */
    public static void deleteAssociatedAppointments(Customer customer){
        dbUtils.establishConnection();
        try{
            dbUtils.connStatement.execute(String.format("delete from appointments where Customer_ID = %d", customer.getCustomer_id()));
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
