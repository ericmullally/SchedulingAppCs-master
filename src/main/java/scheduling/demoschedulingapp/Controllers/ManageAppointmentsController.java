package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
               String lastUpdate = answer.getString("Last_Updated");
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

}
