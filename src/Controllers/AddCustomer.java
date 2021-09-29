/**
 *
 * @author Damon Heard
 */

package Controllers;

import Model.Customer;
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
import java.sql.Statement;
import java.util.*;

public class AddCustomer implements Initializable {

    Stage stage;
    Parent scene;
    boolean customerMatch = true;

    @FXML
    private TextField AddCustIDField;
    @FXML
    private TextField AddCustNameField;
    @FXML
    private TextField AddCustAddressField;
    @FXML
    private TextField AddCustAddressField1;
    @FXML
    private TextField AddCustPostalField;
    @FXML
    private TextField AddCustPhoneField;
    @FXML
    private Button AddCustSaveButton;
    @FXML
    private Button AddCustCancelButton;
    @FXML
    private ComboBox<String> AddCustCountryCombo;
    @FXML
    private ComboBox<String> AddCustStateCombo;

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
     * Saves new customer. Contains validation implementations and appointment deconfliction.
     * @param event user clicks Save
     */
    @FXML
    void onActionSave(ActionEvent event) {

        try {

            int customerID = Tools.getCustomerID();
            String customerName = AddCustNameField.getText();
            String customerAddress = AddCustAddressField.getText();
            String customerPostal = AddCustPostalField.getText();
            String customerPhone = AddCustPhoneField.getText();
            String customerDivision = AddCustStateCombo.getSelectionModel().getSelectedItem();
//            String customerCountry = AddCustCountryCombo.getSelectionModel().getSelectedItem();
            String createDate = Customer.createDate;
            String createdBy = Customer.createdBy;
            String lastUpdate = Customer.lastUpdate;
            String lastUpdatedBy = Customer.lastUpdatedBy;

            if (!Tools.validateCustomer(customerName, customerAddress, customerPostal, customerPhone,
                    customerDivision)) {

                Queries.insertCustomer(new Customer(customerID, customerName, customerAddress, customerPhone, customerPostal,
                        createDate, createdBy, lastUpdate, lastUpdatedBy, customerDivision));
            }

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/CustomerTable.fxml")));
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
     * Populates the combobox containing the list of countries.
     * @throws SQLException
     */
    private void populateCountryCombo() throws SQLException {

        DBConnection.getConnection(); // do i need this?

        String sqlCountry = "SELECT Country FROM countries";
        PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sqlCountry);
        ResultSet rs = preparedStatement.executeQuery(sqlCountry);

        while (rs.next()) {

            AddCustCountryCombo.getItems().add(rs.getString(1));
        }
    }

    /**
     * Populates the combobox containing the list of divisions according to which country is selected.
     * @throws SQLException
     */
    private void populateDivisionCombo(String country) throws SQLException {

        try {
            DBConnection.getConnection();

            ObservableList<String> Divisions = FXCollections.observableArrayList();


            String queryUS = "SELECT first_level_divisions.Division FROM first_level_divisions, countries "
                    + "WHERE first_level_divisions.COUNTRY_ID = countries.Country_ID "
                    + "AND countries.Country = \"" + country + "\"";
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(queryUS);

            while (resultSet.next()) {

                Divisions.add(resultSet.getString(1));
                AddCustStateCombo.setItems(Divisions);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Populates the combobox containing the list of divisions according to which country is selected.
     * @throws SQLException
     */
    @FXML
    private void onActionPopulateDivision(ActionEvent actionEvent) {

        String country = AddCustCountryCombo.getValue();
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
