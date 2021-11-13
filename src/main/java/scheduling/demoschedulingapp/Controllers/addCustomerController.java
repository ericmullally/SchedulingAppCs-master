package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

public class addCustomerController {

    @FXML
    TextField customerIdText, customerNameText, customerPhoneText, customerAddText, postalCodeText;
    @FXML
    ComboBox countryDropDown, stateDropDown;
    @FXML
    Label customerIdLbl, customerNameLbl, phoneLbl, addressLbl, postalLbl, countryLbl, stateLbl;

    @FXML
    Button addCustomerCancelBtn, addCustomerSubmit;

    dbUtils connection = new dbUtils();
    Statement connector = connection.connStatement;
    int customer_ID;

    @FXML
    public void initialize(){
        setCusID();
        setLanguage();
        setCountriesBoxes();
        customerIdText.setText(String.valueOf( customer_ID));
    }

    /**
     * Sets the customer ID
     */
    private void setCusID(){
        try{
            ResultSet cusCount = connector.executeQuery("select count(*) as total from customers");
            cusCount.next();
            customer_ID = cusCount.getInt("total") + 1;
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * sets country dropdown choices.
     */
    private void setCountriesBoxes(){
        ObservableList countries = FXCollections.observableArrayList("-Select-", "US", "UK", "Canada");
        countries.forEach(item -> countryDropDown.getItems().add(item));
    }

    /**
     * sets State or Province dropdown choices.
     */
    public void setStateBoxes(){
        stateDropDown.getItems().clear();
        String selection = countryDropDown.getValue().toString();
        HashMap<String, Integer> codes = new HashMap<String, Integer>(){{
            put("US", 1);
            put("UK", 2);
            put("Canada", 3);
        }};
        try{
            String request = String.format("select * from first_level_divisions where" +
                    " Country_ID = %s", codes.get(selection));
            ResultSet statesProvinces = connector.executeQuery(request);
            while(statesProvinces.next()){
                stateDropDown.getItems().add(statesProvinces.getString("Division"));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * sets labels to french if the host system is using french.
     * Contains Lambda
     */
    private void setLanguage(){
        HashMap<Label, String> frenchLabels = new HashMap<Label, String>(){
            {
                put(customerIdLbl,"N ° de client");
                put(customerNameLbl,"Nom du client");
                put(phoneLbl,"Téléphone");
                put(addressLbl,"Adresse");
                put(postalLbl,"Code postal");
                put(countryLbl,"Pays");
                put(stateLbl,"Province");
            }
        };
        HashMap<Button, String> frenchButtons = new HashMap<Button, String>(){
            {
                put(addCustomerCancelBtn, "Ajouter un client");
                put(addCustomerSubmit, "Annuler");
            }
        };

        if(Locale.getDefault().getLanguage() == "fr"){
            frenchLabels.forEach((k,v) -> k.setText(v) );
            frenchButtons.forEach((k,v) -> k.setText(v) );
        }
    }

    /**
     * Sends the request to the server to create a new customer.
     * displays error message if fields are not correct.
     */
    public void makeAddRequest() throws SQLException {
        try {
            String Customer_Name = customerNameText.getText();
            String Address = customerAddText.getText();
            String Postal_Code = postalCodeText.getText();
            String Phone = customerPhoneText.getText();
            Date Create_Date = new Date();
            String Created_By = "current user";
            Date Last_Update = new Date();
            String Last_Updated_By = "current user";
            int Division_ID = 0;

            String addRequest = String.format("insert into customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone," +
                    " Create_Date, Created_By, Last_Update,Last_Updated_By, Division_ID)" +
                    "values(%d,%s,%s,%s,%s,%t,%s,%t,%d )", customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID);
            ResultSet answer = connector.executeQuery(addRequest);
            System.out.println(answer.toString());

        } catch (SQLException e) {
            Alert connectionErr =  new Alert(Alert.AlertType.ERROR);
            connectionErr.setTitle("Connection Error");
            connectionErr.setContentText("The server has encountered an error.");
            connectionErr.showAndWait();
            System.out.println(e.getMessage());
        }finally {
            connector.close();
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
     * Closes the add customer form.
     * @param mouseEvent
     */
    public void addCustomerCancel(MouseEvent mouseEvent) throws SQLException {
        mouseEvent.consume();
        Stage stage = (Stage) addCustomerCancelBtn.getScene().getWindow();
        connector.close();
        stage.close();
    }


}
