/**
 *
 * @author Damon Heard
 */

package Controllers;


import Model.Appointment;
import Model.User;
import Utils.Queries;
import Utils.Tools;
import com.mysql.cj.util.LogUtils;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Login implements Initializable {

    Stage stage;
    Parent scene;
    Locale userLocale = Locale.getDefault();
    ResourceBundle languageBundle = ResourceBundle.getBundle("languageProperties", userLocale);

    @FXML
    private TextField LoginUsernameField;
    @FXML
    private TextField LoginPasswordField;
    @FXML
    private Button LoginButton;
    @FXML
    private Label UsernameLabel;
    @FXML
    private Label PasswordLabel;
    @FXML
    private Label UserRegionLabel;

    /**
     * Logs user into the application.
     * @param event click 'Submit' button
     * @throws IOException
     */
    @FXML
    void onActionLoginButton(ActionEvent event) throws IOException {

        String username = LoginUsernameField.getText();
        String password = LoginPasswordField.getText();

        boolean loginAttempt = Queries.userLogin(username, password);

        if (LoginUsernameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(languageBundle.getString("Error"));
            alert.setContentText(languageBundle.getString("MissingUsername"));
            ButtonType okay = new ButtonType(languageBundle.getString("Okay"), ButtonBar.ButtonData.OK_DONE);
            alert.showAndWait();
        }
        if (LoginPasswordField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(languageBundle.getString("Error"));
            alert.setContentText(languageBundle.getString("MissingPassword"));
            ButtonType okay = new ButtonType(languageBundle.getString("Okay"), ButtonBar.ButtonData.OK_DONE);
            alert.showAndWait();
        }

        if (loginAttempt) {

            Tools.recordLoginSuccess(username);
            createStartEndApptTimesBase();

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/MainScreen.fxml")));
        } else {

             Tools.recordLoginFail(username);

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/Login.fxml")));
        }


        stage.setScene(new Scene(scene));
        stage.show();
    }

    public static void createStartEndApptTimesBase() {
        LocalDateTime ldtEST = LocalDateTime.of(1900, 1, 1, 8, 0, 0, 0);
        ZonedDateTime zdtEST = ldtEST.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime zdtStart = zdtEST.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime ldtStart = zdtStart.toLocalDateTime();
        LocalTime start = ldtStart.toLocalTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:00");

        LocalDateTime ldtEndEST = LocalDateTime.of(1900, 1, 1, 23, 0, 0, 0);
        ZonedDateTime zdtEndEST = ldtEndEST.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime zdtEnd = zdtEndEST.withZoneSameInstant(ZoneId.systemDefault());
        LocalDateTime ldtEnd = zdtEnd.toLocalDateTime();
        LocalTime end = ldtEnd.toLocalTime();

        do {
            Appointment.ApptStartTimes.add(LocalTime.parse(start.format(dateTimeFormatter)));
            Appointment.ApptEndTimes.add(LocalTime.parse(start.format(dateTimeFormatter)));
            start = start.plusHours(1);
        } while (!start.equals(end));
    }

    /**
     * Sets text labels according to user locale.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        UsernameLabel.setText(languageBundle.getString("Username"));
        PasswordLabel.setText(languageBundle.getString("Password"));
        LoginButton.setText(languageBundle.getString("Submit"));
        UserRegionLabel.setText(String.valueOf(ZoneId.systemDefault()));
    }
}
