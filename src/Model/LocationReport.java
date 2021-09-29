/**
 *
 * @author Damon Heard
 */

package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class to pull reports of appointments by location.
 */
public class LocationReport {

    public static ObservableList<LocationReport> ApptsByLocation = FXCollections.observableArrayList();

    /**
     * Constructor for location report.
     * @param location location of referenced appointment
     * @param count tally of appointments at given location
     */
    private String location;
    private String count;

    public LocationReport(String location, String count) {
        this.location = location;
        this.count = count;
    }

    public static ObservableList<LocationReport> getApptsByLocation() {
        return ApptsByLocation;
    }

    public static void setApptsByLocation(ObservableList<LocationReport> apptsByLocation) {
        ApptsByLocation = apptsByLocation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
