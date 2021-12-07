package scheduling.demoschedulingapp.Classes;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;


/**
 * Singleton class Providing the entire application with the users
 * username, system language and timezone Id.
 */
public class User {
    private String userName;
    private String systemLanguage = Locale.getDefault().getLanguage();
    private ZoneId userTimeZone = ZonedDateTime.now().getZone();




    private static final User INSTANCE = new User();

    private User(){};

    public static User getInstance(){return INSTANCE;}
    public void setUserName(String name){ this.userName = name;}
    public String getUserName(){ return this.userName;}
    public String getSystemLanguage() {return systemLanguage;}
    public ZoneId getUserTimeZone(){return userTimeZone;}
    

}
