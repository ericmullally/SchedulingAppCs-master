package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import scheduling.demoschedulingapp.Classes.Customer;
import scheduling.demoschedulingapp.Classes.User;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
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

    int customer_ID;

    @FXML
    public void initialize(){
        dbUtils.establishConnection();
        setCusID();
        setLanguage();
        setCountriesBoxes();
        customerIdText.setText(String.valueOf( customer_ID));
    }

    public void makeEdit(Customer customer){
        this.customer_ID = Integer.parseInt(customer.getCustomer_id());
        System.out.println("pass");
    }

    /**
     * Sets the customer ID
     */
    private void setCusID(){
        try{
            ResultSet cusCount = dbUtils.connStatement.executeQuery("select count(*) as total from customers");
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
            ResultSet statesProvinces = dbUtils.connStatement.executeQuery(request);
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
     * displays Success message if customer is added
     *
     */
    public void makeAddRequest() throws SQLException {
        if(!checkEntry()){
            return;
        }
        try {
            ResultSet divisionIdRes = dbUtils.connStatement.executeQuery(String.format("Select Division_ID from first_level_divisions " +
                                                                            "where Division = \"%s\"", stateDropDown.getValue().toString() ));
            divisionIdRes.next();

            LocalDateTime date = LocalDateTime.of(LocalDate.now(), LocalTime.now());

            String Customer_Name = customerNameText.getText();
            String Address = customerAddText.getText();
            String Postal_Code = postalCodeText.getText();
            String Phone = customerPhoneText.getText();
            LocalDateTime Create_Date = date;
            String Created_By = User.getInstance().getUserName();
            LocalDateTime Last_Update = date;
            String Last_Updated_By = User.getInstance().getUserName();
            int Division_ID = divisionIdRes.getInt(1) ;

            String addRequest = String.format("insert into customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone," +
                    " Create_Date, Created_By, Last_Update,Last_Updated_By, Division_ID)" +
                    "values(%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\", \"%s\", %d )", customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID);
            dbUtils.connStatement.execute(addRequest);

            String englishMessage = String.format("%s successfully added to database.", Customer_Name);
            String frenchMessage = String.format(" %s ajouté avec succès à la base de données.", Customer_Name);
            Alert success =  new Alert(Alert.AlertType.CONFIRMATION);
            success.setTitle(User.getInstance().getSystemLanguage() == "en" ? "Success" : "Succès");
            success.setContentText(User.getInstance().getSystemLanguage() == "fr" ? frenchMessage : englishMessage);
            success.showAndWait();

            Stage stage = (Stage) addCustomerCancelBtn.getScene().getWindow();
            String[] className = this.getClass().getName().split("\\.");
            MainController.refreshList(className[className.length -1 ]);
            stage.close();

        } catch (SQLException e) {
            String englishMessage = "The server has encountered an error.";
            String frenchMessage = "Le serveur a rencontré une erreur.";
            Alert connectionErr =  new Alert(Alert.AlertType.ERROR);
            connectionErr.setTitle(User.getInstance().getSystemLanguage() == "en" ? "Connection Error" : "Erreur de connexion");
            connectionErr.setContentText(User.getInstance().getSystemLanguage() == "fr" ? frenchMessage : englishMessage);
            connectionErr.showAndWait();
            System.out.println(e.getMessage());
        }finally {
            dbUtils.connStatement.close();
        }
    }

    /**
     * Ensures a customer name, phone, country and state or province are selected.
     * Displays the error message if anything is missing.
     * @return Boolean true is all entries are present false otherwise.
     */
    private boolean checkEntry(){
        if(customerNameText.getText().isBlank() || customerPhoneText.getText().isBlank()) {
            Alert noEntry = new Alert(Alert.AlertType.ERROR);
            noEntry.setTitle(User.getInstance().getSystemLanguage() == "fr" ? "Erreur de soumission" : "Submission Error");
            String messageEnglish =  customerNameText.getText().isBlank() ? "Please enter a name." : "Please enter a phone number.";
            String messageFrench = customerNameText.getText().isBlank() ? "Veuillez saisir un nom." : "Veuillez saisir un numéro de téléphone.";
            noEntry.setContentText(User.getInstance().getSystemLanguage() == "fr" ? messageFrench : messageEnglish);
            noEntry.show();
            return false;
        }else if(countryDropDown.getValue() == null || countryDropDown.getValue() == null) {
            Alert noSelection = new Alert(Alert.AlertType.ERROR);
            noSelection.setTitle(User.getInstance().getSystemLanguage() == "fr" ? "Erreur de soumission" : "Submission Error");
            String messageEnglish =countryDropDown.getValue() == null ? "Please select Country and state.": "Please select a state.";
            String messageFrench = countryDropDown.getValue() == null ? "Veuillez sélectionner le pays et la province." : "Veuillez sélectionner une province.";
            noSelection.setContentText(User.getInstance().getSystemLanguage() == "fr" ? messageFrench : messageEnglish);
            noSelection.show();
            return false;
        }else {
         return true;
        }


    }

    /**
     * Closes the add customer form.
     * @param mouseEvent
     */
    public void addCustomerCancel(MouseEvent mouseEvent) throws SQLException {
        mouseEvent.consume();
        Stage stage = (Stage) addCustomerCancelBtn.getScene().getWindow();
        dbUtils.connStatement.close();
        stage.close();
    }

}
