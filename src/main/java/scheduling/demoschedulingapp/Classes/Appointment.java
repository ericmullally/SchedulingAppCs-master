package scheduling.demoschedulingapp.Classes;

public class Appointment {

    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private String start;
    private String end;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private int customerID;
    private int userID;
    private int contactID;


    public Appointment(  int appointment_ID, String title, String description,String location,String type, String start, String end,
                         String createDate, String createdBy, String lastUpdate, String lastUpdatedBy,int customerID, int userID, int contactID)
    {
        this.appointmentID = appointment_ID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;

    }

    public int getAppointmentID() {  return appointmentID;}
    public String getTitle() { return title;}
    public String getDescription() { return description;}
    public String getLocation() {return location;}
    public String getType() {return type;}
    public String getStart() {return start;}
    public String getEnd() {return end;}
    public String getCreateDate() {return createDate;}
    public String getCreatedBy() {return createdBy;}
    public String getLastUpdate() {return lastUpdate;}
    public String getLastUpdatedBy() { return lastUpdatedBy;}
    public int getCustomerID(){return customerID;}
    public int getUserID(){return userID;}
    public int getContactID(){return contactID;}

    public void setAppointmentID(int appointmentID) {this.appointmentID = appointmentID;}
    public void setTitle(String title){this.title = title ;}
    public void setDescription(String description){this.description = description ;}
    public void setLocation(String location){this.location = location ;}
    public void setType(String type){this.type = type ;}
    public void setStart(String start){this.start = start;}
    public void setEnd(String end){this.end = end;}
    public void setCreateDate(String createDate){this.createDate = createDate;}
    public void setCreatedBy(String createdBy){this.createdBy = createdBy;}
    public void setLastUpdate(String lastUpdate){this.lastUpdate = lastUpdate ;}
    public void setLastUpdatedBy(String lastUpdatedBy){this.lastUpdatedBy = lastUpdatedBy ;}
    public void setCustomerID(int customerID ){this.customerID = customerID ;}
    public void setUserID(int userID){this.userID = userID;}
    public void setContactID(int contactID){this.contactID = contactID;}


}
