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

public class EditMovieController implements Initializable{

	//side menu
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_profile;
	@FXML private Button button_edit_movie;
	@FXML private Button button_search;
	@FXML private Button button_edit_director;
	@FXML private Button button_edit_rating;
	@FXML private VBox nav_admin;
	@FXML private VBox nav_guest;
	@FXML private Label label_name;
	@FXML private Label label_account_type;
	@FXML private Button button_rateMovie;

	//movie search vars
	@FXML private TextField searchMovieTF;
	@FXML private Button searchMovieButton;
	@FXML private ListView moviesLV;

	//add/edit movie data
	@FXML private TextField nameTF;
	@FXML private TextField releaseDateTF;
	@FXML private TextField runTimeTF;
	@FXML private TextField lifetimeGrossTF;
	@FXML private TextArea descriptionTF;

	//director search vars
	@FXML private TextField searchDirectorTF;
	@FXML private Button searchDirectorButton;
	@FXML private ListView directorsLV;

	//add/edit buttons
	@FXML private Button addMovieButton;
	@FXML private Button editMovieButton;

	private String firstName = "",lastName = "", accountType = "";
	int movieID, directorID, userID;

	/**
	 * Method that runs listening for Action Events.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Configure the ListViews
		moviesLV.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 12px;");
		directorsLV.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 12px;");

		// Assigned the action that is caused by the "Logout" button being clicked.
		button_logout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				DBUtils.changeScene(event, "LogIn.fxml", "Log in!", -1, null, null,null);
			}
		});

		/**
		 SIDEBAR NAVIGATION BUTTONS
		 */
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

		// Assigned the action that is caused by the "Home" sidebar button being clicked.
		button_home.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "UserMainPage.fxml", "Movies!", userID, firstName, lastName, accountType);
			}
		}));

		// Assign the action to navigate the profile page once the "Profile" button is clicked.
		button_profile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewProfile.fxml", "My Profile", userID, firstName, lastName, accountType);
			}
		});

		// Assigned the action that is caused by the "Edit Director" sidebar button being clicked.
		button_edit_director.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "EditDirectorPage.fxml", "Edit a Director", userID, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Edit Movie" sidebar button being clicked.
		button_edit_movie.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "EditMoviePage.fxml", "Edit a Movie", userID, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Edit Rating" sidebar button being clicked by an admin.
		button_edit_rating.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "EditRatingPage.fxml", "Edit a Rating", userID, firstName, lastName, accountType);
			}
		}));

		/**
		 * EDIT MOVIE MAIN PAGE BUTTONS
		 */
		//Assigned action that is caused by the "Add Movie" button being clicked
		addMovieButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				//movie data entered by user
				String movieName = nameTF.getText();
				String movieReleaseDate = releaseDateTF.getText();
				String movieDesc = descriptionTF.getText();
				int movieRunTime = 0;
					if(runTimeTF.getText().equals("")) {						//run time is empty
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("All information is required.");
						alert.show();
						return;
					} else if(!runTimeTF.getText().matches("[0-9]+")){	//run time contain something other than #'s
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Run time must be a number");
						alert.show();
						return;
					}
					else {
						movieRunTime = Integer.parseInt(runTimeTF.getText());
					}
				int movieLifetimeGross = 0;
					if(lifetimeGrossTF.getText().equals("")) {				//lifetime gross is empty
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("All information is required.");
						alert.show();
						return;
					} else if(!lifetimeGrossTF.getText().matches("[0-9]+")){	//lifetime gross contains something other than #'s
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Lifetime gross must be a number");
						alert.show();
						return;
					}
					else {
						movieLifetimeGross = Integer.parseInt(lifetimeGrossTF.getText());
					}


				if(movieName == null || movieName == "" || movieReleaseDate == null || movieReleaseDate == "" ){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("All information is required.");
					alert.show();
					return;
				}

				//no director was selected
				if (directorsLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please find a director already in the database and select them.\n\nIf the director is not already in the database please add them under the \"Edit Director\" sidebar tab.");
					alert.show();
					return;
				} else {
					//director was selected, get the directorID
					String selectedDirector = directorsLV.getSelectionModel().getSelectedItem().toString();
					directorID = Integer.parseInt(selectedDirector.substring(1, 5));
				}

				//database connection vars
				Connection connection = null;
				PreparedStatement psInsert = null;
				PreparedStatement getMovieID = null;
				PreparedStatement psCheckMovieExists = null;
				ResultSet resultSet = null;


				try {
					// Connect to the database and run the query to gather all current movies.
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
					psCheckMovieExists = connection.prepareStatement("SELECT * FROM movie WHERE name = ?");
					psCheckMovieExists.setString(1, movieName);
					resultSet = psCheckMovieExists.executeQuery();

					// If the movie name already exists, throw an error for the user.
					if (resultSet.isBeforeFirst()) {
						System.out.println("Movie already exists!");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("A movie with this name already exists! \n\nTry using the \"Edit Selected Movie\" buttton.");
						alert.show();
					} else {
						// If no error is found, insert the movie information into the database.
						psInsert = connection.prepareStatement("INSERT INTO movie (name, gross, release_date, run_time, description) VALUES (?, ?, ?, ?, ?)");
						psInsert.setString(1, movieName);
						psInsert.setInt(2, movieLifetimeGross);
						psInsert.setString(3, movieReleaseDate);
						psInsert.setInt(4, movieRunTime);
						psInsert.setString(5, movieDesc);
						psInsert.executeUpdate();

						// Get the movie ID that was generated
						getMovieID = connection.prepareStatement("SELECT LAST_INSERT_ID() AS last_id FROM movie");
						resultSet = getMovieID.executeQuery();
						resultSet.next();
						movieID = resultSet.getInt("last_id");

						// Insert the relationship between the movie and director to the relationship table.
						psInsert = connection.prepareStatement("INSERT INTO directed_by (movie_id_fk, director_id_fk) VALUES (?, ?)");
						psInsert.setInt(1, movieID);
						psInsert.setInt(2, directorID);
						psInsert.executeUpdate();

						// Confirm to the user that they have the movie to the database
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setContentText("Movie data added to the Movie Madness database.");
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
					if (psCheckMovieExists != null) {
						try {
							psCheckMovieExists.close();
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
		editMovieButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				//movie data entered by user
				String enteredMovieName = nameTF.getText();
				String enteredMovieReleaseDate = releaseDateTF.getText();
				String enteredMovieDesc = descriptionTF.getText();
				int enteredMovieRunTime;
					if(runTimeTF.getText().equals("")) { enteredMovieRunTime = -1;} //empty
					else if(!runTimeTF.getText().matches("[0-9]+")){			//contain something other than #'s
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Run time must be a number");
						alert.show();
						return;
					}
					else { enteredMovieRunTime = Integer.parseInt(runTimeTF.getText()); }
				int enteredMovieLifetimeGross;
					if(lifetimeGrossTF.getText().equals("")) { enteredMovieLifetimeGross = -1;} 	//empty
					else if(!lifetimeGrossTF.getText().matches("[0-9]+")){					//contains something other than #'s
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Lifetime gross must be a number");
						alert.show();
						return;
					}
					else {enteredMovieLifetimeGross = Integer.parseInt(lifetimeGrossTF.getText()); }


				//current movie data vars
				String selectedMovieName = null;
				int selectedMovieGross = 0;
				String selectedMovieReleaseDate = null;
				int selectedMovieRunTime = 0;
				String selectedMovieDesc = null;

				//no movie was selected
				if (moviesLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please find a movie already in the database and select it.\n\nIf the movie is not already in the database add it using the \"Add New Movie\" button.");
					alert.show();
					return;
				} else {
					//movie was selected, get the movie information
					String selectedMovie = moviesLV.getSelectionModel().getSelectedItem().toString();
					String[] movieAttribs = selectedMovie.split("  |  ");
					movieID = Integer.parseInt(movieAttribs[0].substring(1, 5));
					selectedMovieName = movieAttribs[2].substring(6);
					selectedMovieGross = Integer.parseInt(movieAttribs[4].substring(7));
					selectedMovieReleaseDate = movieAttribs[6].substring(14);
					selectedMovieRunTime = Integer.parseInt(movieAttribs[8].substring(10));
					selectedMovieDesc = movieAttribs[10].substring(6);
				}

				//(new) movie vars to be inserted into database
				String finalMovieName = null;
				String finalMovieReleaseDate = null;
				int finalMovieRunTime = 0;
				int finalMovieLifetimeGross = 0;
				String finalMovieDesc = null;

				//new or old movie name?
				if(enteredMovieName.equals(null) || enteredMovieName.equals("")){finalMovieName = selectedMovieName;}
				else {finalMovieName = enteredMovieName;}

				//new or old movie release date?
				if(enteredMovieReleaseDate.equals(null) || enteredMovieReleaseDate.equals("")){finalMovieReleaseDate = selectedMovieReleaseDate;}
				else {finalMovieReleaseDate = enteredMovieReleaseDate;}

				//new or old run time?
				if(enteredMovieRunTime == -1){finalMovieRunTime = selectedMovieRunTime;}
				else {finalMovieRunTime = enteredMovieRunTime;}

				//new or old gross
				if(enteredMovieLifetimeGross == -1){finalMovieLifetimeGross = selectedMovieGross;}
				else {finalMovieLifetimeGross = enteredMovieLifetimeGross;}

				//new or old description
				if(enteredMovieDesc.equals(null) || enteredMovieDesc.equals("")){finalMovieDesc = selectedMovieDesc;}
				else {finalMovieDesc = enteredMovieDesc;}


				//director selected or no?
				boolean updateDirector = false;
				if (directorsLV.getSelectionModel().getSelectedItem() != null) { //if a director is selected
					//director was selected, get the directorID, set updateDirector = true
					String selectedDirector = directorsLV.getSelectionModel().getSelectedItem().toString();
					directorID = Integer.parseInt(selectedDirector.substring(1, 5));
					updateDirector = true;
				}

				Connection connection = null;
				PreparedStatement psUpdate = null;

				try {
					// Update the user information in the database
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
					psUpdate = connection.prepareStatement("UPDATE movie SET name = ?, gross = ?, release_date = ?, run_time = ?, description = ? WHERE movie_id = ?");
					psUpdate.setString(1, finalMovieName);
					psUpdate.setInt(2, finalMovieLifetimeGross);
					psUpdate.setString(3, finalMovieReleaseDate);
					psUpdate.setInt(4, finalMovieRunTime);
					psUpdate.setString(5, finalMovieDesc);
					psUpdate.setInt(6, movieID);
					psUpdate.executeUpdate();

					//Do we need to update the director?
					if(updateDirector) {
						//update the movie - director relationship
						psUpdate = connection.prepareStatement("UPDATE directed_by Set director_id_fk = ? WHERE movie_id_fk = ?");
						psUpdate.setInt(1, directorID);
						psUpdate.setInt(2, movieID);
						psUpdate.executeUpdate();
					}

					// Refresh the search movies list view
					moviesLV.getItems().clear();
					String movie =  "[" + movieID + "]  |  Name: " + finalMovieName + "  |  Gross: " + finalMovieLifetimeGross  + "  |  Release Date: " + finalMovieReleaseDate  + "  |  Run Time: " + finalMovieRunTime + "  |  Desc: " + finalMovieDesc;
					moviesLV.getItems().add(movie);

					// Confirm to the user that they have updated the movie information
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setContentText("Movie data updated.");
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

		//Assigned action that is caused by the "Search Movie" button being clicked
		searchMovieButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;

				String searchedMovie = searchMovieTF.getText();

				moviesLV.getItems().clear();

				if(searchedMovie.equals(null)){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please enter a movie to search.");
					alert.show();
				} else {

					try {
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
						psQuery = connection.prepareStatement("SELECT movie_id, name, gross, release_date, run_time, description FROM movie WHERE name LIKE ? ORDER BY name ASC");
						psQuery.setString(1, "%" + searchedMovie + "%");
						resultSet = psQuery.executeQuery();

						while(resultSet.next()) {
							int movieID = resultSet.getInt("movie_id");
							String movieName = resultSet.getString("name");
							int movieGross = resultSet.getInt("gross");
							String movieReleaseDate = resultSet.getString("release_date");
							String movieRunTime = resultSet.getString("run_time");
							String movieDescription = resultSet.getString("description");
							String movie =  "[" + movieID + "]  |  Name: " + movieName + "  |  Gross: " + movieGross  + "  |  Release Date: " + movieReleaseDate  + "  |  Run Time: " + movieRunTime + "  |  Desc: " + movieDescription;
							moviesLV.getItems().add(movie);
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
		this.accountType = accountType;
		this.userID = userID;

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
