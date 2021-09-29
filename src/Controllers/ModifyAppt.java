package Controllers;

import Model.Appointment;
import Utils.Queries;
import Utils.Tools;
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
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class ModifyAppt implements Initializable {

    Stage stage;
    Parent scene;

    private static Appointment appointmentToModify;


    @FXML
    private TextField ModApptCustIDField;

    @FXML
    private TextField ModApptTitleField;
    @FXML
    private TextField ModApptOGDateField;

    @FXML
    private TextField ModApptDescField;

    @FXML
    private Button ModApptSave;

    @FXML
    private Button ModApptCancel;

    @FXML
    private ComboBox<String> ModApptContactCombo;

    @FXML
    private ComboBox<LocalTime> ModApptStartTimeCombo;

    @FXML
    private ComboBox<LocalTime> ModApptEndTimeCombo;

    @FXML
    private TextField ModApptApptIDField;

    @FXML
    private ComboBox<String> ModApptTypeCombo;

    @FXML
    private DatePicker ModApptDatePicker;
    @FXML
    private ComboBox<String> ModApptLocationCombo;

    /**
     * Cancels current action and returns user to main screen.
     * @param event
     * @throws IOException
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/MainScreen.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Sends appointment data to Modify Appointment screen and prepopulates fields.
     * @param selectedAppointment
     * @throws SQLException
     */
    public void sendAppointment(Appointment selectedAppointment) throws SQLException {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate apptLocalDate = selectedAppointment.getApptStartTime().toLocalDate();

        String apptStartTime = selectedAppointment.getApptStartTime().format(timeFormatter);
        String apptEndTime = selectedAppointment.getApptEndTime().format(timeFormatter);

        // new times
        LocalDateTime apptStart = selectedAppointment.getApptStartTime();
        LocalDateTime apptEnd = selectedAppointment.getApptEndTime();

        ZonedDateTime zonedStart = apptStart.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedEnd = apptEnd.atZone(ZoneId.of("UTC"));

        ZonedDateTime localZonedStart = zonedStart.withZoneSameInstant(ZoneId.systemDefault());
        ZonedDateTime localZonedEnd = zonedEnd.withZoneSameInstant(ZoneId.systemDefault());

        LocalDateTime localStart = localZonedStart.toLocalDateTime();
        LocalDateTime localEnd = localZonedEnd.toLocalDateTime();

        LocalTime startTime = localStart.toLocalTime();
        LocalTime endTime = localEnd.toLocalTime();
        // end

        appointmentToModify = selectedAppointment;

        ModApptCustIDField.setText(String.valueOf(Integer.parseInt(String.valueOf(selectedAppointment.getCustomerID()))));
        ModApptApptIDField.setText(String.valueOf(Integer.parseInt(String.valueOf(selectedAppointment.getApptID()))));
        ModApptTitleField.setText(String.valueOf(selectedAppointment.getApptTitle()));
        ModApptDescField.setText(String.valueOf(selectedAppointment.getApptDescription()));
        ModApptLocationCombo.setValue(Queries.getApptLocation(selectedAppointment));
        ModApptContactCombo.setValue(convertContactToName());
        ModApptTypeCombo.setValue(String.valueOf(selectedAppointment.getApptType()));
        ModApptDatePicker.setValue(apptLocalDate);
        ModApptOGDateField.setText(String.valueOf((selectedAppointment.getApptStartTime())));
        ModApptStartTimeCombo.setValue(startTime);
        ModApptEndTimeCombo.setValue(endTime);
    }

    /**
     * Populates comboboxes with selected methods.
     */
    public void populateCombos() {

        try {
            ModApptContactCombo.setItems(Appointment.Contacts);
            ModApptLocationCombo.setItems(Appointment.ApptLocations);
            ModApptTypeCombo.setItems(Appointment.ApptType);
            ModApptStartTimeCombo.setItems(Appointment.ApptStartTimes);
            ModApptEndTimeCombo.setItems(Appointment.ApptEndTimes);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Converts contact to string for populating combobox method.
     * @return contact's name.
     */
    private String convertContactToName() {

         String contactName = "";

         if (appointmentToModify.getApptContact() == 1) {
             contactName = "Anika Costa";
         } else if (appointmentToModify.getApptContact() == 2) {
             contactName = "Daniel Garcia";
         } else if (appointmentToModify.getApptContact() == 3) {
            contactName = "Li Lee";
         } return contactName;
    }

    /**
     * Saves modified appointment. Contains validation implementations and appointment deconfliction.
     * @param event user clicks Save
     */
    @FXML
    void onActionSave(ActionEvent event) throws IOException {

        LocalDate apptDate = ModApptDatePicker.getValue();
        String apptStartString = String.valueOf(ModApptStartTimeCombo.getValue());
        String apptEndString = String.valueOf(ModApptEndTimeCombo.getValue());
        LocalDateTime apptStartLocal = Tools.dateTimeFormatter(apptDate, apptStartString);
        LocalDateTime apptEndLocal = Tools.dateTimeFormatter(apptDate, apptEndString);

        try {

            int apptID = appointmentToModify.getApptID();
            int customerID = appointmentToModify.getCustomerID();
            String apptTitle = ModApptTitleField.getText();
            String apptDescription = ModApptDescField.getText();
            String apptLocation = ModApptLocationCombo.getSelectionModel().getSelectedItem();
            int apptContact = ModApptContactCombo.getSelectionModel().getSelectedIndex() + 1;
            String apptType = ModApptTypeCombo.getSelectionModel().getSelectedItem();
            String userID = appointmentToModify.getUserID();

            if (Tools.validateAppt(apptTitle, apptDescription, apptLocation, apptContact, apptType, apptStartLocal, apptEndLocal)) {

                Queries.hasOverlap(apptStartLocal, apptEndLocal);
                System.out.println(Appointment.ConflictingAppts.size());

                if (Appointment.ConflictingAppts.size() >= 1) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Conflicting appointment on the schedule. Please try another date and/or time.");
                    alert.showAndWait();

//                    stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
//                    scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/ModifyAppt.fxml")));
//                    stage.setScene(new Scene(scene));
//                    stage.show();
                } else {

                    Queries.updateAppointment(new Appointment(apptID, customerID, apptTitle, apptDescription, apptLocation, apptContact,
                            apptType, apptStartLocal, apptEndLocal, userID));
                }
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
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/MainScreen.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Appointment.ConflictingAppts.clear();
        populateCombos();
    }

}
