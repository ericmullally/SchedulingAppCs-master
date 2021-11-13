package scheduling.demoschedulingapp.Controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduling.demoschedulingapp.Classes.Customer;
import scheduling.demoschedulingapp.SchedulingApplication;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    ObservableList<Customer> customers = FXCollections.observableArrayList();
    dbUtils connection = new dbUtils();
    Statement connector = connection.connStatement;

    @FXML
    public void initialize() {
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
     * NEED TO SEE WHAT HAPPENS IF an entry is missing
     */
    private void buildCustomerList() {

        try {
            ResultSet answer = connector.executeQuery("select * from customers");
            while (answer.next()) {
                String cusID = answer.getString("Customer_ID");
                String name = answer.getString("Customer_Name");
                String create_date = answer.getString("Create_Date");
                String phone = answer.getString("Phone");
                String lastUpdate = answer.getString("Last_Update");
                Customer customer = new Customer(cusID, name, create_date, phone, lastUpdate);
                customers.add(customer);
            }
            connector.close();
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
            SchedulingApplication.showNewWindow("addCustomer.fxml", "Add Customer");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void openEditCustomer() {

    }

    public void deleteCustomer() {

    }


}
