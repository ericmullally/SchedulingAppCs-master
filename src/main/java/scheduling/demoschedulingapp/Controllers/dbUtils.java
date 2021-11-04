package scheduling.demoschedulingapp.Controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * Handles all database requests.
 * Can fetch data add and update data or delete data.
 */
public abstract class dbUtils {
    private static Statement connStatement;
    private static String Username = "sqlUser";
    private static String Password = "Passw0rd!";
    private static String url = "jdbc:mysql://localhost:3306/client_schedule" ;


    private static void establishConnection() {
        try{
            Connection connection = DriverManager.getConnection(url, Username, Password);
            connStatement = connection.createStatement();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * fetches data from the database.
     *
     * @param requestString
     */
    public static void makeRequest(String requestString) {

        try{
            establishConnection();
            ResultSet answer = connStatement.executeQuery(requestString);
            while(answer.next()){
                System.out.println(answer.getString(1));
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

}
