package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DBConnect;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	initializeDatabase();
        // Load the initial login view
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
        primaryStage.setTitle("Pet Store System Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Optional: Set a fixed size or make it not resizable
        primaryStage.setResizable(false);
    }
    
    private void initializeDatabase() {
        try {
            DBConnect dbConnect = new DBConnect();
            dbConnect.createUsersTable();  // This method creates the Users table if it does not exist
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
