/**
 *
 * @author Damon Heard
 */

package Controllers;

import Model.Appointment;
import Model.Customer;
import Utils.DBConnection;
import Utils.Queries;
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
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerTable implements Initializable {

    Stage stage;
    Parent scene;

    private static Customer customerToDelete;
    private static Customer modifiedCustomer;
    public static Customer getModifiedCustomer() {
        return modifiedCustomer;
    }
    private static Appointment customerAppointments;

    ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    @FXML
    private TableView<Customer> CustomerTableview;

    @FXML
    private TableColumn<Customer, Integer> CustomerTableIDColumn;

    @FXML
    private TableColumn<Customer, String> CustomerTableNameColumn;

    @FXML
    private TableColumn<Customer, String> CustomerTableAddressColumn;

    @FXML
    private TableColumn<Customer, String> CustomerTablePostalColumn;

    @FXML
    private TableColumn<Customer, String> CustomerTablePhoneColumn;

    @FXML
    private TableColumn<Customer, String> CustomerTableCreatedColumn;

    @FXML
    private TableColumn<Customer, String> CustomerTableCreatedByColumn;

    @FXML
    private TableColumn<Customer, String> CustomerTableUpdatedColumn;

    @FXML
    private TableColumn<Customer, String> CustomerTableUpdatedByColumn;

    @FXML
    private TableColumn<Customer, Integer> CustomerTableDivisionIDColumn;

    @FXML
    private Button CustomerTableModifyButton;

    @FXML
    private Button CustomerTableCancelButton;

    @FXML
    private Button CustomerTableDeleteButton;

    @FXML
    private Button CustomerTableAddCustomer;

    /**
     * Cancels current action and directs user back to main screen.
     * @param event clicks cancel
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
     * Directs user to Add Customer screen.
     * @param event user clicks Add Customer
     * @throws IOException
     */
    @FXML
    void onActionAddCustomer(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/AddCustomer.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Directs user to Modify Customer screen and sends over relevant customer data.
     * @param event user clicks Modify Customer
     * @throws IOException
     */
    @FXML
    void onActionModifyCustomer(ActionEvent event) throws IOException, SQLException {

        modifiedCustomer = CustomerTableview.getSelectionModel().getSelectedItem();

        if (CustomerTableview.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No item selected.");
            alert.setContentText("Don't forget to select a customer to modify!");
            alert.show();
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/ModifyCustomer.fxml"));
        scene = loader.load();

        ModifyCustomer MCController = loader.getController();
        MCController.sendCustomer(modifiedCustomer);

        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Deletes selected customer after deleting their appointments according to SQL foreign key constraints.
     * @param event user clicks Delete Customer
     * @throws IOException
     */
    @FXML
    void onActionDeleteCustomer(ActionEvent event) throws IOException {

        customerToDelete = CustomerTableview.getSelectionModel().getSelectedItem();
        boolean hasAppointments = Queries.checkAppointments(customerToDelete);


        if (CustomerTableview.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No item selected.");
            alert.setContentText("Don't forget to select a customer to delete!");
            alert.show();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm delete");
        alert.setContentText("Are you sure you want to delete customer: " + customerToDelete.getCustomerName() + "? \n" +
                "Deleting this customer will delete all of their appointments. \nThis action cannot be undone.");
        Optional<ButtonType> confirm = alert.showAndWait();
        if(confirm.get() == ButtonType.OK) {
            try {

                if (hasAppointments) {
                    Queries.deleteAppt(customerAppointments);
                    Queries.deleteCustomer(customerToDelete);
                    CustomerTableview.getItems().remove(customerToDelete);
                } else {
                    Queries.deleteCustomer(customerToDelete);
                    CustomerTableview.getItems().remove(customerToDelete);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Customer deleted.");
            alert1.setContentText(customerToDelete.getCustomerName() + " and all associated appointments were deleted.");
            alert1.show();

        } if (confirm.get() == ButtonType.CANCEL) {
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/CustomerTable.fxml")));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /**
     * Populates Tableview with all customers.
     * @throws SQLException
     */
    public void populateCustomerTable() throws SQLException {

        try {

            PreparedStatement populateCustomerTableview = DBConnection.getConnection().prepareStatement("SELECT * from customers");
            ResultSet resultSet = populateCustomerTableview.executeQuery();
            allCustomers.removeAll();

            while (resultSet.next()) {

                int customerID = resultSet.getInt("Customer_ID");
                String name = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postal = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                String created = resultSet.getString("Create_Date");
                String createdBy = resultSet.getString("Created_By");
                String lastUpdate = resultSet.getString("Last_Update");
                String updatedBy = resultSet.getString("Last_Updated_By");
                String divisionID = resultSet.getString("Division_ID");

                Customer newCustomer = new Customer(customerID, name, address, postal, phone, created, createdBy, lastUpdate,
                        updatedBy, divisionID);

                allCustomers.add(newCustomer);
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Populates cells of Tabelview and launches program.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        CustomerTableview.setItems(allCustomers);
        CustomerTableIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        CustomerTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        CustomerTableAddressColumn.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        CustomerTablePostalColumn.setCellValueFactory(new PropertyValueFactory<>("customerPostal"));
        CustomerTablePhoneColumn.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        CustomerTableCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        CustomerTableCreatedByColumn.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        CustomerTableUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        CustomerTableUpdatedByColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        CustomerTableDivisionIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerDivision"));

        try {
            populateCustomerTable();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
