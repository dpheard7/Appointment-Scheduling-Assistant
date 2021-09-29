/**
 *
 * @author Damon Heard
 */

package Utils;

import Model.Appointment;
import Model.Customer;
import Model.DataRecord;
import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class used to store useful methods to be implemented across application.
 */
public class Tools {

    public static String userLog = "login_activity.txt";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Retrieves customerID
     * @return customerID
     */
    public static int getCustomerID() {
        int customerID = 0;

        for (Customer customer : DataRecord.getAllCustomers()) {
            if (customer.getCustomerID() > customerID) {
                customerID = customer.getCustomerID();
            }
        }
        return customerID;
    }

    /**
     * Retrieves appointmentID
     * @return appointmentID
     */
    public static int getApptID() {
        int apptID = 0;

        for (Appointment appointment : DataRecord.getAllAppointments()) {
            if (appointment.getCustomerID() > apptID) {
                apptID = appointment.getCustomerID();
            }
        }
        return apptID;
    }

    /**
     * Validates new/modified customer entry.
     * @param customerName name of customer.
     * @param customerAddress address of customer.
     * @param customerPostal postcode of customer.
     * @param customerPhone phone number of customer.
     * @param customerDivision division of customer.
     * @return status of validation
     * @throws Exception
     */
    public static boolean validateCustomer(String customerName, String customerAddress, String customerPostal,
                                           String customerPhone, String customerDivision) throws Exception {

        if (customerName.isEmpty() || customerAddress.isEmpty() || customerPostal.isEmpty() || customerPhone.isEmpty() || customerDivision.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("A field is blank.");
            alert.showAndWait();
            throw new NullPointerException();
        }
        return false;
    }

    /**
     * Validates new/modified appointment entry.
     * @param apptTitle appointment title
     * @param apptDescription appointment description
     * @param apptLocation appointment location
     * @param apptContact appointment's contact
     * @param apptType appointment type
     * @param apptStart appointment start time
     * @param apptEnd appointment end time
     * @return status of validation
     * @throws Exception
     */
    public static boolean validateAppt(String apptTitle, String apptDescription, String apptLocation,
                                       int apptContact, String apptType, LocalDateTime apptStart, LocalDateTime apptEnd) throws Exception {

        if (apptTitle.isEmpty() || apptDescription.isEmpty() || apptLocation.isEmpty() || apptContact == 0 || apptType.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("A field is blank.");
            alert.showAndWait();
            throw new NullPointerException();
        }
        if (apptEnd.isBefore(apptStart) || apptEnd.equals(apptStart)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("End date/time must be after start date/time.\nPlease check and try again.");
            alert.showAndWait();
            throw new Exception();

        }
        if (apptStart == null || apptEnd == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("A time field is blank.");
            alert.showAndWait();
            throw new NullPointerException();
        }
        if (apptStart.equals(2200)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Appointment cannot start at end of business hours.\nPlease select an earlier time");
            alert.showAndWait();
        }
        return true;
    }

    /**
     * Formats date/time strings into LocalDateTime type
     * @param date
     * @param time
     * @return
     */
    public static LocalDateTime dateTimeFormatter(LocalDate date, String time) {

        String concat = date + " " + time + ":00";
        LocalDateTime localDateTime = LocalDateTime.parse(concat, dateTimeFormatter);
        return localDateTime;
    }

    /**
     * Method to track successful login attempts.
     * @param userName username of user
     * @throws IOException
     */
    public static void recordLoginSuccess(String userName) throws IOException {

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userLog, true));
            bufferedWriter.append("Successful login on " + ZonedDateTime.now(ZoneOffset.UTC) + ", User: " + userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error.");
            alert.setContentText("An error occurred. Please try again.");
            alert.showAndWait();
        }
    }

    /**
     * Records failed logins.
     * @param userName username of user
     * @throws IOException
     */
    public static void recordLoginFail(String userName) throws IOException {

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userLog, true));
            bufferedWriter.append("Failed login on " + ZonedDateTime.now(ZoneOffset.UTC) + ", User: " + userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error.");
            alert.setContentText("An error occurred. Please try again.");
            alert.showAndWait();
        }
    }
}
