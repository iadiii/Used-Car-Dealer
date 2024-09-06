package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.DBConnect;

/*  this is a login controller class
 *  Its takes you to different pages based on the role of the user.
 *  
 *   we have the following parameters
 *                  connect to hold database connection.
 *                  preparedStatement to hold the generated preparedStatement.
 *                  result to hold the resultset object.
 *                  name to hold the username of the user.
 *                  loginviewcontroller is reference variable of the current class.
 *                  
 *                  
 *   we have the following methods 
 *  initialize method: is called after the FXML file has been loaded, 
 *                     but before the UI controls declared in the FXML file
 *                     are injected into the controller's fields
 *                     
 *      adminLogin method is used to direct the user to the corresponding view 
 *                        according to the user role 
 *                        
 *      we also have @FXML annotated fields of the ui elements
 */
public class LoginController implements Initializable{
	 @FXML
	    private AnchorPane loginform;

	    @FXML
	    private Label loginhead;

	    @FXML
	    private PasswordField password;

	    @FXML
	    private Button signinbtn;

	    @FXML
	    private AnchorPane signinform;

	    @FXML
	    private TextField username;

	    @FXML
	    private Label welcomemessage;

	    @FXML
	    private AnchorPane welcomepage;
	
	    
	    
	    //database related variables 
	    private Connection connect;
	private PreparedStatement preparedStatement;
	private ResultSet result;
		

	 private Button loginbtn;

	 //variable used to hold the user name
	   public static String name; 
       public static LoginController loginviewcontroller;
       private static Connection conn;
	   public String getName() {//getter method
		return name;
	}
	//adminLogin method to navigate the user to the corresponding page according to the user role.
	   //makes use of user510 table to achieve this 
	public void adminLogin()
	   {String sql="select * from userList where username=? and password=?"; 
		   try {
			DBConnect dbconnect=new DBConnect();
			connect=dbconnect.getConnection();
			Alert alert;
			if(username.getText().isEmpty()|| password.getText().isEmpty())
			{
				alert=new Alert(AlertType.ERROR);
				alert.setTitle("Error Message");
				alert.setHeaderText(null);
				alert.setContentText("please fill the fields");
				alert.showAndWait();
			}
			else { name=username.getText();
				PreparedStatement preparedStatement=connect.prepareStatement(sql);
				preparedStatement.setString(1, username.getText());
             preparedStatement.setString(2, password.getText());
             result=preparedStatement.executeQuery();
             
             if(result.next()) {
             	//go to dashboard
             	alert=new Alert(AlertType.INFORMATION);
 				alert.setTitle("Information Message");
 				alert.setHeaderText(null);
 				loginviewcontroller.name=username.getText();
 				alert.setContentText("succesfull login");
 				alert.showAndWait();
 				signinbtn.getScene().getWindow().hide();//hide the login window
 				if(result.getString("role").equals("administrator")) 
 				{Parent root=FXMLLoader.load(getClass().getResource("/view/AdminView.fxml"));
 				
 				Stage stage=new Stage();
 				Scene scene=new Scene(root);
 				stage.initStyle(StageStyle.TRANSPARENT);
 				stage.setScene(scene);
 				stage.show();}
 				else {
 				
 				
 				Parent root=FXMLLoader.load(getClass().getResource("/view/CustomerView.fxml"));
 				Stage stage=new Stage();
 				Scene scene=new Scene(root);
 				stage.initStyle(StageStyle.TRANSPARENT);
 				stage.setScene(scene);
 				stage.show();}
 				
             }
             else {
             	//error message will show up
             	alert=new Alert(AlertType.ERROR);
 				alert.setTitle("Error Message");
 				alert.setHeaderText(null);
 				alert.setContentText("Incorrect username or  password");
 				alert.showAndWait();
             }
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

}
