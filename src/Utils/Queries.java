/**
 *
 * @author Damon Heard
 */

package Utils;

import Model.Appointment;
import Model.Customer;
import Model.Reports;
import Model.User;
import Model.LocationReport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.EventObject;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Class to hold database queries for implementation across application.
 */
public class Queries {

    /**
     * Retrieve customer division
     * @param division
     * @return customer division
     * @throws SQLException
     */
    public static String getDivision(Customer division) throws SQLException {

        PreparedStatement getDivision = DBConnection.getConnection().prepareStatement("SELECT Division " +
                "FROM first_level_divisions " +
                "WHERE Division_ID = \"" + division.getCustomerDivision() + "\"");

        ResultSet resultSet = getDivision.executeQuery();
        resultSet.next();
        String custDiv = resultSet.getString(1);
        division.setCustomerDivision(resultSet.getString(1));
        return custDiv;
    }

    /**
     * Retrieves appointment location
     * @param appointment
     * @return appointment location
     * @throws SQLException
     */
    public static String getApptLocation(Appointment appointment) throws SQLException {

        PreparedStatement getLocation = DBConnection.getConnection().prepareStatement("SELECT Location " +
                "FROM appointments " +
                "WHERE Appointment_ID = \"" + appointment.getApptID() + "\"");

        ResultSet resultSet = getLocation.executeQuery();
        resultSet.next();
        String apptLocation = resultSet.getString(1);
        appointment.setApptLocation(resultSet.getString(1));
        return apptLocation;
    }

    /**
     * Inserts new customer into database.
     * @param newCustomer
     * @throws IOException
     */
    public static void insertCustomer(Customer newCustomer) throws IOException {
        try {

            PreparedStatement convertDivision = DBConnection.getConnection().prepareStatement("SELECT Division_ID " +
                    "FROM first_level_divisions " +
                    "WHERE Division = \"" + newCustomer.getCustomerDivision() + "\"");
            ResultSet resultSet = convertDivision.executeQuery();
            resultSet.next();

            PreparedStatement addSQLCustomer = DBConnection.getConnection().prepareStatement("INSERT INTO customers " +
                    "(Customer_Name, Address, Phone, Postal_Code, Create_Date, Created_By, Last_Update, " +
                    "Last_Updated_By, Division_ID) " + "VALUES (?, ?, ?, ?, NOW(), ?, NOW(), ?, ?)");

            addSQLCustomer.setString(1, newCustomer.getCustomerName());
            addSQLCustomer.setString(2, newCustomer.getCustomerAddress());
            addSQLCustomer.setString(3, newCustomer.getCustomerPhone());
            addSQLCustomer.setString(4, newCustomer.getCustomerPostal());
            addSQLCustomer.setInt(5, User.getUserID());
            addSQLCustomer.setInt(6, User.getUserID());
            addSQLCustomer.setInt(7, Integer.parseInt(resultSet.getObject(1).toString()));

            addSQLCustomer.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SQL Error.");
            alert.setContentText("SQL Error. Please try again.");
            alert.showAndWait();

            Stage stage;
            Parent scene;
            EventObject event = null;
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(Queries.class.getResource("/Views/AddCustomer.fxml")));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /**
     * Inserts new appointment into database
     * @param newAppointment
     * @throws IOException
     */
    public static void insertAppointment(Appointment newAppointment) throws IOException {

        Stage stage;
        Parent scene;

        try {

            ZoneId zoneId = ZoneId.systemDefault();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime ldtStart = newAppointment.getApptStartTime();
            LocalDateTime ldtEnd = newAppointment.getApptEndTime();

            ZonedDateTime zonedStart = ldtStart.atZone(zoneId);
            ZonedDateTime zonedEnd = ldtEnd.atZone(zoneId);

            LocalDateTime localStart = zonedStart.toLocalDateTime();
            LocalDateTime localEnd = zonedEnd.toLocalDateTime();

            ZonedDateTime zonedStartUTC = zonedStart.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime zonedEndUTC = zonedEnd.withZoneSameInstant(ZoneOffset.UTC);

            LocalDateTime finalStart = zonedStartUTC.toLocalDateTime();
            LocalDateTime finalEnd = zonedEndUTC.toLocalDateTime();

            PreparedStatement addSQLAppointment = DBConnection.getConnection().prepareStatement("INSERT INTO appointments " +
                    "(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, " +
                    "Customer_ID, User_ID, Contact_ID) " + "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)");

            addSQLAppointment.setString(1, newAppointment.getApptTitle());
            addSQLAppointment.setString(2, newAppointment.getApptDescription());
            addSQLAppointment.setString(3, newAppointment.getApptLocation());
            addSQLAppointment.setString(4, newAppointment.getApptType());
            addSQLAppointment.setTimestamp(5, Timestamp.valueOf(localStart));
            addSQLAppointment.setTimestamp(6, Timestamp.valueOf(localEnd));
            addSQLAppointment.setInt(7, User.getUserID());
            addSQLAppointment.setInt(8, User.getUserID());
            addSQLAppointment.setInt(9, newAppointment.getCustomerID());
            addSQLAppointment.setInt(10, User.getUserID());
            addSQLAppointment.setInt(11, newAppointment.getApptContact());

            addSQLAppointment.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SQL Error.");
            alert.setContentText("SQL Error. Please try again.");
            alert.showAndWait();

            EventObject event = null;
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(Queries.class.getResource("/Views/AddAppt.fxml")));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /**
     * Enables user login. Sets language bundle according to user locale.
     * @param userName
     * @param password
     * @return
     */
    public static boolean userLogin(String userName, String password) {

        Locale userLocale = Locale.getDefault();
        ResourceBundle languageBundle = ResourceBundle.getBundle("languageProperties", userLocale);

        boolean loginSuccess = false;

        try {
            PreparedStatement loginAttempt = DBConnection.getConnection().prepareStatement("SELECT * " +
                    "FROM users " +
                    "WHERE User_Name = ? " +
                    "AND Password = ? ");

            loginAttempt.setString(1, userName);
            loginAttempt.setString(2, password);

            ResultSet resultSet = loginAttempt.executeQuery();

            if (resultSet.next() == false) {
                loginAttempt.close();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(languageBundle.getString("Error"));
                alert.setContentText(languageBundle.getString("InvalidCredentials"));
                alert.showAndWait();
                loginSuccess = false;
            } else {
                User newUser = new User(resultSet.getInt("User_ID"), resultSet.getString("User_Name"));
                newUser.getUserLocale();
                newUser.getUserZone();
                loginSuccess = true;
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SQL Error.");
            alert.setContentText(languageBundle.getString("ErrorOccurred"));
            alert.showAndWait();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(languageBundle.getString("Error"));
            alert.setContentText(languageBundle.getString("ErrorOccurred"));
            alert.showAndWait();
        }
        return loginSuccess;
    }

    /**
     * Updates selected customer in database.
     * @param customer
     * @throws IOException
     */
    public static void updateCustomer(Customer customer) throws IOException {

        try {

            PreparedStatement convertDivision = DBConnection.getConnection().prepareStatement("SELECT Division_ID " +
                    "FROM first_level_divisions " +
                    "WHERE Division = \"" + customer.getCustomerDivision() + "\"");
            ResultSet resultSet = convertDivision.executeQuery();
            resultSet.next();

            PreparedStatement updateCustomer = DBConnection.getConnection().prepareStatement("UPDATE customers " +
                    "SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = NOW(), Created_By = ?, Last_Update = NOW(), " +
                    "Last_Updated_By = ?, Division_ID  = ? " +
                    "WHERE Customer_ID = ? " );

            updateCustomer.setString(1, customer.getCustomerName());
            updateCustomer.setString(2, customer.getCustomerAddress());
            updateCustomer.setString(3, customer.getCustomerPostal());
            updateCustomer.setString(4, customer.getCustomerPhone());
            updateCustomer.setString(5, customer.getCreatedBy());
            updateCustomer.setString(6, customer.getLastUpdatedBy());
            updateCustomer.setInt(7, Integer.parseInt(resultSet.getObject(1).toString()));
            updateCustomer.setInt(8, customer.getCustomerID());

            updateCustomer.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SQL Error.");
            alert.setContentText("SQL Error. Please try again.");
            alert.showAndWait();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error.");
            alert.setContentText("Error. Please try again.");
            alert.showAndWait();
        }
    }

    /**
     * Updates selected appointment in database.
     * @param appointment
     * @throws IOException
     */
    public static void updateAppointment(Appointment appointment) throws IOException {

        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime ldtStart = appointment.getApptStartTime();
        LocalDateTime ldtEnd = appointment.getApptEndTime();

        ZonedDateTime zonedStart = ldtStart.atZone(zoneId);
        ZonedDateTime zonedEnd = ldtEnd.atZone(zoneId);

        LocalDateTime localStart = zonedStart.toLocalDateTime();
        LocalDateTime localEnd = zonedEnd.toLocalDateTime();

        ZonedDateTime zonedStartUTC = zonedStart.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime zonedEndUTC = zonedEnd.withZoneSameInstant(ZoneOffset.UTC);

        LocalDateTime finalStart = zonedStartUTC.toLocalDateTime();
        LocalDateTime finalEnd = zonedEndUTC.toLocalDateTime();

        System.out.println(finalStart);

        try {

            PreparedStatement updateAppointment = DBConnection.getConnection().prepareStatement("UPDATE appointments " +
                    "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = NOW(), " +
                    "Last_Updated_By = ?, Customer_ID  = ?, User_ID = ?, Contact_ID = ? " +
                    "WHERE Appointment_ID = ? " );

            updateAppointment.setString(1, appointment.getApptTitle());
            updateAppointment.setString(2, appointment.getApptDescription());
            updateAppointment.setString(3, appointment.getApptLocation());
            updateAppointment.setString(4, appointment.getApptType());
            updateAppointment.setTimestamp(5, Timestamp.valueOf(localStart));
            updateAppointment.setTimestamp(6, Timestamp.valueOf(localEnd));
            updateAppointment.setInt(7, User.getUserID());
            updateAppointment.setInt(8, appointment.getCustomerID());
            updateAppointment.setInt(9, User.getUserID());
            updateAppointment.setInt(10, appointment.getApptContact());
            updateAppointment.setInt(11, appointment.getApptID());

            updateAppointment.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SQL Error.");
            alert.setContentText("SQL Error. Please try again.");
            alert.showAndWait();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error.");
            alert.setContentText("Error. Please try again.");
            alert.showAndWait();
        }
    }

    /**
     * Deletes customer from database.
     * @param customer
     */
    public static void deleteCustomer(Customer customer) {

        try {

            PreparedStatement deleteCustomer = DBConnection.getConnection().prepareStatement("DELETE " +
                    "FROM customers " +
                    "WHERE Customer_ID = ? ");
            deleteCustomer.setInt(1, customer.getCustomerID());
            System.out.println(customer.getCustomerID());
            deleteCustomer.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error.");
            alert.setContentText("Error. Please try again.");
            alert.showAndWait();
        }
    }

    /**
     * Deletes appointment from database.
     * @param appointment
     */
    public static boolean deleteAppt(Appointment appointment) {

        try {

            PreparedStatement deleteAppt = DBConnection.getConnection().prepareStatement("DELETE " +
                    "FROM appointments " +
                    "WHERE Appointment_ID = ? ");
            deleteAppt.setInt(1, appointment.getApptID());
            deleteAppt.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error.");
            alert.setContentText("Error. Please try again.");
            alert.showAndWait();
        }
        return true;
    }

    /**
     * Checks appointment to ensure no overlap with existing appointments.
     * @param apptStart
     * @param apptEnd
     */
    public static void hasOverlap(LocalDateTime apptStart, LocalDateTime apptEnd) {

//        LocalDateTime ldtStart = apptStart.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
//        LocalDateTime ldtEnd = apptEnd.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

        LocalDateTime ldtStart = apptStart.atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime ldtEnd = apptEnd.atZone(ZoneId.systemDefault()).toLocalDateTime();

        try {

            PreparedStatement conflictAppt = DBConnection.getConnection().prepareStatement("SELECT * " +
                    "FROM appointments " +
                    "WHERE Start BETWEEN ? AND ? " +
                    "OR End BETWEEN ? AND ? " +
                    "OR Start < ? AND End > ? " +
                    "OR Start = ? " +
                    "OR End = ? " +
                    "OR Start = ? AND End = ? ");

            conflictAppt.setTimestamp(1, Timestamp.valueOf(ldtStart));
            conflictAppt.setTimestamp(2, Timestamp.valueOf(ldtEnd));
            conflictAppt.setTimestamp(3, Timestamp.valueOf(ldtStart));
            conflictAppt.setTimestamp(4, Timestamp.valueOf(ldtEnd));
            conflictAppt.setTimestamp(5, Timestamp.valueOf(ldtStart));
            conflictAppt.setTimestamp(6, Timestamp.valueOf(ldtEnd));
            conflictAppt.setTimestamp(7, Timestamp.valueOf(ldtStart));
            conflictAppt.setTimestamp(8, Timestamp.valueOf(ldtEnd));
            conflictAppt.setTimestamp(9, Timestamp.valueOf(ldtStart));
            conflictAppt.setTimestamp(10, Timestamp.valueOf(ldtEnd));


            ResultSet resultSet = conflictAppt.executeQuery();
            while (resultSet.next()) {

                int apptID = resultSet.getInt(1);
                String title = resultSet.getString(2);
                String description = resultSet.getString(3);
                String location = resultSet.getString(4);
                String type = resultSet.getString(5);
                Timestamp start = resultSet.getTimestamp(6);
                Timestamp end = resultSet.getTimestamp(7);
                int customerID = resultSet.getInt(12);
                String userID = resultSet.getString(13);
                int contactID = resultSet.getInt(14);

                LocalDateTime apptStartLocal = start.toLocalDateTime();
                LocalDateTime apptEndLocal = end.toLocalDateTime();

                Appointment.ConflictingAppts.add(new Appointment(apptID, customerID, title, description, location, contactID,
                        type, apptStartLocal, apptEndLocal, userID));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Prelimiary appointment check before customer deletion due to foreign key constraints.
     * @param customer
     * @return
     */
    public static boolean checkAppointments (Customer customer) {

        boolean hasAppointments = false;

        try {

            PreparedStatement checkAppts = DBConnection.getConnection().prepareStatement("SELECT * " +
                    "FROM appointments " +
                    "WHERE Customer_ID = ? ");
            checkAppts.setInt(1, customer.getCustomerID());
            ResultSet resultSet = checkAppts.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getFetchSize() > 0) {
                    hasAppointments = true;
                    System.out.println("Resultset size = " + resultSet.getFetchSize());
                } else {
                    hasAppointments = false;
                    System.out.println("No appointments");
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error.");
            alert.setContentText("Error. Please try again.");
            alert.showAndWait();
        }
        return hasAppointments;
    }

    /**
     * Retrieves list of appointments for the current week.
     * @return
     */
    public static ObservableList<Appointment> getWeeklyAppts() {

        try {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PreparedStatement weeklyAppts = DBConnection.getConnection().prepareStatement("SELECT * " +
                "FROM appointments " +
                "WHERE Start >= NOW() " +
                "AND Start <= NOW() + INTERVAL 7 DAY " );
        ResultSet resultSet = weeklyAppts.executeQuery();
        while (resultSet.next()) {

            int apptID = resultSet.getInt("Appointment_ID");
            int customerID = resultSet.getInt("Customer_ID");
            String apptTitle = resultSet.getString("Title");
            String apptDescription = resultSet.getString("Description");
            String apptLocation = resultSet.getString("Location");
            int apptContact = resultSet.getInt("Contact_ID");
            String apptType = resultSet.getString("Type");
            String apptStart = resultSet.getString("Start");
            String apptEnd = resultSet.getString("End");
            String userID = resultSet.getString("User_ID");

            LocalDateTime ldtStart = LocalDateTime.parse(apptStart, dateTimeFormatter);
            LocalDateTime ldtEnd = LocalDateTime.parse(apptEnd, dateTimeFormatter);

            Appointment appointment = new Appointment(apptID, customerID, apptTitle, apptDescription, apptLocation, apptContact,
                    apptType, ldtStart, ldtEnd, userID);
            Appointment.FilteredAppts.add(appointment);
        }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return Appointment.FilteredAppts;
    }

    /**
     * Retrieves all appointments
     * @return
     */
    public static ObservableList<Appointment> getAllAppts() {

        try {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            PreparedStatement getAllAppts = DBConnection.getConnection().prepareStatement("SELECT * " +
                    "FROM appointments " );
            ResultSet resultSet = getAllAppts.executeQuery();
            while (resultSet.next()) {

                int apptID = resultSet.getInt("Appointment_ID");
                int customerID = resultSet.getInt("Customer_ID");
                String apptTitle = resultSet.getString("Title");
                String apptDescription = resultSet.getString("Description");
                String apptLocation = resultSet.getString("Location");
                int apptContact = resultSet.getInt("Contact_ID");
                String apptType = resultSet.getString("Type");
                String apptStart = resultSet.getString("Start");
                String apptEnd = resultSet.getString("End");
                String userID = resultSet.getString("User_ID");

                LocalDateTime ldtStart = LocalDateTime.parse(apptStart, dateTimeFormatter);
                LocalDateTime ldtEnd = LocalDateTime.parse(apptEnd, dateTimeFormatter);

                Appointment appointment = new Appointment(apptID, customerID, apptTitle, apptDescription, apptLocation, apptContact,
                        apptType, ldtStart, ldtEnd, userID);
                Appointment.FilteredAppts.add(appointment);
                Appointment.AlertAppts.add(appointment);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return Appointment.FilteredAppts;
    }

    /**
     * Retrieves appointments for the month.
     * @return
     */
    public static ObservableList<Appointment> getMonthlyAppts() {

        ObservableList<Appointment> FilteredAppts = FXCollections.observableArrayList();

        try {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            PreparedStatement weeklyAppts = DBConnection.getConnection().prepareStatement("SELECT * " +
                    "FROM appointments " +
                    "WHERE Start >= NOW() " +
                    "AND Start <= NOW() + INTERVAL 30 DAY " );
            ResultSet resultSet = weeklyAppts.executeQuery();
            while (resultSet.next()) {

                int apptID = resultSet.getInt("Appointment_ID");
                int customerID = resultSet.getInt("Customer_ID");
                String apptTitle = resultSet.getString("Title");
                String apptDescription = resultSet.getString("Description");
                String apptLocation = resultSet.getString("Location");
                int apptContact = resultSet.getInt("Contact_ID");
                String apptType = resultSet.getString("Type");
                String apptStart = resultSet.getString("Start");
                String apptEnd = resultSet.getString("End");
                String userID = resultSet.getString("User_ID");

                LocalDateTime ldtStart = LocalDateTime.parse(apptStart, dateTimeFormatter);
                LocalDateTime ldtEnd = LocalDateTime.parse(apptEnd, dateTimeFormatter);

                Appointment appointment = new Appointment(apptID, customerID, apptTitle, apptDescription, apptLocation, apptContact,
                        apptType, ldtStart, ldtEnd, userID);
                Appointment.FilteredAppts.add(appointment);
                FilteredAppts.add(appointment);
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return Appointment.FilteredAppts;
    }

    /**
     * Retrieves reports by month and type for report
     * @return monthly reports by type
     */
    public static ObservableList<Reports> MonthlyReportbyType() {

        ObservableList<Reports> MonthlyCount = FXCollections.observableArrayList();

        try {
            PreparedStatement getMonthlyAppts = DBConnection.getConnection().prepareStatement("SELECT MONTH(Start) " +
                    "AS 'Month', type as 'Type', COUNT(*) as 'Count' from appointments " +
                    "GROUP BY Month, Type ");
            ResultSet resultSet = getMonthlyAppts.executeQuery();
            while (resultSet.next()) {
                String month = resultSet.getString(1);
                String type = resultSet.getString(2);
                String count = resultSet.getString(3);

                Reports monthlyTypeReport = new Reports(month, type, count);
                MonthlyCount.add(monthlyTypeReport);
                Reports.ApptTypesByMonth.add(monthlyTypeReport);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return Reports.ApptTypesByMonth;
    }

    /**
     * Retrieves reports by location for report
     * @return monthly reports by location
     */
    public static ObservableList<LocationReport> ReportByLocation() {

        try {
            PreparedStatement getMonthlyAppts = DBConnection.getConnection().prepareStatement("SELECT Location " +
                    "AS 'Location', COUNT(*) as 'Count' from appointments " +
                    "GROUP BY Location " +
                    "ORDER BY Location ");
            ResultSet resultSet = getMonthlyAppts.executeQuery();
            while (resultSet.next()) {
                String location = resultSet.getString(1);
                String count = resultSet.getString(2);

                LocationReport.ApptsByLocation.add(new LocationReport(location, count));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return LocationReport.ApptsByLocation;
    }
}
