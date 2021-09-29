/**
 *
 * @author Damon Heard
 */

package Controllers;

import Model.Appointment;
import Model.LocationReport;
import Model.Reports;
import Utils.DBConnection;
import Utils.Queries;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    Stage stage;
    Parent scene;

    private ObservableList<String> ReportTypes = FXCollections.observableArrayList("by type and month", "of contact schedule", "by location");

    @FXML
    private AnchorPane ReportsViewChoice;

    @FXML
    private ComboBox<String> ReportsSelectCombo;

    @FXML
    private Button ReportsReturnToMain;

    @FXML
    private Button ReportsGenerateButton;
    @FXML
    private TableView<Model.Reports> ReportsMonthlyTable;

    @FXML
    private TableColumn<ReportsController, String> ReportMonthColumn;

    @FXML
    private TableColumn<ReportsController, String> ReportTypeColumn;

    @FXML
    private TableColumn<ReportsController, String> ReportCountColumn;
    @FXML
    private TableView<Appointment> ReportsContactSkedTable;

    @FXML
    private TableColumn<?, ?> ContactTableApptIDColumn;

    @FXML
    private TableColumn<?, ?> ContactTableCustIDColumn;

    @FXML
    private TableColumn<?, ?> ContactTableTitleColumn;

    @FXML
    private TableColumn<?, ?> ContactTableTypeColumn;

    @FXML
    private TableColumn<?, ?> ContactTableDescColumn;

    @FXML
    private TableColumn<Appointment, String> ContactTableStartColumn;

    @FXML
    private TableColumn<Appointment, String> ContactTableEndColumn;
    @FXML
    private TableColumn<?, ?> ContactTableContactIDCol;
    @FXML
    private TableView<LocationReport> ReportsByLocationTable;

    @FXML
    private TableColumn<?, ?> LocationColumn;

    @FXML
    private TableColumn<?, ?> LocationTableCountColumn;
    @FXML
    private ComboBox<String> ContactIDCombo;

    @FXML
    private Label ContactIDLabel;


    /**
     * Generates reports based on criteria outlined in combobox.
     * @param event user clicks generate report button.
     */
    @FXML
    void onActionGenerateReport(ActionEvent event) {

        String monthly = ReportTypes.get(0);
        String contactSchedule = ReportTypes.get(1);
        String location = ReportTypes.get(2);

        if (ReportsSelectCombo.getValue() == monthly) {

            ContactIDCombo.setVisible(false);
            ContactIDLabel.setVisible(false);
            ReportsMonthlyTable.setVisible(true);
            ReportsByLocationTable.setVisible(false);
            ReportsContactSkedTable.setVisible(false);

            Reports.ApptTypesByMonth.clear();

            ReportsMonthlyTable.setItems(Queries.MonthlyReportbyType());

            System.out.println(Reports.ApptTypesByMonth.size());

            ReportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("apptType"));
            ReportMonthColumn.setCellValueFactory(new PropertyValueFactory<>("apptMonth"));
            ReportCountColumn.setCellValueFactory(new PropertyValueFactory<>("apptCount"));
        }

        if (ReportsSelectCombo.getValue() == contactSchedule) {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            ContactIDCombo.setVisible(true);
            ContactIDLabel.setVisible(true);
            Appointment.ContactAppts.clear();
            ReportsMonthlyTable.setVisible(false);
            ReportsContactSkedTable.setVisible(true);
            ReportsByLocationTable.setVisible(false);

            try {

                PreparedStatement getContactSched = DBConnection.getConnection().prepareStatement("SELECT * " +
                        "FROM appointments " +
                        "WHERE Contact_ID = ? " +
                        "ORDER BY Start ");
                getContactSched.setInt(1, ContactIDCombo.getSelectionModel().getSelectedIndex() + 1);
                ResultSet resultSet = getContactSched.executeQuery();

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

                    ZonedDateTime zonedStart = ldtStart.atZone(ZoneId.systemDefault());
                    ZonedDateTime zonedEnd = ldtEnd.atZone(ZoneId.systemDefault());

                    LocalDateTime finalStart = zonedStart.toLocalDateTime();
                    LocalDateTime finalEnd = zonedEnd.toLocalDateTime();

                    Appointment.ContactAppts.add(new Appointment(apptID, customerID, apptTitle, apptDescription, apptLocation, apptContact,
                            apptType, finalStart, finalEnd, userID));
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }

            ReportsContactSkedTable.setItems(Appointment.ContactAppts);
            ContactTableApptIDColumn.setCellValueFactory(new PropertyValueFactory<>("apptID"));
            ContactTableTitleColumn.setCellValueFactory(new PropertyValueFactory<>("apptTitle"));
            ContactTableDescColumn.setCellValueFactory(new PropertyValueFactory<>("apptDescription"));
            ContactTableTypeColumn.setCellValueFactory(new PropertyValueFactory<>("apptType"));
            ContactTableCustIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            ContactTableContactIDCol.setCellValueFactory(new PropertyValueFactory<>("apptContact"));


            ContactTableStartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApptStartTime()
                    .atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault()).format(dateTimeFormatter)));
            ContactTableEndColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApptEndTime()
                    .atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault()).format(dateTimeFormatter)));
        }

        if (ReportsSelectCombo.getValue() == location) {

            ContactIDCombo.setVisible(false);
            ContactIDLabel.setVisible(false);

            LocationReport.ApptsByLocation.clear();

            ReportsMonthlyTable.setVisible(false);
            ReportsContactSkedTable.setVisible(false);
            ReportsByLocationTable.setVisible(true);

            ReportsByLocationTable.setItems(Queries.ReportByLocation());

            LocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            LocationTableCountColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        }
    }

    @FXML
    void onActionShowContacts(ActionEvent event) {

        if (ReportsSelectCombo.getSelectionModel().getSelectedIndex() == 1) {
            ContactIDCombo.setVisible(true);
            ContactIDLabel.setVisible(true);
        }

    }

    /**
     * Returns user to main screen.
     * @param event
     * @throws IOException
     */
    @FXML
    void onActionReturnToMain(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/MainScreen.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Hides tableviews until needed to view reports.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReportsSelectCombo.setItems(ReportTypes);
        ReportsMonthlyTable.setVisible(false);
        ReportsContactSkedTable.setVisible(false);
        ReportsByLocationTable.setVisible(false);
        ContactIDCombo.setVisible(false);
        ContactIDLabel.setVisible(false);

        ContactIDCombo.setItems(Appointment.Contacts);
    }
}
