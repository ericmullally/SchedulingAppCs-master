package scheduling.demoschedulingapp.Classes;


import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Singleton class Providing the entire application with username and system language.
 */
public class User {
    private String userName;
    private String systemLanguage = Locale.getDefault().getLanguage();

    private static final User INSTANCE = new User();

    private User(){};

    public static User getInstance(){
        return INSTANCE;
    }

    public void setUserName(String name){
        this.userName = name;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getSystemLanguage() {
        return systemLanguage;
    }

}
