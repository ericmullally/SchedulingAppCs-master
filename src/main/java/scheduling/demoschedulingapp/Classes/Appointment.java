package scheduling.demoschedulingapp.Classes;

import java.time.LocalDateTime;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * An appointment with all details form the database
 * and the time updated to the current users time.
 */
public class Appointment {

    private final int timeOffset = ZonedDateTime.now().getOffset().getTotalSeconds();

    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private String startString;
    private String endString;
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
        LocalDateTime localStart = LocalDateTime.parse(start.replace(" ", "T"));
        LocalDateTime localEnd = LocalDateTime.parse(end.replace(" ", "T"));

        ZonedDateTime dbStartConverted = TimeConversion.convertTimes(ZonedDateTime.of(localStart, ZoneId.of("UTC")), User.getInstance().getUserTimeZone());
        ZonedDateTime dbEndConverted = TimeConversion.convertTimes(ZonedDateTime.of(localEnd, ZoneId.of("UTC")), User.getInstance().getUserTimeZone());

        this.appointmentID = appointment_ID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = dbStartConverted;
        this.end =  dbEndConverted ;
        this.startString = buildStartString(dbStartConverted.toString());
        this.endString = buildEndString(dbEndConverted.toString());
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
    public ZonedDateTime getStart() {return start;}
    public ZonedDateTime getEnd() {return end;}
    public String getStartString(){return startString;}
    public String getEndString(){return endString;}
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
    public void setStart(ZonedDateTime start){this.start = start;}
    public void setEnd(ZonedDateTime end){this.end = end;}
    public void setCreateDate(String createDate){this.createDate = createDate;}
    public void setCreatedBy(String createdBy){this.createdBy = createdBy;}
    public void setLastUpdate(String lastUpdate){this.lastUpdate = lastUpdate ;}
    public void setLastUpdatedBy(String lastUpdatedBy){this.lastUpdatedBy = lastUpdatedBy ;}
    public void setCustomerID(int customerID ){this.customerID = customerID ;}
    public void setUserID(int userID){this.userID = userID;}
    public void setContactID(int contactID){this.contactID = contactID;}

    /**
     * builds a string of the start time more appropriate for display purposes
     * @param startTime localDateTime
     * @return formatted string with the date and time.
     */
    public String buildStartString(String startTime){
        String[] startList = startTime.split("T");
        String timeList;
        if(startList[1].contains("-"))
            timeList = startList[1].split("-")[0];
        else if(startList[1].contains("+")){
            timeList = startList[1].split(java.util.regex.Pattern.quote("+"))[0];
        }else{
            timeList = startList[1].split("Z")[0];
        }
        return String.format("%s %s", startList[0], timeList);
    }

    /**
     * builds a string of the end time more appropriate for display purposes
     * @param endTime localDateTime
     * @return formatted string with the date and time.
     */
    public String buildEndString(String endTime){
        String[] endList = endTime.split("T");
        String timeList;
        if(endList[1].contains("-"))
            timeList = endList[1].split("-")[0];
        else if(endList[1].contains("+")){
            timeList = endList[1].split(java.util.regex.Pattern.quote("+"))[0];
        }else{
            timeList = endList[1].split("Z")[0];
        }
        return String.format("%s %s", endList[0], timeList);
    }


}
