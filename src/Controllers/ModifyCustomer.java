package Controllers;

import Model.Appointment;
import Model.Customer;
import Model.DataRecord;
import Utils.DBConnection;
import Utils.Queries;
import Utils.Tools;
import javafx.beans.Observable;
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
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModifyCustomer implements Initializable {

    Stage stage;
    Parent scene;

    private Customer customerToModify;
    private Customer thisCustomer;
    private Customer appointments;

    private ObservableList<Appointment> customerAppts = FXCollections.observableArrayList();
    private static Appointment modifiedAppt;

    public static Appointment getModifiedAppt() {
        return modifiedAppt;
    }

    @FXML
    private TextField ModCustIDField;
    @FXML
    private TextField ModCustNameField;
    @FXML
    private TextField ModCustAddressField;
    @FXML
    private TextField ModCustPostalField;
    @FXML
    private TextField ModCustPhoneField;
    @FXML
    private Label CustZipField;
    @FXML
    private Button ModCustSaveButton;
    @FXML
    private Button ModCustCancelButton;
    @FXML
    private ComboBox<String> ModCustCountryCombo;
    @FXML
    private ComboBox<String> ModCustStateCombo;
    @FXML
    private Button ModCustDeleteApptButton;
    @FXML
    private Button ModCustModApptButton;

    /**
     * Cancels current action and returns user to main screen.
     * @param event
     * @throws IOException
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/CustomerTable.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Sends customer data to Modify Customer screen and prepopulates fields. Sets value for country combobox.
     * @param selectedCustomer
     * @throws SQLException
     */
    public void sendCustomer (Customer selectedCustomer) throws SQLException {

        int divisionID = Integer.parseInt(selectedCustomer.getCustomerDivision());

        if (divisionID <= 54) {
            ModCustCountryCombo.setValue("U.S");
        }
        else if (divisionID >= 60 && divisionID <= 72) {
            ModCustCountryCombo.setValue("Canada");
        }
        else if (divisionID >= 101 && divisionID <= 104) {
            ModCustCountryCombo.setValue("UK");
        }

        customerToModify = selectedCustomer;

        ModCustIDField.setText(String.valueOf(Integer.parseInt(String.valueOf(selectedCustomer.getCustomerID()))));
        ModCustNameField.setText(selectedCustomer.getCustomerName());
        ModCustAddressField.setText(String.valueOf(selectedCustomer.getCustomerAddress()));
        ModCustPhoneField.setText(String.valueOf(selectedCustomer.getCustomerPhone()));
        ModCustPostalField.setText(String.valueOf(selectedCustomer.getCustomerPostal()));
        ModCustStateCombo.setValue(Queries.getDivision(selectedCustomer));
    }

    /**
     * Saves modified customer. Contains validation implementations.
     * @param event user clicks Save
     */
    @FXML
    void onActionSave(ActionEvent event) throws IOException {

        int customerID = customerToModify.getCustomerID();

        try {

            String customerName = ModCustNameField.getText();
            String customerAddress = ModCustAddressField.getText();
            String customerPostal = ModCustPostalField.getText();
            String customerPhone = ModCustPhoneField.getText();
            String customerDivision = ModCustStateCombo.getSelectionModel().getSelectedItem();
            String createDate = Customer.createDate;
            String createdBy = Customer.createdBy;
            String lastUpdate = Customer.lastUpdate;
            String lastUpdatedBy = Customer.lastUpdatedBy;

            if (!Tools.validateCustomer(customerName, customerAddress, customerPostal, customerPhone,
                    customerDivision)) {

                Customer modifiedCustomer = new Customer(customerID, customerName, customerAddress, customerPhone,
                        customerPostal, createDate, createdBy, lastUpdate, lastUpdatedBy, customerDivision);

                DataRecord.addCustomer(modifiedCustomer);
                Queries.updateCustomer(modifiedCustomer);

                for (Appointment appointment : customerAppts) {
                    modifiedCustomer.addCustAppt(appointment);
                }
            }
        }
        catch (NumberFormatException numberFormatException) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Illegal number format. Please review your entry and try again.");
            alert.showAndWait();
            numberFormatException.printStackTrace();
        } catch (NullPointerException nullPointerException) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("A field is blank. Please try again.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid entry. Please check and try again.");
            alert.showAndWait();
            e.printStackTrace();
        }

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/CustomerTable.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Populates country combobox.
     * @throws SQLException
     */
    private void populateCountryCombo() throws SQLException {

        String sqlCountry = "SELECT Country FROM countries";
        PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sqlCountry);
        ResultSet rs = preparedStatement.executeQuery(sqlCountry);

        while (rs.next()) {

            ModCustCountryCombo.getItems().add(rs.getString(1));
        }
    }

    /**
     * Populates state/province combobox.
     * @throws SQLException
     */
    private void populateDivisionCombo(String country) throws SQLException {

        try {

            DBConnection.getConnection();
            ObservableList<String> Stuff = FXCollections.observableArrayList();
            String queryUS = "SELECT first_level_divisions.Division FROM first_level_divisions, countries "
                    + "WHERE first_level_divisions.COUNTRY_ID = countries.Country_ID "
                    + "AND countries.Country = \"" + country + "\"";
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(queryUS);

            while (resultSet.next()) {
                Stuff.add(resultSet.getString(1));
                ModCustStateCombo.setItems(Stuff);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Populates state/province combobox according to which country is selected.
     * @throws SQLException
     */
    @FXML
    void onActionPopulateDivision(ActionEvent event) {

        String country = ModCustCountryCombo.getValue();
        if (country == null || country.isBlank()) {
            System.out.println("Invalid selection");
            return;
        }
        try {
            populateDivisionCombo(country);
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database error.");
            alert.setContentText("Error accessing first-level division table. Please try again.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            populateCountryCombo();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }


}
