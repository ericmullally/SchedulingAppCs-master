package scheduling.demoschedulingapp.Controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduling.demoschedulingapp.Classes.Customer;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



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
    ObservableList<Customer> customers = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        customer_id.setCellValueFactory(new PropertyValueFactory<Customer, String>("customer_id"));
        name.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        create_date.setCellValueFactory(new PropertyValueFactory<Customer, String>("create_date"));
        phone.setCellValueFactory(new PropertyValueFactory<Customer, String>("phone"));
        lastUpdate.setCellValueFactory(new PropertyValueFactory<Customer, String>("lastUpdate"));
        buildCustomerList();
        fillTable();
    }

    private void buildCustomerList() {
        dbUtils connection = new dbUtils();
        Statement connector = connection.connStatement;
        try{
            ResultSet answer =  connector.executeQuery("select * from customers");
            while(answer.next()){
                String cusID = answer.getString("Customer_ID");
                String name = answer.getString("Customer_Name");
                String create_date = answer.getString("Create_Date");
                String phone = answer.getString("Phone");
                String lastUpdate = answer.getString("Last_Update");
                Customer customer = new Customer(cusID, name, create_date, phone, lastUpdate);
                customers.add(customer);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    private void fillTable(){
        try{
            customer_table.setItems(customers);

        }catch(IndexOutOfBoundsException e){
            System.out.println(e.getMessage());
        }
    }

    public void addCustomer(){

    }

    public void editCustomer(){

    }

    public void deleteCustomer(){

    }




}
