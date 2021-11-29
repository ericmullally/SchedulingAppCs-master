package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduling.demoschedulingapp.Classes.Appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportsController {

    @FXML
    Button january_Btn, febuary_Btn ,march_Btn ,april_Btn ,may_Btn ,june_Btn ,july_Btn ,august_Btn ,
           september_Btn ,october_Btn ,november_Btn ,december_Btn ;
    @FXML
    Label monthLbl;
    @FXML
    TableView<Type> typeAppointmentTable;
    @FXML
    TableColumn<Type, String> typeCol;
    @FXML
    TableColumn<Type, Integer> totalCol;

    //region Appointments Stuff

    /**
     * simple class to display in the appointment report.
     */
    public class Type{
        private String name;
        private int count;

        public Type(String name){ this.name = name;}
        public void incrementCount(){count ++;}
        public void decrementCount(){count --;}
        public int getCount(){return count;}
        public String getName() { return name; }
    }

    @FXML
    public void initialize(){
        typeCol.setCellValueFactory(new PropertyValueFactory<Type, String>("name"));
        totalCol.setCellValueFactory(new PropertyValueFactory<Type, Integer>("count"));
    }

    /**
     * decides which button was pressed and sets the appointment report accordingly.
     * @param e the event
     */
    public void handleBtn(ActionEvent e){
        String btnName = ((Button) e.getTarget()).getId();
        HashMap<String, Integer> months = new HashMap<>(){{
            put("january", 1); put("febuary", 2); put("march", 3);
            put("april", 4); put("may", 5); put("june", 6);
            put("july", 7); put("august", 8); put("september", 9);
            put("october", 10); put("november", 11); put("december", 12);
        }};

        monthLbl.setText(btnName.split("_")[0].toUpperCase());
        fillTypeAppointment(months.get(btnName.split("_")[0]));
    }

    /**
     * fills the appointment table with the type and number of appointments
     * for the button that was clicked.
     * @param month
     */
    private void fillTypeAppointment(int month){
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        HashMap<String, Type > types = new HashMap<>(){
            {
                put("Stand up", new Type("Stand up"));
                put("Sit Down", new Type("Sit Down"));
                put("Scrum", new Type("Scrum"));
                put("Interview", new Type("Interview"));
            }
        };

        appointments.clear();
        typeAppointmentTable.getItems().clear();
        dbUtils.establishConnection();

        try{
            ResultSet appointmentRes = dbUtils.connStatement.executeQuery(String.format("select * from appointments where extract(MONTH from Start) = %d", month));
            while(appointmentRes.next()){
                int appointmentID = appointmentRes.getInt("Appointment_ID");
                String title = appointmentRes.getString("Title");
                String description = appointmentRes.getString("Description");
                String location = appointmentRes.getString("Location");
                String type = appointmentRes.getString("Type");
                String start = appointmentRes.getString("Start");
                String end = appointmentRes.getString("End");
                String createDate = appointmentRes.getString("Create_Date");
                String createdBy = appointmentRes.getString("Created_By");
                String lastUpdate = appointmentRes.getString("Last_Update");
                String lastUpdatedBy = appointmentRes.getString("Last_Updated_By");
                int customerID = appointmentRes.getInt("Customer_ID");
                int userID = appointmentRes.getInt("User_ID");
                int contactID = appointmentRes.getInt("Contact_ID");

                Appointment appointment = new Appointment(appointmentID, title, description, location, type, start, end,
                        createDate, createdBy, lastUpdate, lastUpdatedBy, customerID, userID, contactID);
                appointments.add(appointment);
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        if(appointments.size() == 0){
            typeAppointmentTable.setPlaceholder(new Label("No Appointments to Show."));
        }else{
            appointments.forEach(a-> types.get(a.getType()).incrementCount() );
            for(Map.Entry<String, Type> entry : types.entrySet()){
                if(entry.getValue().getCount() > 0){
                    typeAppointmentTable.getItems().add(entry.getValue());
                }
            }

        }
    }

    //endregion

    //region contacts


    //endregion

    //region by country
    //endregion

}
