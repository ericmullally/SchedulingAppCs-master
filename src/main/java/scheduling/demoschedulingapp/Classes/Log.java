package scheduling.demoschedulingapp.Classes;

public class Log {
    private String userName;
    private String time;
    private String date;
    private String success;

    public Log(String userName, String time, String date, String success){
        this.userName = userName;
        this.time = time;
        this.date =date;
        this.success = success;
    }

    public String getUserName(){return userName;}
    public String getTime(){return time;}
    public String getDate(){return date;}
    public String getSuccess(){return success;}

    public void setUserName(String userName){this.userName = userName;}
    public void setTime(String time){this.time = time;}
    public void setDate(String date){this.date = date;}
    public void setSuccess(String success){this.success = success;}


}
