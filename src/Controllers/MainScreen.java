/**
 *
 * @author Damon Heard
 */

package Controllers;

import Main.Main;
import Model.Appointment;
import Utils.DBConnection;
import Utils.Queries;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class MainScreen implements Initializable {

    Stage stage;
    Parent scene;

    private static Appointment appointmentToDelete;
    private static Appointment appointmentToModify;

    ObservableList<Appointment> ApptData = FXCollections.observableArrayList();
    ObservableList<Appointment> AlertAppts = Queries.getAllAppts();


    @FXML
    private ToggleGroup FilterApptsTG;
    @FXML
    private TableView<Appointment> MainScreenTable;
    @FXML
    private TableColumn<?, String> MainApptIDColumn;
    @FXML
    private TableColumn<?, String> MainCustIDColumn;
    @FXML
    private TableColumn<?, ?> MainUserIDColumn;
    @FXML
    private TableColumn<?, String> MainTitleColumn;
    @FXML
    private TableColumn<?, String> MainDescColumn;
    @FXML
    private TableColumn<?, String> MainLocationColumn;
    @FXML
    private TableColumn<?, String> MainContactIDColumn;
    @FXML
    private TableColumn<?, String> MainTypeColumn;
    @FXML
    private TableColumn<Appointment, String> MainStartColumn;
    @FXML
    private TableColumn<Appointment, String> MainEndColumn;
    @FXML
    private RadioButton MainScreenWeekFilterRadio;
    @FXML
    private RadioButton MainScreenMonthFilterRadio;
    @FXML
    private Button MainScreenAddCustButton;
    @FXML
    private Button MainScreenAddApptButton;
    @FXML
    private Button MainScreenLogoutButton;
    @FXML
    private RadioButton MainScreenViewAllRadio;
    @FXML
    private DatePicker MainScreenDatePicker;
    @FXML
    private Button MainScreenViewCustButton;
    @FXML
    private Button MainScreenReportsButton;
    @FXML
    private Button MainScreenModifyApptButton;
    @FXML
    private Button MainScreenDeleteApptButton;
    @FXML
    private Label MainAlertLabel;

    @FXML
    void onActionAddAppt(ActionEvent event) throws IOException {

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/AddAppt.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Deletes selected appointment
     * @param event click delete appointment button
     */
    @FXML
    void onActionDeleteAppt(ActionEvent event) {

        appointmentToDelete = MainScreenTable.getSelectionModel().getSelectedItem();

        if (MainScreenTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No item selected.");
            alert.setContentText("Don't forget to select an appointment to delete!");
            alert.show();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm delete");
        alert.setContentText("Are you sure you want to delete appointment: " + appointmentToDelete.getApptID() + "? \n" +
                "\nThis action cannot be undone.");
        Optional<ButtonType> confirm = alert.showAndWait();
        if(confirm.get() == ButtonType.OK) {
            Queries.deleteAppt(appointmentToDelete);
            MainScreenTable.getItems().remove(appointmentToDelete);
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Appointment deleted.");
            alert1.setContentText("Appointment ID " + appointmentToDelete.getApptID() + ", type " + appointmentToDelete.getApptType() + " was deleted.");
            alert1.show();
        }
    }

    /**
     * Directs user to ModifyAppt view where the appointment can be modified.
     * @param event
     * @throws IOException
     * @throws SQLException
     * @throws ParseException
     */
    @FXML
    void onActionModifyAppt(ActionEvent event) throws IOException, SQLException, ParseException {

        appointmentToModify = MainScreenTable.getSelectionModel().getSelectedItem();

        if (MainScreenTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No item selected.");
            alert.setContentText("Don't forget to select an appointment to modify!");
            alert.show();
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/ModifyAppt.fxml"));
        scene = loader.load();

        ModifyAppt MAController = loader.getController();
        MAController.sendAppointment(appointmentToModify);

        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Displays all appointments in tableview according to radio button selection
     * @param event clicks All radio button
     */
    @FXML
    void onActionDisplayAll(ActionEvent event) {

        clearTable();
        MainScreenTable.setItems(Queries.getAllAppts());
    }

    /**
     * Displays month's appointments in tableview according to radio button selection
     * @param event clicks Month radio button
     */
    @FXML
    void onActionDisplayByMonth(ActionEvent event) {

        clearTable();
        MainScreenTable.setItems(Queries.getMonthlyAppts());
    }

    public void clearTable() {

        ApptData.clear();
        ApptData.removeAll();
        Appointment.FilteredAppts.clear();
        MainScreenTable.getItems().removeAll();
        MainScreenTable.getItems().clear();
    }

    /**
     * Displays week's appointments in tableview according to radio button selection
     * @param event clicks Week radio button
     */
    @FXML
    void onActionDisplayByWeek(ActionEvent event) {

        clearTable();
        MainScreenTable.refresh();
        MainScreenTable.setItems(Queries.getWeeklyAppts());
    }

    /**
     * Takes user to Reports screen
     * @param event clicks Reports button
     * @throws IOException
     */
    @FXML
    void onActionGetReports(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/Reports.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionLogout(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Takes user to Customer Table screen to view all customers
     * @param event clicks View Customers button
     * @throws IOException
     */
    @FXML
    void onActionViewCustomers(ActionEvent event) throws IOException {

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/CustomerTable.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Populates tableview on main screen with appointments
     */
    public void populateMainTable () {

        try {

            PreparedStatement populateMainTableview = DBConnection.getConnection().prepareStatement("SELECT Appointment_ID, " +
                    "Customer_ID, Title, Description, Location, Contact_ID, Type, Start, End, User_ID " +
                    "FROM appointments " +
                    "ORDER BY 'Start'");
            ResultSet resultSet = populateMainTableview.executeQuery();

            ApptData.removeAll();

            while (resultSet.next()) {

                ZoneId zoneId = ZoneId.systemDefault();
                ZoneId zoneUTC = ZoneId.of("UTC");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                int appointmentID = resultSet.getInt("Appointment_ID");
                int customerID = resultSet.getInt("Customer_ID");
                String title = resultSet.getString("Title");
                String description = resultSet.getString("Description");
                String location = resultSet.getString("Location");
                int contactID = resultSet.getInt("Contact_ID");
                String type = resultSet.getString("Type");
                String userID = resultSet.getString("User_ID");

                String start = resultSet.getString("Start");
                String end = resultSet.getString("End");

                //UTC time
                LocalDateTime ldtStart = LocalDateTime.parse(start, dateTimeFormatter);
                LocalDateTime ldtEnd = LocalDateTime.parse(end, dateTimeFormatter);

                //Local time
                ZonedDateTime zonedApptStart = ldtStart.atZone(zoneUTC).withZoneSameInstant(zoneId);
                ZonedDateTime zonedApptEnd = ldtEnd.atZone(zoneUTC).withZoneSameInstant(zoneId);

                ApptData.add(new Appointment(appointmentID, customerID, title, description, location, contactID, type, ldtStart, ldtEnd, userID));
            }
        } catch (SQLException sqlException) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("SQL Error. Please try again.");
            alert.showAndWait();
            sqlException.printStackTrace();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid entry. Please check and try again.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Alerts user to upcoming appointments based on user's system  time. Contains lambda expression to efficiently
     * iterate over all appointments and alert user to upcoming appointments
     */
    public void upcomingApptAlert() {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Appointment.FilteredAppts.forEach(appointment -> {

            LocalDateTime ldtStart = appointment.getApptStartTime();
            ZonedDateTime zNow = ZonedDateTime.now();
            ZonedDateTime zNow1 = zNow.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime zStart = ZonedDateTime.of(ldtStart, ZoneOffset.UTC);
            String localeTime = appointment.getApptStartTime()
                    .atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault()).format(dateTimeFormatter);

            if (zStart.isAfter(zNow1) && zStart.isBefore(zNow1.plusMinutes(15)))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Upcoming appointment.");
                alert.setContentText("Appointment " + appointment.getApptID() + " starting at " + localeTime);
                alert.setWidth(500);
                alert.setHeight(300);
                alert.showAndWait();
            } else {
                MainAlertLabel.setVisible(true);
            }
        });
    }

    /**
     * Initializes app. Implements appointment method, displays appointments according to user timezone, populates main table.
     * Contains lambdas to set cell values.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        upcomingApptAlert();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());

        populateMainTable();

        MainScreenTable.setItems(ApptData);
        MainApptIDColumn.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        MainTitleColumn.setCellValueFactory(new PropertyValueFactory<>("apptTitle"));
        MainDescColumn.setCellValueFactory(new PropertyValueFactory<>("apptDescription"));
        MainLocationColumn.setCellValueFactory(new PropertyValueFactory<>("apptLocation"));
        MainContactIDColumn.setCellValueFactory(new PropertyValueFactory<>("apptContact"));
        MainTypeColumn.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        MainCustIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        MainUserIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        MainStartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApptStartTime()
                .atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault()).format(dateTimeFormatter)));
        MainEndColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApptEndTime()
                .atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault()).format(dateTimeFormatter)));


    }
}