package scheduling.demoschedulingapp.Controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduling.demoschedulingapp.Classes.Customer;
import scheduling.demoschedulingapp.SchedulingApplication;


import java.io.IOException;
import java.net.URL;
import java.util.Date;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    dbUtils connection = new dbUtils();
    Statement connector = connection.connStatement;

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

    /**
     * NEED TO SEE WHAT HAPPENS IF an entry is missing
     */
    private void buildCustomerList() {

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
            connector.close();
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

    /*private boolean verifyFields(){
       List<String> fields = Arrays.asList("phone", "address", "zip");

       Pattern phonePattern = Pattern.compile("^\\d{10}$");
       Matcher phoneMatcher = phonePattern.matcher(customerPhoneText.getText());

       Pattern addressPattern = Pattern.compile("[\\\\d]+[A-Za-z0-9\\\\s,\\\\.]+");
       Matcher addressMatcher  = addressPattern.matcher(customerAddText.getText());

        Pattern zipPattern = Pattern.compile("^[0-9]{5}(?:-[0-9]{4})?$");
        Matcher zipMatcher  = zipPattern.matcher(customerAddText.getText());

       for(int i =0; i < fields.size(); i ++){
           Alert fieldAlert = new Alert(Alert.AlertType.ERROR);
           fieldAlert.setTitle("Incorrect format");
           switch(fields.get(i)){
               case "phone":
                   if(!phoneMatcher.find()){
                       fieldAlert.setContentText("phone number ");
                       return false;
                   }
                   break;
               case "address":
                   if(!addressMatcher.find()){return false;}
                   break;
               case "zip":
                   if(!zipMatcher.find()){return false;}
                   break;
               default:
                   System.out.println("Something has gone terribly " +
                           "wrong in the verify fields func.");
                   break;
           }
       }
       return true;
    }*/

    /**
     * opens the add customer form.
     * @throws IOException
     */
    public void openAddCustomer() {
        try{
            SchedulingApplication.showNewWindow("addCustomer.fxml", "Add Customer");
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void openEditCustomer(){

    }

    public void deleteCustomer(){

    }


}
