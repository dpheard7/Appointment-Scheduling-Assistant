/**
 *
 * @author Damon Heard
 */

package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class to handle database connectivity
 */
public class DBConnection {

    /**
     * Database login information.
     */

    /*
    username: sqlUser
    password: Passw0rd!
    DB name: client_schedule
    location: //localhost/

     */
    private static final String protocol = "jdbc";
    private static final String location = "//localhost/";
    private static final String vendorName = ":mysql:";
    private static final String dbName = "client_schedule";

    private static final String jdbcURL = protocol + vendorName + location + dbName + "?connectionTimeZone = SERVER";

    private static final String userName = "sqlUser";
    private static final String password = "Passw0rd!";

    private static final String MYSQLJDBCDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn;

    /**
     * Establishes database connection
     * @return connection status
     */
    public static Connection startConnection() {
        try {
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, userName, password);
            System.out.println("Connection Successful.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Establishes connection
     * @return connection status
     */
    public static Connection getConnection(){
        return conn;
    }

    /**
     * Closes database connection
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException {
        try {
            conn.close();
            System.out.println("Connection closed.");
        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//            System.out.println(throwables.getMessage());
        }
    }
}
