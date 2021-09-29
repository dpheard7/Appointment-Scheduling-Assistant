/**
 *
 * @author Damon Heard
 */

package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class to model customers for creation/modification.
 */
public class Customer {

    private ObservableList<Appointment> customerAppts = FXCollections.observableArrayList();

    private int customerID;
    private String customerName;
    private String customerAddress;
    private String customerPostal;
    private String customerPhone;
    private String customerDivision;
    private String customerCountry;
    public static String createDate;
    public static String createdBy = "5";
    public static String lastUpdate;
    public static String lastUpdatedBy = "5";

    public Customer(int customerID, String customerName, String customerAddress, String customerPhone, String customerPostal,
                    String createDate, String createdBy, String lastUpdate, String lastUpdatedBy, String customerDivision) {

        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPostal = customerPostal;
        this.customerPhone = customerPhone;
        this.customerDivision = customerDivision;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Retrieves customerID
     * @return customerID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Sets customerID
     * @param customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * Adds customer to ObservableList
     * @param addedAppt
     */
    public void addCustAppt(Appointment addedAppt) {
        customerAppts.add(addedAppt);
    }

    /**
     * Retrieves customer name
     * @return customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Retrieves customer address
     * @return customerAddress
     */
    public String getCustomerAddress() {
        return customerAddress;
    }

    /**
     * Retrieves customer postal code
     * @return customerPostal
     */
    public String getCustomerPostal() {
        return customerPostal;
    }

    /**
     * Retrieves customer phone number
     * @return customerPhone
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    /**
     * Retrieves customer first level division
     * @return customerDivision
     */
    public String getCustomerDivision() {
        return customerDivision;
    }

    /**
     * Sets customer first level division
     * @param customerDivision
     */
    public void setCustomerDivision(String customerDivision) {
        this.customerDivision = customerDivision;
    }

    /**
     * Retrieves customer country
     * @return customerCountry
     */
    public String getCustomerCountry() {
        return customerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    /**
     * Retrieves creation date of customer
     * @return createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Retrieves update date of customer
     * @return lastUpdatedBy
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

}