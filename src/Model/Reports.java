/**
 *
 * @author Damon Heard
 */

/**
 *Class to create reports. Inserts data into ObservableList for Tableview.
 */
package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Reports {

    public static ObservableList<Reports> ApptTypesByMonth = FXCollections.observableArrayList();

    /**
     * Constructor for Report instance.
     * @param apptMonth month of appointment
     * @param apptType type of appointment
     * @param apptCount tally of appointments
     */
    private String apptMonth;
    private String apptType;
    private String apptCount;

    public Reports(String apptMonth, String apptType, String apptCount) {
        this.apptType = apptType;
        this.apptMonth = apptMonth;
        this.apptCount = apptCount;
    }

    public static ObservableList<Reports> getApptTypesByMonth() {
        return ApptTypesByMonth;
    }

    public static void setApptTypesByMonth(ObservableList<Reports> apptTypesByMonth) {
        ApptTypesByMonth = apptTypesByMonth;
    }

    public String getApptMonth() {
        return apptMonth;
    }

    public void setApptMonth(String apptMonth) {
        this.apptMonth = apptMonth;
    }

    public String getApptType() {
        return apptType;
    }

    public void setApptType(String apptType) {
        this.apptType = apptType;
    }

    public String getApptCount() {
        return apptCount;
    }

    public void setApptCount(String apptCount) {
        this.apptCount = apptCount;
    }
}
