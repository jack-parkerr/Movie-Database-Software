package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UserLoggedInController implements Initializable{

	//sidebar vars
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_edit_director;
	@FXML private Button button_edit_movie;
	@FXML private Button button_profile;
	@FXML private Button button_edit_rating;
	@FXML private Button button_search;
	@FXML private Button button_search2;
	@FXML private Button button_logout2;
	@FXML private Button button_rateMovie;
	@FXML private Label label_name;
	@FXML private Label label_account_type;
	@FXML public VBox nav_admin;
	@FXML public VBox nav_guest;

	//my reviews vars
	@FXML private ListView reviewsLV;

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

		//Configure the style of the reviews list
		reviewsLV.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 14px;");

		button_search.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "SearchPage.fxml", "Search Movies", userID, firstName, lastName, accountType);
			}
		});

		button_rateMovie.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "RateMoviePage.fxml", "Rate a Movie", userID, firstName, lastName, accountType);
			}
		});

		// Assigned the action that is caused by the "Logout" button being clicked.
		button_logout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "LogIn.fxml", "Log in!", -1, null, null,null);
			}
		});

		button_search2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "SearchPage.fxml", "Search Movies", userID, firstName, lastName, accountType);
			}
		});

		// Assigned the action that is caused by the "Logout" button being clicked.
		button_logout2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "LogIn.fxml", "Log in!", -1, null, null,null);
			}
		});

		// Assign the action to navigate the profile page once the "Profile" button is clicked.
		button_profile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewProfile.fxml", "My Profile", userID, firstName, lastName, accountType);
			}
		});

		// Assigned the action that is caused by the "Home" button being clicked.
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
				DBUtils.changeScene(event, "EditDirectorPage.fxml", "Edit a Director", userID, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "View Events" button being clicked.
		button_edit_movie.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "EditMoviePage.fxml", "Edit a Movie", userID, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Donate" button being clicked.
		button_edit_rating.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "EditRatingPage.fxml", "Edit a Rating", userID, firstName, lastName, accountType);
			}
		}));
	}
	
	public void setUserInformation(String firstName, String lastName, int userID, String accountType) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userID = userID;
		this.accountType = accountType;

		label_name.setText(firstName + " " + lastName);
		label_account_type.setText("Login");

		// Configure the ListView to display all the user's events
		Connection connection = null;
		PreparedStatement psGetReviews = null;
		ResultSet resultSet = null;

		reviewsLV.getItems().clear();

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
			psGetReviews = connection.prepareStatement("SELECT name, rate, review FROM rating JOIN movie ON rating.fk_movie_id = movie.movie_id WHERE fk_user_id = ? ORDER BY name ASC");
			psGetReviews.setInt(1,  userID);
			resultSet = psGetReviews.executeQuery();

			while(resultSet.next()) {
				String movieName = resultSet.getString("name");
				int rating = resultSet.getInt("rate");
				String movieReview = resultSet.getString("review");
				String  review =  movieName + ":  Rating: " + rating + "/5  |  " + movieReview;
				reviewsLV.getItems().add(review);
			}

			if(reviewsLV.getItems().isEmpty()){
				reviewsLV.getItems().add("No Reviews");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psGetReviews != null) {
				try {
					psGetReviews.close();
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



		// Configure the sidebar navigation
		if (accountType.equals("Admin")) {
			nav_admin.setVisible(true);
			nav_admin.setManaged(true);

			nav_guest.setVisible(true);
			nav_guest.setManaged(true);

			label_account_type.setText("Logout");

		} else {
			nav_guest.setVisible(true);
			nav_guest.setManaged(true);

			nav_admin.setVisible(false);
			nav_admin.setManaged(false);
		}

	}
}
