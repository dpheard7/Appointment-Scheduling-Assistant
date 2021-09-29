/**
 *
 * @author Damon Heard
 */

package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataRecord {

    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    /**
     * Returns ObservableList of all customers.
     * @return
     */
    public static ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }

    /**
     * Adds a customer to the record
     * @param newCustomer new customer
     */
    public static void addCustomer(Customer newCustomer) {
        allCustomers.add(newCustomer);
    }

    /**
     * Returns ObservableList of all appointments.
     * @return
     */
    public static ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }


}
