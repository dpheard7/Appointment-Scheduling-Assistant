/**
 *
 * @author Damon Heard
 */

package Main;

import Utils.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Views/Login.fxml"));
        primaryStage.setTitle("Scheduling Assistant");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.setMinWidth(1188);
        primaryStage.setMinHeight(414);
        primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {

        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }
}
