package scheduling.demoschedulingapp.Controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles all database requests.
 * Can fetch data add and update data or delete data.
 */
public class dbUtils {
    private final String Username = "sqlUser";
    private final String Password = "Passw0rd!";
    private final String url = "jdbc:mysql://localhost:3306/client_schedule";
    public Statement connStatement;

    public dbUtils() {
        establishConnection();
    }


    public void establishConnection() {
        try {
            Connection connection = DriverManager.getConnection(url, Username, Password);
            connStatement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}
