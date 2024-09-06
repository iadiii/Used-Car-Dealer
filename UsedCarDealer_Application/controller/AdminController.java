package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.CarModel;
import model.DBConnect;

public class AdminController implements Initializable {

    @FXML
    private Button addbtn;
    @FXML
    private AnchorPane adminform;
    @FXML
    private Button availableCarsbtn;
    @FXML
    private Label make;
    @FXML
    private TextField makeField;
    @FXML
    private Button clearbtn;
    @FXML
    private Button deletebtn;
    @FXML
    private AnchorPane operationForm;
    @FXML
    private Button logoutbtn;
    @FXML
    private BorderPane modifyRecords;
    @FXML
    private Label nameofuser;
    @FXML
    private Label carId;
    @FXML
    private TextField carIdField;
    @FXML
    private Label price;
    @FXML
    private TextField priceField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField colorField;
    @FXML
    private TextField quantityField;
    @FXML
    private Label model;
    @FXML
    private TableColumn<CarModel, Integer> carIdCol;
    @FXML
    private TableColumn<CarModel, String> makeCol;
    @FXML
    private TableColumn<CarModel, Double> priceCol;
    @FXML
    private TableColumn<CarModel, String> modelCol;
    @FXML
    private TableColumn<CarModel, String> colorCol;
    @FXML
    private TableColumn<CarModel, String> quantityCol;
    @FXML
    private TableColumn<CarModel, String> yearCol;
    @FXML
    private TextField modelField;
    @FXML
    private TableView<CarModel> tableView;
    @FXML
    private BorderPane tabularForm;
    @FXML
    private Button updatebtn;
    @FXML
    private Label welcomeMessage;
    @FXML
    private Button homebtn;
    @FXML
    private Button manageCarsbtn;
    @FXML
    private AnchorPane navbar;
    
    

    private Connection connection;
    private PreparedStatement preparedStatement;
    private Statement statement;
    private ResultSet result;

    public ObservableList<CarModel> addCarsListData() {
    	ObservableList<CarModel> list = FXCollections.observableArrayList();
        String query = "SELECT carid, make, model, year, price, color, quantity FROM carmodel";
        try {
            connection = new DBConnect().getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();
            while (result.next()) {
                CarModel carModel = new CarModel(
                    result.getInt("carid"),
                    result.getString("make"),
                    result.getString("model"),
                    result.getInt("year"),
                    result.getDouble("price"),
                    result.getString("color"),
                    result.getInt("quantity") // Ensure this column exists in your database and is being fetched
                );
                list.add(carModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void showCarsListData() {
        ObservableList<CarModel> carList = addCarsListData();
        carIdCol.setCellValueFactory(new PropertyValueFactory<>("carId"));
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year")); // Corrected
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color")); // Corrected
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity")); // Corrected

        tableView.setItems(carList);
    }


    public void addCar() {
        // Updated SQL statement to include 'color' and 'quantity'
        String sql = "INSERT INTO carmodel (carid, make, model, year, price, color, quantity) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Check for null values in UI components which might not be initialized correctly
        if (carIdField == null || makeField == null || modelField == null || yearField == null ||
            priceField == null || colorField == null || quantityField == null) {
            showAlert(AlertType.ERROR, "Initialization Error", "One or more fields are not initialized properly.");
            return;
        }

        try {
            // Before proceeding, check if any fields are empty
            if (carIdField.getText().isEmpty() || makeField.getText().isEmpty() || 
                modelField.getText().isEmpty() || yearField.getText().isEmpty() || 
                priceField.getText().isEmpty() || colorField.getText().isEmpty() || 
                quantityField.getText().isEmpty()) {
                showAlert(AlertType.ERROR, "Error Message", "Don't leave field empty, fill them.");
                return;
            }

            // Attempt to parse integers and doubles from fields safely
            int carId = Integer.parseInt(carIdField.getText());
            int year = Integer.parseInt(yearField.getText()); // Safely parsing the year
            double price = Double.parseDouble(priceField.getText()); // Safely parsing the price
            int quantity = Integer.parseInt(quantityField.getText()); // Safely parsing the quantity

            // Prepare the SQL statement
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, carId);
            preparedStatement.setString(2, makeField.getText());
            preparedStatement.setString(3, modelField.getText());
            preparedStatement.setInt(4, year); // Correctly setting the year
            preparedStatement.setDouble(5, price); // Correctly setting the price
            preparedStatement.setString(6, colorField.getText());
            preparedStatement.setInt(7, quantity);

            // Execute update and handle user feedback
            preparedStatement.executeUpdate();
            showAlert(AlertType.INFORMATION, "Info Message", "Record inserted successfully.");
            showCarsListData();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Input Error", "Please enter valid numbers for Car ID, Year, Price, and Quantity.");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to insert the record into the database.");
            e.printStackTrace();
        }
    }




    public void updateCar() {
        // Expanded SQL query to update additional fields
        String sqlUpdate = "UPDATE carmodel SET make=?, model=?, year=?, price=?, color=?, quantity=? WHERE carid=?";
        
        try {
            connection = new DBConnect().getConnection();

            // Checking all fields for empty values to prevent SQL errors
            if (carIdField.getText().isEmpty() || makeField.getText().isEmpty() || modelField.getText().isEmpty() ||
                yearField.getText().isEmpty() || priceField.getText().isEmpty() || colorField.getText().isEmpty() ||
                quantityField.getText().isEmpty()) {
                showAlert(AlertType.ERROR, "Error Message", "Don't leave any fields empty, fill them all.");
            } else {
                preparedStatement = connection.prepareStatement(sqlUpdate);
                preparedStatement.setString(1, makeField.getText());
                preparedStatement.setString(2, modelField.getText());
                preparedStatement.setInt(3, Integer.parseInt(yearField.getText())); // Safely parsing the year as integer
                preparedStatement.setDouble(4, Double.parseDouble(priceField.getText())); // Safely parsing the price as double
                preparedStatement.setString(5, colorField.getText());
                preparedStatement.setInt(6, Integer.parseInt(quantityField.getText())); // Safely parsing the quantity as integer
                preparedStatement.setInt(7, Integer.parseInt(carIdField.getText())); // Car ID at the end as per the SQL statement

                preparedStatement.executeUpdate();
                showAlert(AlertType.INFORMATION, "Info Message", "Record updated successfully.");
                showCarsListData(); // Refresh the list to show the updated record
                clearFields();
            }
        } catch (NumberFormatException nfe) {
            showAlert(AlertType.ERROR, "Input Error", "Please check that all numeric fields contain valid numbers.");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to update the record.");
            e.printStackTrace();
        }
    }

    public void deleteCar() {
        String sqlDelete = "DELETE FROM carmodel WHERE carid=?";
        try {
            connection = new DBConnect().getConnection();
            if (carIdField.getText().isEmpty()) {
                showAlert(AlertType.ERROR, "Error Message", "Car ID field must not be empty.");
            } else {
                preparedStatement = connection.prepareStatement(sqlDelete);
                preparedStatement.setInt(1, Integer.parseInt(carIdField.getText()));
                preparedStatement.executeUpdate();
                showAlert(AlertType.INFORMATION, "Info Message", "Record deleted successfully.");
                showCarsListData();
                clearFields();
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to delete the record.");
            e.printStackTrace();
        }
    }

    public void logout() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            Stage stage = (Stage) logoutbtn.getScene().getWindow();
            stage.close();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearFields() {
        carIdField.setText("");
        makeField.setText("");
        modelField.setText("");
        priceField.setText("");
        yearField.setText("");  
        colorField.setText("");   
        quantityField.setText(""); 
    }


    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showCarsListData();
    }

    public void formSwitch(ActionEvent event) {
        if (event.getSource().equals(homebtn)) {
            operationForm.setVisible(false);
            tableView.setVisible(false);
        } else if (event.getSource().equals(manageCarsbtn)) {
            operationForm.setVisible(true);
            tableView.setVisible(true);
            showCarsListData();
        }
    }
}
