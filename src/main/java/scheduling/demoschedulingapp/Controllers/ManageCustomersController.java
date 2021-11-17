package scheduling.demoschedulingapp.Controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import java.util.Locale;


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
        dbUtils.establishConnection();

        setLanguage();
        customer_id.setCellValueFactory(new PropertyValueFactory<Customer, String>("customer_id"));
        name.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        create_date.setCellValueFactory(new PropertyValueFactory<Customer, String>("create_date"));
        phone.setCellValueFactory(new PropertyValueFactory<Customer, String>("phone"));
        lastUpdate.setCellValueFactory(new PropertyValueFactory<Customer, String>("lastUpdate"));
        buildCustomerList();
        fillTable();
    }

    /**
     * sets language to french if it detects the host computer is using french.
     * contains lambda.
     */
    private void setLanguage(){
        HashMap<Button, String> frenchBtns= new HashMap<Button, String>(){
            {
                put(add_customer, "Ajouter un client");
                put(edit_customer, "Éditer un client");
                put(delete_customer, "effacer un client");
            }
        };
        HashMap<TableColumn<Customer, String>, String> frenchLables = new HashMap<TableColumn<Customer, String>, String>(){
            {
                put(customer_id, "N ° de client");
                put(name,"Nom");
                put(create_date,"Date créée");
                put(phone, "Téléphone");
                put(lastUpdate, "Dernière mise à jour");
            }
        };

        if(Locale.getDefault().getLanguage() == "fr"){
            frenchBtns.forEach((k,v)-> k.setText(v));
            frenchLables.forEach((k,v) -> k.setText(v));
        }
    }

    /**
     * Builds and refreshes the customer table being displayed.
     */
    public static void buildCustomerList() {

        try {
            customers.clear();
            ResultSet answer = dbUtils.connStatement.executeQuery("select * from customers");
            while (answer.next()) {
                String cusID = answer.getString("Customer_ID");
                String name = answer.getString("Customer_Name");
                String create_date = answer.getString("Create_Date");
                String phone = answer.getString("Phone");
                String lastUpdate = answer.getString("Last_Update");
                Customer customer = new Customer(cusID, name, create_date, phone, lastUpdate);
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
     *
     * @throws IOException
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
     * Opens a new window on top of the original application.
     * @throws IOException
     */
    @FXML
    public void openEditCustomer() {
        Customer selectedCustomer = customer_table.getSelectionModel().getSelectedItem();
        if(selectedCustomer == null){
            String messageEnglish = "Please select a customer to edit.";
            String messageFrench = "Veuillez sélectionner un client à modifier.";
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle(User.getInstance().getSystemLanguage() == "en" ? "No Customer Selected." : "Aucun client sélectionné.");
            noneSelected.setContentText(User.getInstance().getSystemLanguage() == "en" ? messageEnglish : messageFrench);
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
            addWindow.setTitle("Add Customer");
            addWindow.initModality(Modality.APPLICATION_MODAL);
            addWindow.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteCustomer() {

    }


}
