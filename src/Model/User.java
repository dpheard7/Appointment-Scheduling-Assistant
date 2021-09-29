/**
 *
 * @author Damon Heard
 */

package Model;

import java.time.ZoneId;
import java.util.Locale;

/**
 * Class to model database users for login tracking.
 */
public class User {

    /**
     * Constructor for Report instance.
     * @param userID month of appointment
     * @param userName type of appointment
     * @param userPassword tally of appointments
     * @param userZone defaults to local system region
     * @param userLocale locale of user
     */
    private static int userID;
    private static String userName;
    private static String userPassword;
    private static ZoneId userZone = ZoneId.systemDefault();
    private static Locale userLocale;

    public User(int userID, String userName) {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    /**
     * Retrieves user's locale
     * @return userLocale
     */
    public static Locale getUserLocale() {
        return userLocale;
    }

    /**
     * Retrieves user timezone
     * @return userZone
     */
    public static ZoneId getUserZone() {
        return userZone;
    }

    /**
     * Retrieves userID
     * @return userID
     */
    public static int getUserID() {
        return userID;
    }
}
