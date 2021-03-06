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
import scheduling.demoschedulingapp.Classes.Customer;
import scheduling.demoschedulingapp.Classes.User;
import scheduling.demoschedulingapp.SchedulingApplication;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;


/**
 * controls customer tab
 * loads customer data from db.
 * controls Add, delete, Edit functionality for customers.
 */
public class ManageCustomersController {

    @FXML
    TableView<Customer> customer_table;
    @FXML
    TableColumn<Customer, String> customer_id, name, create_date, phone, lastUpdate;
    @FXML
    Button add_customer, edit_customer, delete_customer;

    private static ObservableList<Customer> customers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setLanguage();
        customer_id.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        create_date.setCellValueFactory(new PropertyValueFactory<>("create_date"));
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        lastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        buildCustomerList();
        fillTable();
    }

    /**
     * refreshes the table view.
     */
    public void refreshListClick(){
        buildCustomerList();
        fillTable();
    }

    /**
     * sets language to french if it detects the host computer is using french.
     * contains lambda.
     */
    private void setLanguage(){
        HashMap<Button, String> frenchBtns= new HashMap<>(){
            {
                put(add_customer, "Ajouter un client");
                put(edit_customer, "??diter un client");
                put(delete_customer, "effacer un client");
            }
        };
        HashMap<TableColumn<Customer, String>, String> frenchLables = new HashMap<>(){
            {
                put(customer_id, "N ?? de client");
                put(name,"Nom");
                put(create_date,"Date cr????e");
                put(phone, "T??l??phone");
                put(lastUpdate, "Derni??re mise ?? jour");
            }
        };

        if(User.getInstance().getSystemLanguage().equals("fr")){
            frenchBtns.forEach((k,v)-> k.setText(v));
            frenchLables.forEach((k,v) -> k.setText(v));
        }
    }

    /**
     * Builds and refreshes the customer table being displayed.
     */
    public static void buildCustomerList() {

        try {
            dbUtils.establishConnection();
            customers.clear();
            ResultSet answer = dbUtils.connStatement.executeQuery("select * from customers");
            while (answer.next()) {
                String cusID = answer.getString("Customer_ID");
                String name = answer.getString("Customer_Name");
                String address = answer.getString("Address");
                String zip = answer.getString("Postal_Code");
                String phone = answer.getString("Phone");
                String create_date = answer.getString("Create_Date");
                String createdBy = answer.getString("Created_By");
                String lastUpdate = answer.getString("Last_Update");
                String lastUpdatedBy = answer.getString("Last_Update");
                int divisionID  = answer.getInt("Division_ID");

                Customer customer = new Customer( cusID,  name,  address,  zip, phone,  create_date,
                        createdBy,  lastUpdate,  lastUpdatedBy, divisionID);
                customers.add(customer);
            }
            dbUtils.connStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * fills the customer table with customers in database.
     */
    private void fillTable() {
        try {
            customer_table.setItems(customers);

        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * opens the add customer form.
     */
    public void openAddCustomer() {
        try {
            Parent loader = FXMLLoader.load(SchedulingApplication.class.getResource("addCustomer.fxml"));
            Stage addWindow = new Stage();
            addWindow.setScene(new Scene(loader));
            addWindow.setTitle("Add Customer");
            addWindow.initModality(Modality.APPLICATION_MODAL);
            addWindow.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * opens add customer form in edit mode.
     */
    public void openEditCustomer() {
        Customer selectedCustomer = customer_table.getSelectionModel().getSelectedItem();
        if(selectedCustomer == null){
            String messageEnglish = "Please select a customer to edit.";
            String messageFrench = "Veuillez s??lectionner un client ?? modifier.";
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle(User.getInstance().getSystemLanguage().equals("en") ? "No Customer Selected." : "Aucun client s??lectionn??.");
            noneSelected.setContentText(User.getInstance().getSystemLanguage().equals("en") ? messageEnglish : messageFrench);
            noneSelected.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(SchedulingApplication.class.getResource("addCustomer.fxml"));
            Parent root = loader.load();
            addCustomerController controller =  loader.getController();
            controller.makeEdit(selectedCustomer);

            Stage addWindow = new Stage();
            addWindow.setScene(new Scene(root));
            addWindow.setTitle("Edit Customer");
            addWindow.initModality(Modality.APPLICATION_MODAL);
            addWindow.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Deletes the selected customer and all associated Appointments.
     */
    public void deleteCustomer() {
        Customer selectedCustomer = customer_table.getSelectionModel().getSelectedItem();
        if(selectedCustomer == null){
            String messageEnglish = "Please select a customer to Delete.";
            String messageFrench = "Veuillez s??lectionner un client ?? Effacer.";
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle(User.getInstance().getSystemLanguage().equals("en") ? "No Customer Selected." : "Aucun client s??lectionn??.");
            noneSelected.setContentText(User.getInstance().getSystemLanguage().equals("en") ? messageEnglish : messageFrench);
            noneSelected.showAndWait();
            return;
        }

        String messageEnglish = "Customer will be deleted as well as any appointments associated with this customer. Do you wish to proceed?";
        String messageFrench = "Le client sera supprim?? ainsi que tous les rendez-vous associ??s ?? ce client. Voulez-vous continuer?";
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle(User.getInstance().getSystemLanguage().equals("en") ? "Delete Customer" : "Supprimer le client");
        confirmation.setContentText(User.getInstance().getSystemLanguage().equals("en")  ? messageEnglish : messageFrench);
        Optional<ButtonType> confirmed = confirmation.showAndWait();

        if(confirmed.isPresent() && confirmed.get() == ButtonType.OK){
            try{
                ManageAppointmentsController.deleteAssociatedAppointments(selectedCustomer);
                dbUtils.establishConnection();
                dbUtils.connStatement.execute(String.format("delete from customers where Customer_ID = %d", Integer.parseInt(selectedCustomer.getCustomer_id())));
                buildCustomerList();
                fillTable();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

    }


}
