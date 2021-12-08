package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewProfileController implements Initializable{
	
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_profile;
	@FXML private Button button_search;
	@FXML private Button button_edit_director;
	@FXML private Button button_edit_movie;
	@FXML private Button button_edit_rating;
	@FXML private Button button_rateMovie;

	@FXML private Button button_save;

	@FXML private TextField tf_edit_first_name;
	@FXML private TextField tf_edit_last_name;
	@FXML private TextField tf_edit_username;

	@FXML private Label label_display_name;
	@FXML private Label label_display_email_type;
	
	@FXML private Label label_name;
	@FXML private Label label_account_type;

	@FXML private VBox nav_admin;
	@FXML private VBox nav_guest;

	@FXML private Pane pane_display_info;
	@FXML private Pane pane_edit_info;

	private String firstName = "",lastName = "", accountType = "";
	int userID;

	/**
	 * Method that runs listening for Action Events.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Assigned the action that is caused by the "Logout" button being clicked.
		button_logout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "LogIn.fxml", "Log in!", -1, null, null,null);
			}
		});

		button_rateMovie.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "RateMoviePage.fxml", "Rate a Movie", userID, firstName, lastName, accountType);
			}
		});

		button_search.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "SearchPage.fxml", "Search Movies", userID, firstName, lastName, accountType);
			}
		});

		// Assigned the action that is caused by the "Profile" icon being clicked.
		button_profile.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewProfile.fxml", "My Profile", userID, firstName, lastName, accountType);
			}
		}));


		// Assigned the action that is caused by the "Home" button being clicked an admin.
		button_home.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "UserMainPage.fxml", "Home", userID, firstName, lastName, accountType);
			}
		}));


		// Assigned the action that is caused by the "Create Event" button being clicked.
		button_edit_director.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "EditDirectorPage.fxml", "Create an Event", userID, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "View Events" button being clicked by an admin.
		button_edit_movie.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "EditMoviePage.fxml", "View Available Events", userID, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Donate" button being clicked by an admin.
		button_edit_rating.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "EditRatingPage.fxml", "Donate", userID, firstName, lastName, accountType);
			}
		}));

		// Update the user information
		button_save.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.updateUser(event, userID, tf_edit_first_name.getText(), tf_edit_last_name.getText(), accountType);
			}
		}));

	}
	
	public void setUserInformation(String firstName, String lastName, int userID, String accountType) {

		//get username
		Connection connection = null;
		PreparedStatement psQuery = null;
		ResultSet resultSet = null;

		String username = "";

		try {
			// Connect to the database and run the query to gather all current users.
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
			psQuery = connection.prepareStatement("SELECT username FROM user WHERE user_id = ?");
			psQuery.setInt(1, userID);
			resultSet = psQuery.executeQuery();

			if(resultSet.next() ) { // is cursor not at default position?
				username = resultSet.getString(1);
			}



		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close all statements that have been used to query and connect to the database.
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psQuery != null) {
				try {
					psQuery.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();

				}
			}
		}



		this.firstName = firstName;
		this.lastName = lastName;
		this.accountType = accountType;
		this.userID = userID;

		label_name.setText(firstName + " " + lastName);
		label_account_type.setText("Login");

		label_display_name.setText(firstName + " " + lastName);

		tf_edit_username.setText(username);
		tf_edit_first_name.setText(firstName);
		tf_edit_last_name.setText(lastName);

		// Configure the sidebar navigation
		// Configure the sidebar navigation
		if (accountType.equals("Admin")) {
			nav_admin.setVisible(true);
			nav_admin.setManaged(true);

			nav_guest.setVisible(true);
			nav_guest.setManaged(true);

			pane_edit_info.setVisible(true);
			pane_edit_info.setManaged(true);

			label_display_email_type.setText(username + " - " + accountType);
			label_account_type.setText("Logout");

		} else {
			nav_admin.setVisible(false);
			nav_admin.setManaged(true);

			nav_guest.setVisible(true);
			nav_guest.setManaged(true);

			pane_edit_info.setVisible(true);
			pane_edit_info.setManaged(true);

			label_display_email_type.setText(username + " - " + accountType);
			label_account_type.setText("Logout");
		}
	}
}
