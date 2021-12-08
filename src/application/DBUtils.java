/* 
 * DBUtils.java
 * 
 * JavaFX Bookkeeping Software
 * 
 * This class handles the interaction with the database. It also process changes
 * in scenes between pages of the software. This information is often specific to
 * the type of user that is logged in, and this class handles that information.
 * 
 */

package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DBUtils {
	
	/**
	 * Method used to change between scenes on the application
	 * @param event :       The event that causes the scene change
	 * @param fxmlFile :    The FXML file that we want to load
	 * @param title :       Title of the scene we are switching to
	 * @param userID
	 * @param accountType : a string version of the account type (Admin, Donor, Volunteer)
	 */
	public static void changeScene(ActionEvent event, String fxmlFile, String title, int userID, String firstName, String lastName, String accountType) {
		Parent root = null;

		// Check to see if the user information was passed
		if (userID != -1 && accountType != null) {
			try {
				FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
				root = loader.load();
				// Display the scene that is trying to be displayed
				if (fxmlFile.equals("EditDirectorPage.fxml")) {
					EditDirectorController createEventController = loader.getController();
					createEventController.setUserInformation(firstName, lastName, userID, accountType);
				} else if (fxmlFile.equals("UserMainPage.fxml")) {
					UserLoggedInController loggedInController = loader.getController();
					loggedInController.setUserInformation(firstName, lastName, userID, accountType);
				} else if (fxmlFile.equals("EditMoviePage.fxml")) {
					EditMovieController viewEventsController = loader.getController();
					viewEventsController.setUserInformation(firstName, lastName, userID, accountType);
				} else if (fxmlFile.equals("SearchPage.fxml")) {
					SearchPageController searchPageController = loader.getController();
					searchPageController.setUserInformation(firstName, lastName, userID, accountType);
				} else if (fxmlFile.equals("ViewProfile.fxml")) {
					ViewProfileController viewProfileController = loader.getController();
					viewProfileController.setUserInformation(firstName, lastName, userID, accountType);
				} else if (fxmlFile.equals("EditRatingPage.fxml")){
					EditRatingController editRatingController = loader.getController();
					editRatingController.setUserInformation(firstName, lastName, userID, accountType);
				} else if (fxmlFile.equals("RateMoviePage.fxml")){
					RateMovieController rateMovieController = loader.getController();
					rateMovieController.setUserInformation(firstName, lastName, userID, accountType);
				} else if (fxmlFile.equals("AdvancedSearchPage.fxml")){
					AdvancedSearchPageController advSearchController = loader.getController();
					advSearchController.setUserInformation(firstName, lastName, userID, accountType);
				} else {
					System.out.println("[ERROR] Page not loaded.");
				}
			// Catch any exception that is thrown and print it's stack trace
			} catch (IOException e) {
				e.printStackTrace();
			}
		// Navigate to the log in page. We do not need to process user information here.
		} else {
			try {
				root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setTitle(title);
		Scene scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.show();
	}

	public static void signUpUser(ActionEvent event, String username, String password, String firstName, String lastName, String accountType) {
		// Set up variables that will be used to query the database.
		Connection connection = null;
		PreparedStatement psInsert = null;
		PreparedStatement psCheckUserExists = null;
		ResultSet resultSet = null;


		try {
			// Connect to the database and run the query to gather all current users.
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
			psCheckUserExists = connection.prepareStatement("SELECT * FROM user WHERE username = ?");
			psCheckUserExists.setString(1, username);
			resultSet = psCheckUserExists.executeQuery();

			// If the email is already in use, throw an error for the user.
			if (resultSet.isBeforeFirst()) {
				System.out.println("User already exists!");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("This username is already in use.");
				alert.show();
				// If no error is found, insert the user information into the database.
			} else {
				psInsert = connection.prepareStatement("INSERT INTO user (username, password, type, firstName, lastName) VALUES (?, ?, ?, ?, ?)");
				psInsert.setString(1, username);
				psInsert.setString(2, password); //convert byte array password to string for storage
				psInsert.setString(3, accountType);
				psInsert.setString(4, firstName);
				psInsert.setString(5, lastName);
				psInsert.executeUpdate();

				// Confirm with the user that their account has been created.
				changeScene(event, "LogIn.fxml", "Log in!", -1, null, null, null);
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setContentText("Registration complete.");
				alert.show();

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
			if (psCheckUserExists != null) {
				try {
					psCheckUserExists.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psInsert != null) {
				try {
					psInsert.close();
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
	}

	/**
	 * Method used to log in a user.
	 *
	 * @param event: the event that caused the method to run.
	 * @param username: the username entered by the user.
	 * @param password: the password entered by the user.
	 */
	public static void logInUser(ActionEvent event, String username, String password) {
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		//convert string password to char array to prepare for encrypting
		char[] charArrInputPass = password.toCharArray();

		try {
			// Connect to the database and run the query to gather the user information from the database
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
			preparedStatement = connection.prepareStatement("SELECT user_id, username, password, type, firstName, lastName FROM user WHERE username = ?");
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();

			// If the user is not found, display an error for the user.
			if (!resultSet.isBeforeFirst()) {
				System.out.println("User not found in the database!");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Provided credentials are incorrect!");
				alert.show();
			} else {
				// If the user is found, load their information.
				while (resultSet.next()) {
					int retrievedUserID = resultSet.getInt("user_id");
					String retrievedPassword = resultSet.getString("password");
					String retrievedAccountType = resultSet.getString("type");
					String retrievedFirstName = resultSet.getString("firstName");
					String retrievedLastName = resultSet.getString("lastName");

					//compare the string converted hashed password from the database to the newly string converted hashed password
					if (retrievedPassword.equals(password)) {
						changeScene(event, "UserMainPage.fxml", "Home", retrievedUserID, retrievedFirstName, retrievedLastName, retrievedAccountType);
					} else {
						// If the password was incorrect, display an error to the user.
						System.out.println("Passwords did not match!");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("The provided credentials are incorrect!");
						alert.show();
					}
				}
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
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
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
	}

	/**
	 * Method used to update user information.
	 *  @param event : the event that caused the method to run.
	 * @param userID : the email entered by the user.
	 * @param firstName : the user's updated first name
	 * @param lastName : the user's updated last name
	 * @param accountType : the user's account type.
	 * @param userID
	 */
	public static void updateUser(ActionEvent event, int userID, String firstName, String lastName, String accountType) {
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement psUpdate = null;

		try {
			// Update the user information in the database
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
			psUpdate = connection.prepareStatement("UPDATE user SET FirstName = ?, LastName = ? WHERE userID = ?");
			psUpdate.setString(1, firstName);
			psUpdate.setString(2, lastName);
			psUpdate.setInt(3, userID);
			psUpdate.executeUpdate();

			// Refresh the profile page
			changeScene(event, "ViewProfile.fxml", "My Profile", userID, firstName, lastName, accountType);

			// Confirm to the user that they have cancelled their registration
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("User information updated.");
			alert.show();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close all statements that have been used to query and connect to the database.
			if (psUpdate != null) {
				try {
					psUpdate.close();
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
	}
}