/**
 *
 * @author Damon Heard
 */

package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Class to model appointments for creation/modification.
 */
public  class Appointment {

    /**
     * ObservableLists used to insert objects and retrieve for different methods.
     */
    public static ObservableList<Appointment> getAllCustomerAppts;
    private ObservableList<Appointment> customerAppts = FXCollections.observableArrayList();
    public static ObservableList<Appointment> FilteredAppts = FXCollections.observableArrayList();

    public static ObservableList<String> Contacts = FXCollections.observableArrayList("Anika Costa", "Daniel Garcia", "Li Lee");
    public static ObservableList<String> ApptType = FXCollections.observableArrayList("Planning Session", "De-Briefing",
            "Field Assessment", "Field Training Exercise");
    public static ObservableList<LocalTime> ApptStartTimes = FXCollections.observableArrayList();
    public static ObservableList<LocalTime> ApptEndTimes = FXCollections.observableArrayList();

    //    public static ObservableList<String> ApptStartTimes = FXCollections.observableArrayList("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
//            "15:00", "17:00");
//    public static ObservableList<String> ApptEndTimes = FXCollections.observableArrayList("08:50", "10:30", "12:30", "14:30",
//            "16:30", "18:30");
    public static ObservableList<String> ApptLocations = FXCollections.observableArrayList("Tower, 14A", "Hangar", "South Lawn");
    public static ObservableList<Appointment> ConflictingAppts = FXCollections.observableArrayList();
    public static ArrayList<Appointment> AlertAppts = new ArrayList<>();
    public static ObservableList<Appointment> ContactAppts = FXCollections.observableArrayList();


    private int apptID;
    private String apptTitle;
    private String apptDescription;
    private String apptLocation;
    private int apptContact;
    private String apptType;
    public LocalDateTime apptStartTime;
    public LocalDateTime apptEndTime;
    private int customerID;
    private String userID;

    public Appointment(int apptID, int customerID, String apptTitle, String apptDescription, String apptLocation, int apptContact,
                       String apptType, LocalDateTime apptStartTime, LocalDateTime apptEndTime, String userID) {
        this.apptID = apptID;
        this.apptTitle = apptTitle;
        this.apptDescription = apptDescription;
        this.apptLocation = apptLocation;
        this.apptContact = apptContact;
        this.apptType = apptType;
        this.apptStartTime = apptStartTime;
        this.apptEndTime = apptEndTime;
        this.customerID = customerID;
        this.userID = userID;
    }

    /**
     *
     * @return userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * sets userID
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * retrieves appointment ID
     * @return apptID
     */
    public int getApptID() {
        return apptID;
    }

    /**
     * sets apptID
     * @param apptID
     */
    public void setApptID(int apptID) {
        this.apptID = apptID;
    }

    /**
     * retrieves appointment title
     * @return apptTitle
     */
    public String getApptTitle() {
        return apptTitle;
    }

    /**
     * sets appointment title
     * @param apptTitle
     */
    public void setApptTitle(String apptTitle) {
        this.apptTitle = apptTitle;
    }

    /**
     * retrieves appointment description
     * @return apptDescription
     */
    public String getApptDescription() {
        return apptDescription;
    }

    /**
     * sets appointment description
     * @param apptDescription
     */
    public void setApptDescription(String apptDescription) {
        this.apptDescription = apptDescription;
    }

    /**
     * retrieves appointment description
     * @return apptDescription
     */
    public String getApptLocation() {
        return apptLocation;
    }

    /**
     * sets appointment location
     * @param apptLocation
     */
    public void setApptLocation(String apptLocation) {
        this.apptLocation = apptLocation;
    }

    /**
     * retrieves appointment contact
     * @return apptContact
     */
    public int getApptContact() {
        return apptContact;
    }

    /**
     * sets appointment contact
     * @param apptContact
     */
    public void setApptContact(int apptContact) {
        this.apptContact = apptContact;
    }

    /**
     * retrieves appointment type
     * @return apptType
     */
    public String getApptType() {
        return apptType;
    }

    /**
     * sets appointment type
     * @param apptType
     */
    public void setApptType(String apptType) {
        this.apptType = apptType;
    }

    /**
     * retrieves appointment start time
     * @return apptStartTime
     */
    public LocalDateTime getApptStartTime() {
        return apptStartTime;
    }

    /**
     * sets apptStartTime
     * @param apptStartTime
     */
    public void setApptStartTime(LocalDateTime apptStartTime) {
        this.apptStartTime = apptStartTime;
    }

    /**
     * retrieves ObservableList of appointment contacts
     * @return Contacts
     */
    public static ObservableList<String> getContacts() {
        return Contacts;
    }

    /**
     * retrieves ObservableList of appointment start times
     * @return ApptStartTimes
     */
    public static ObservableList<LocalTime> getApptStartTimes() {
        return ApptStartTimes;
    }

    /**
     * retrieves ObservableList of locations
     * @return ApptLocations
     */
    public static ObservableList<String> getApptLocations() {
        return ApptLocations;
    }

    /**
     * retrieves appointment's end time
     * @return apptEndTime
     */
    public LocalDateTime getApptEndTime() {
        return apptEndTime;
    }

    /**
     * retrieves customerID
     * @return customerID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * sets customerID
     * @param customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * sets customer appointments
     * @param getAllCustomerAppts
     */
    public static void setGetAllCustomerAppts(ObservableList<Appointment> getAllCustomerAppts) {
        Appointment.getAllCustomerAppts = getAllCustomerAppts;
    }

    /**
     * gets all customer appointments in ObservableList
     * @return customerAppts
     */
    public ObservableList<Appointment> getCustomerAppts() {
        return customerAppts;
    }

}
