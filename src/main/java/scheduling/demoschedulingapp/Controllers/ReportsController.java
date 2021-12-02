package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduling.demoschedulingapp.Classes.Appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * controls all tabs in the reports section.
 */
public class ReportsController {
    //region appointment fxml
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
    //endregion

    //region contact fxml
    @FXML
    TableView<Schedule> contactTbl;
    @FXML
    TableColumn<Schedule, String> contactNameCol, appointmentIdCol, titleCol, conTypeCol,
                                  descriptionCol, startCol,endCol, customerIdCol;
    @FXML
    TextField contactNameSearch;
    //endregion

    //region by country
    @FXML
    BarChart<String, Number> customerAppointmentCount;
    @FXML
    CategoryAxis customerNames;
    @FXML
    NumberAxis numberAppointments;

    //endregion


    @FXML
    public void initialize(){
        typeCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        conTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerAppointmentCount.setAnimated(false);
        buildContactScheduleList();
        setFilter();
        buildCusAppointmentMap();
        setChart();
    }

    public void refreshClicked(){
        buildContactScheduleList();
        buildCusAppointmentMap();
        setChart();
    }

    //region Appointments

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

    /**
     * decides which button was pressed and sets the appointment report accordingly.
     * @param e the event
     */
    public void handleAppointmentBtn(ActionEvent e){
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
     * @param month month to be displayed.
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
    private static ObservableList<Schedule> schedules = FXCollections.observableArrayList();

    /**
     * simple class to fill the table.
     * only needed in this area.
     */
    public static class Schedule{
        private String contactName;
        private String appointmentID;
        private String title;
        private String type;
        private String description;
        private String start;
        private String end;
        private String customerID;

        public Schedule(String contactName,String appointmentID, String title,String type,
                        String description, String start, String end, String customerID ){
            this.contactName =contactName; this.appointmentID = appointmentID;
            this.title = title; this.type = type; this.description = description;
            this.start = start; this.end = end; this.customerID = customerID;
        }

        public String getAppointmentID(){return appointmentID;}
        public String getContactName(){return contactName;}
        public String getTitle(){return title;}
        public String getType(){return type;}
        public String getDescription(){return description;}
        public String getStart(){return start;}
        public String getEnd(){return end;}
        public String getCustomerID(){return customerID;}
    }

    /**
     * builds the contact schedule list for displaying.
     */
    public static void buildContactScheduleList(){
        schedules.clear();
        dbUtils.establishConnection();
        try{
            ResultSet scheduleRes = dbUtils.connStatement.executeQuery(String.format("select Contact_Name, Appointment_ID, Title, Type, Description, Start," +
                                                            " End, Customer_ID from contacts join appointments on contacts.Contact_ID = appointments.Contact_ID "));
            while(scheduleRes.next()){
                 String contactName = scheduleRes.getString("Contact_Name");
                 String appointmentID = scheduleRes.getString("Appointment_ID");
                 String title = scheduleRes.getString("Title");
                 String type = scheduleRes.getString("Type");
                 String description = scheduleRes.getString("Description");
                 String start = scheduleRes.getString("Start");
                 String end = scheduleRes.getString("End");
                 String customerID = scheduleRes.getString("Customer_ID");

                 Schedule schedule = new Schedule(contactName, appointmentID, title, type, description, start, end, customerID);
                 schedules.add(schedule);
            }
            scheduleRes.close();


        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * sets the filter for the searchbar in the contacts schedule tab.
     */
    private void setFilter(){
        contactTbl.setItems(schedules);

        FilteredList<Schedule> filteredSchedules = new FilteredList<>(schedules, b-> true);
        contactNameSearch.textProperty().addListener((observable, oldVal, newVal )->{
            filteredSchedules.setPredicate(Schedule ->{
                if(newVal.isBlank()|| newVal.isEmpty() || newVal == null){
                    return true;
                }else{
                    String searchKey = newVal.toLowerCase();
                    if(Schedule.getContactName().toLowerCase().indexOf(searchKey) > -1){
                        return true;
                    }else{
                        return false;
                    }
                }
            });
        });

        SortedList<Schedule> sortedSchedules = new SortedList<>(filteredSchedules);
        sortedSchedules.comparatorProperty().bind(contactTbl.comparatorProperty());
        contactTbl.setItems(sortedSchedules);
    }

    //endregion

    //region by country
    private static HashMap<String, Integer> customerAppointments = new HashMap<>();

    /**
     * sets the chart for the customers appointments.
     */
    private void setChart(){
        customerAppointmentCount.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Appointments");
        customerAppointments.forEach((k,v)-> series.getData().add(new XYChart.Data<>(k,v)) );
        customerAppointmentCount.getData().add(series);

    }

    /**
     * builds the hasmap to be used in the chart.
     */
    public static void buildCusAppointmentMap() {
        customerAppointments.clear();
        dbUtils.establishConnection();
        try {
            ResultSet customerRes = dbUtils.connStatement.executeQuery("select Customer_Name  from " +
                    "appointments join customers on appointments.Customer_ID = customers.Customer_ID");
            while (customerRes.next()) {
                String customerName = customerRes.getString("Customer_Name");
                if (!customerAppointments.containsKey(customerName)) {
                    customerAppointments.put(customerName, 1);
                } else {
                    customerAppointments.put(customerName, customerAppointments.get(customerName) + 1);
                }
            }
            customerRes.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    //endregion

}
