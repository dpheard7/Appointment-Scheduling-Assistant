/**
 *
 * @author Damon Heard
 */
package Controllers;

import Model.Appointment;
import Model.User;
import Utils.DBConnection;
import Utils.Queries;
import Utils.Tools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddAppt implements Initializable {

    Stage stage;
    Parent scene;

    private ObservableList allCustomers = FXCollections.observableArrayList();

    @FXML
    private TextField AddApptIDField;

    @FXML
    private TextField AddApptTitleField;

    @FXML
    private TextField AddApptDescField;

    @FXML
    private Button AddApptSave;

    @FXML
    private Button AddApptCancel;

    @FXML
    private ComboBox <String> AddApptContactCombo;

    @FXML
    private ComboBox<String> AddApptLocationCombo;
    @FXML
    private ComboBox<LocalTime> AddApptStartTimeCombo;
    @FXML
    private ComboBox<LocalTime> AddApptEndTimeCombo;
    @FXML
    private TextField AddApptUserIDField;
    @FXML
    private TextField AddApptApptIDField;
    @FXML
    private ComboBox<String> AddApptTypeCombo;
    @FXML
    private DatePicker AddApptDatePicker;
    @FXML
    private ComboBox<String> AddApptCustomerCombo;

    /**
     * Cancels current action and returns user to main screen.
     * @param event
     * @throws IOException
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/MainScreen.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }



    /**
     * Saves new appointment. Contains validation implementations and appointment deconfliction.
     * @param event user clicks Save
     */
    @FXML
    void onActionSave(ActionEvent event) {

        try {

            LocalDate apptDate = AddApptDatePicker.getValue();
            String apptStartString = String.valueOf(AddApptStartTimeCombo.getValue());
            String apptEndString = String.valueOf(AddApptEndTimeCombo.getValue());
            LocalDateTime apptStartLocal = Tools.dateTimeFormatter(apptDate, apptStartString);
            LocalDateTime apptEndLocal = Tools.dateTimeFormatter(apptDate, apptEndString);

            int apptID = Tools.getApptID();
            int customerID = AddApptCustomerCombo.getSelectionModel().getSelectedIndex() + 1;
            String apptTitle = AddApptTitleField.getText();
            String apptDescription = AddApptDescField.getText();
            String apptLocation = AddApptLocationCombo.getSelectionModel().getSelectedItem();
            int apptContact = AddApptContactCombo.getSelectionModel().getSelectedIndex() + 1;
            String apptType = AddApptTypeCombo.getSelectionModel().getSelectedItem();
            String userID = String.valueOf(User.getUserID());

                if (Tools.validateAppt(apptTitle, apptDescription, apptLocation, apptContact, apptType, apptStartLocal, apptEndLocal)) {

                    if (AddApptCustomerCombo.getSelectionModel().isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("No customer selected.");
                        alert.showAndWait();
                    }

                    Queries.hasOverlap(apptStartLocal, apptEndLocal);
                    System.out.println(Appointment.ConflictingAppts.size());

                    if (Appointment.ConflictingAppts.size() >= 1) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Conflicting appointment on the schedule. Please try another date and/or time.");
                        alert.showAndWait();
                        Appointment.ConflictingAppts.clear();

                        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/AddAppt.fxml")));
                        stage.setScene(new Scene(scene));
                        stage.show();
                    } else {
                        Appointment.ConflictingAppts.clear();

                        Queries.insertAppointment(new Appointment(apptID, customerID, apptTitle, apptDescription, apptLocation, apptContact,
                                apptType, apptStartLocal, apptEndLocal, userID));
                    }
                }

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/MainScreen.fxml")));
            stage.setScene(new Scene(scene));
            stage.show();

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
     * Populates the combobox containing the list of customers
     * @throws SQLException
     */
    private void populateCustomerCombo() throws SQLException {

        String getCustomers = "SELECT Customer_Name " +
                "FROM customers";
        PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(getCustomers);
        ResultSet rs = preparedStatement.executeQuery(getCustomers);

        while (rs.next()) {

            AddApptCustomerCombo.getItems().add(rs.getString(1));
        }
    }

    /**
     * Implements methods to populate comboboxes.
     */
    public void populateCombos() {

        try {
            AddApptContactCombo.setItems(Appointment.Contacts);
            AddApptLocationCombo.setItems(Appointment.ApptLocations);
            AddApptTypeCombo.setItems(Appointment.ApptType);
            AddApptStartTimeCombo.setItems(Appointment.ApptStartTimes);
            AddApptEndTimeCombo.setItems(Appointment.ApptEndTimes);
            populateCustomerCombo();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateCombos();
    }
}
