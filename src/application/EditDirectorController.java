package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EditDirectorController implements Initializable{

	//sidebar vars
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_edit_director;
	@FXML private Button button_edit_movie;
	@FXML private Button button_edit_rating;
	@FXML private Button button_profile;
	@FXML private Button button_search;
	@FXML public VBox nav_admin;
	@FXML public VBox nav_guest;
	@FXML private Label label_name;
	@FXML private Label label_account_type;
	@FXML private Button button_rateMovie;

	//search director vars
	@FXML private TextField searchDirectorTF;
	@FXML private ListView directorsLV;
	@FXML private Button searchDirectorButton;

	//add/edit movie data
	@FXML private TextField nameTF;
	@FXML private TextField birthdateTF;

	//add/edit buttons
	@FXML private Button addDirectorButton;
	@FXML private Button editDirectorButton;

	private String firstName = "",lastName = "", accountType = "";
	int directorID, userID;

	/**
	 * Method that runs listening for Action Events.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Configure the ListView
		directorsLV.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 12px;");

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

		// Assigned the action that is caused by the "Home" button being clicked.
		button_home.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "UserMainPage.fxml", "Home", userID, firstName, lastName, accountType);
			}
		}));

		// Assign the action to navigate the profile page once the "Profile" button is clicked.
		button_profile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewProfile.fxml", "My Profile", userID, firstName, lastName, accountType);
			}
		});

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

		addDirectorButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				//movie data entered by user
				String directorName = nameTF.getText();
				String directorBD = birthdateTF.getText();

				//verify all data was entered
				if(directorName == null || directorName == "" || directorBD == null || directorBD == "" ){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("All information is required.");
					alert.show();
					return;
				}

				//database connection vars
				Connection connection = null;
				PreparedStatement psInsert = null;
				PreparedStatement psCheckDirExists = null;
				ResultSet resultSet = null;


				try {
					// Connect to the database and run the query to gather all current movies.
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
					psCheckDirExists = connection.prepareStatement("SELECT * FROM director WHERE name = ?");
					psCheckDirExists.setString(1, directorName);
					resultSet = psCheckDirExists.executeQuery();

					// If the movie name already exists, throw an error for the user.
					if (resultSet.isBeforeFirst()) {
						System.out.println("Director already exists!");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("A director with this name already exists! \n\nTry using the \"Edit Selected Director\" buttton.");
						alert.show();
					} else {
						// If no error is found, insert the movie information into the database.
						psInsert = connection.prepareStatement("INSERT INTO director (name, birthdate) VALUES (?, ?)");
						psInsert.setString(1, directorName);
						psInsert.setString(2, directorBD);
						psInsert.executeUpdate();

						// Confirm to the user that they have the movie to the database
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setContentText("Director data added to the Movie Madness database.");
						alert.show();

					}
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				} finally {
					if (resultSet != null) {
						try {
							resultSet.close();
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
					if (psCheckDirExists != null) {
						try {
							psCheckDirExists.close();
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
		}));

		//Assigned action that is caused by the "Edit Movie" button being clicked
		editDirectorButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				//movie data entered by user
				String enteredDirName = nameTF.getText();
				String enteredDirBirthdate = birthdateTF.getText();

				//current director data vars
				String selectedDirName = null;
				String selectedDirBirthdate = null;


				//no director was selected
				if (directorsLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please find a director already in the database and select it.\n\nIf the director is not already in the database add it using the \"Add New Director\" button.");
					alert.show();
					return;
				} else {
					//director was selected, get the director information
					String selectedDir = directorsLV.getSelectionModel().getSelectedItem().toString();
					String[] dirAttribs = selectedDir.split("  |  ");
					directorID = Integer.parseInt(dirAttribs[0].substring(1, 5));
					selectedDirName = dirAttribs[2].substring(6);
					selectedDirBirthdate = dirAttribs[4].substring(10);
				}

				//(new) movie vars to be inserted into database
				String finalDirName = null;
				String finalDirBirthdate = null;

				//new or old movie name?
				if(enteredDirName.equals(null) || enteredDirName.equals("")){finalDirName = selectedDirName;}
				else {finalDirName = enteredDirName;}

				//new or old movie release date?
				if(enteredDirBirthdate.equals(null) || enteredDirBirthdate.equals("")){finalDirBirthdate = selectedDirBirthdate;}
				else {finalDirBirthdate = enteredDirBirthdate;}

				Connection connection = null;
				PreparedStatement psUpdate = null;

				try {
					// Update the user information in the database
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
					psUpdate = connection.prepareStatement("UPDATE director SET name = ?, birthdate = ? WHERE director_id = ?");
					psUpdate.setString(1, finalDirName);
					psUpdate.setString(2, finalDirBirthdate);
					psUpdate.setInt(3, directorID);
					psUpdate.executeUpdate();

					// Refresh the search movies list view
					directorsLV.getItems().clear();
					String director =  "[" + directorID + "]  |  Name: " + finalDirName + "  |  Birthday: " + finalDirBirthdate;
					directorsLV.getItems().add(director);

					// Confirm to the user that they have updated the movie information
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setContentText("Director data updated.");
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
		}));

		//Assigned action that is caused by the "Search Director" button being clicked
		searchDirectorButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;

				String searchedDirector = searchDirectorTF.getText();

				directorsLV.getItems().clear();

				if(searchedDirector.equals(null)){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please enter a director to search.");
					alert.show();
				} else {

					try {
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
						psQuery = connection.prepareStatement("SELECT director_id, name, birthdate FROM director WHERE name LIKE ? ORDER BY name ASC");
						psQuery.setString(1, "%" + searchedDirector + "%");
						resultSet = psQuery.executeQuery();

						while(resultSet.next()) {
							int directorID = resultSet.getInt("director_id");
							String directorName = resultSet.getString("name");
							String directorBirthdate = resultSet.getString("birthdate");
							String director =  "[" + directorID + "]  |  Name: " + directorName + "  |  Birthday: " + directorBirthdate;
							directorsLV.getItems().add(director);
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
				}
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
