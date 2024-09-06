package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CarModel;
import model.DBConnect;

public class CustomerController implements Initializable {

    @FXML
    private Button cus_addbtn;
    @FXML
    private Label cus_amount;
    @FXML
    private Label cus_carbuy;
    @FXML
    private Label cus_makebuy;
    @FXML
    private TableColumn<CarModel, String> makeCol;
    @FXML
    private ComboBox<String> cus_carCombo;
    @FXML
    private Button cus_buybtn;
    @FXML
    private AnchorPane cus_buyform;
    @FXML
    private Button cus_logout;
    @FXML
    private Label cus_name;
    @FXML
    private Button cus_paybtn;
    @FXML
    private TableColumn<CarModel, Integer> carIdCol;
    @FXML
    private TableColumn<CarModel, Double> priceCol;
    @FXML
    private Label cus_quantity;
    @FXML
    private Spinner<Integer> cus_quantitySpinner;
    @FXML
    private TableColumn<CarModel, String> modelCol;
    @FXML
    private TableView<CarModel> tableView;
    @FXML
    private Label cus_total;
    @FXML
    private AnchorPane cus_welcomeForm;
    @FXML
    private Label cus_welcomeMessage;
    @FXML
    private Button cus_home;
    private ObservableList<CarModel> carsList;
    @FXML
    private TableColumn<CarModel, String> yearCol;
    @FXML
    private TableColumn<CarModel, String> colorCol;
    @FXML
    private TableColumn<CarModel, String> quantityCol;
    
    private Connection connection;
    private PreparedStatement preparedStatement;
    private Statement statement;
    private ResultSet result;
    

    public ObservableList<CarModel> addCarsListData() {
        ObservableList<CarModel> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM carmodel";
        try {
        	DBConnect dbconnect = new DBConnect();
        	connection = dbconnect.getConnection();
             preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();
            while (result.next()) {
                CarModel carModel = new CarModel(result.getInt("carid"), result.getString("make"), result.getString("model"), result.getInt("year"), result.getDouble("price"), result.getString("color"), result.getInt("quantity"));
                list.add(carModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void showCarsListData() {
    	    carsList = addCarsListData();
    	    carIdCol.setCellValueFactory(new PropertyValueFactory<>("carId"));
    	    makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
    	    modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
    	    yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
    	    priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    	    colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
    	    quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    	    tableView.setItems(carsList);
    }

    public void formSwitch(ActionEvent event) {
        if (event.getSource().equals(cus_home)) {
            tableView.setVisible(false);
            cus_buyform.setVisible(false);
        } else if (event.getSource().equals(cus_buybtn)) {
            tableView.setVisible(true);
            cus_buyform.setVisible(true);
        }
    }


    public void logout() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Press OK to logout.");
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get().equals(ButtonType.OK)) {
            cus_logout.getScene().getWindow().hide();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int quantity;
	private SpinnerValueFactory<Integer> spinner;
	
	public void buySpinner(){
		spinner=new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10,0);
		cus_quantitySpinner.setValueFactory(spinner);
	}
	public void buyQuantity() {
		
		quantity=cus_quantitySpinner.getValue();
	}

    public void purchaseCar() {
        int quantity = cus_quantitySpinner.getValue();
        CarModel selectedCar = tableView.getSelectionModel().getSelectedItem();
        if (selectedCar != null && quantity > 0) {
            double totalCost = selectedCar.getPrice() * quantity;
            showConfirmationDialog(totalCost);
            showBillDetails();
        } else {
            showAlert(AlertType.WARNING, "No Car Selected", "Please select a car and set a quantity before purchasing.");
        }
    }

    private void showConfirmationDialog(double totalCost) {
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION, "The total cost is $" + new DecimalFormat("#0.00").format(totalCost) + ". Do you want to proceed?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            showAlert(AlertType.INFORMATION, "Purchase Successful", "Thank you for your purchase!");
        }
    }

    public void showBillDetails() {
        CarModel selectedCar = tableView.getSelectionModel().getSelectedItem();
        Integer quantity = cus_quantitySpinner.getValue();

        if (selectedCar == null || quantity == null || quantity <= 0) {
            showAlert(AlertType.ERROR, "Selection Error", "Please select a car and enter a valid quantity greater than zero.");
            return;
        }

        double totalPrice = selectedCar.getPrice() * quantity;
        cus_amount.setText(String.format("$%.2f", totalPrice)); // Update the label with the calculated total

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Bill Details");
        alert.setHeaderText(null);
        StringBuilder billDetails = new StringBuilder();
        billDetails.append("Car ID: ").append(selectedCar.getCarId()).append("\n");
        billDetails.append("Make: ").append(selectedCar.getMake()).append("\n");
        billDetails.append("Model: ").append(selectedCar.getModel()).append("\n");
        billDetails.append("Quantity: ").append(quantity).append("\n");
        billDetails.append("Total: $").append(String.format("%.2f", totalPrice)).append("\n");

        Label label = new Label(billDetails.toString());
        label.setStyle("-fx-font-size: 16;");
        alert.getDialogPane().setPrefSize(600, 400);
        alert.getDialogPane().setContent(label);
        alert.showAndWait();
    }

    @FXML
    private void onPayButtonAction(ActionEvent event) {
        showBillDetails();
    }



    
    public void addPurchase() {
        CarModel selectedCar = tableView.getSelectionModel().getSelectedItem();
        int quantity = cus_quantitySpinner.getValue();

        if (selectedCar != null && quantity > 0 && selectedCar.getQuantity() >= quantity) {
            double totalCost = selectedCar.getPrice() * quantity;
            int newQuantity = selectedCar.getQuantity() - quantity;

            try {
                String sqlUpdate = "UPDATE carmodel SET quantity=? WHERE carid=?";
                preparedStatement = connection.prepareStatement(sqlUpdate);
                preparedStatement.setInt(1, newQuantity);
                preparedStatement.setInt(2, selectedCar.getCarId());
                preparedStatement.executeUpdate();

                cus_amount.setText(String.format("$%.2f", totalCost));  // Update the total price on the UI
                showConfirmationDialog(totalCost);
                showCarsListData();  // Refresh the display to show updated quantity
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Database Error", "Failed to update the inventory.");
                e.printStackTrace();
            }
        } else {
            showAlert(AlertType.WARNING, "Purchase Error", "Not enough stock or invalid quantity.");
        }
    }



    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                CarModel selectedCar = tableView.getSelectionModel().getSelectedItem();
                // Optional: You can update any UI component or a variable here if needed
            }
        });
        cus_name.setText(LoginController.name);
        showCarsListData();
        populateCarComboBox();
        buySpinner();
        cus_paybtn.setOnAction(this::onPayButtonAction);
    }
    
    public void populateCarComboBox() {
        ObservableList<String> carOptions = FXCollections.observableArrayList();
        String query = "SELECT distinct carid FROM carmodel";  // Or "SELECT distinct make FROM carmodel" for car makes
        try {
            connection = new DBConnect().getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();
            while (result.next()) {
                carOptions.add(String.valueOf(result.getInt("carid")));  // Or result.getString("make")
            }
            cus_carCombo.setItems(carOptions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
