package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import scheduling.demoschedulingapp.Classes.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class addCustomerController {

    @FXML
    TextField customerIdText, customerNameText, customerPhoneText, customerAddText, postalCodeText;
    @FXML
    ComboBox countryDropDown, stateDropDown;

    dbUtils connection = new dbUtils();
    Statement connector = connection.connStatement;



    public void makeAddRequest(){
        try{
            ResultSet cusCount = connector.executeQuery("select count(*) from customers");
            int Customer_ID =  1;
            String Customer_Name = customerNameText.getText();
            String Address = customerAddText.getText();
            String Postal_Code = postalCodeText.getText();
            String Phone = customerPhoneText.getText();
            Date Create_Date = new Date();
            String Created_By ="current user";
            Date Last_Update = new Date();
            String Last_Updated_By = "current user";
            int Division_ID = 0;

            String addRequest = String.format("insert into customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone," +
                    " Create_Date, Created_By, Last_Update,Last_Updated_By, Division_ID)" +
                    "values(%d,%s,%s,%s,%s,%t,%s,%t,%d )", Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By,Division_ID );
            ResultSet answer =  connector.executeQuery(addRequest);
            System.out.println(answer.toString());
            connector.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void addCustomerCancel(MouseEvent mouseEvent) {

    }
}
