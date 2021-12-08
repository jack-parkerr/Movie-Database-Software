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

public class RateMovieController implements Initializable{

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

	//search movie vars
	@FXML private TextField searchMovieTF;
	@FXML private ListView moviesLV;
	@FXML private Button searchMovieButton;

	@FXML private Button showPreviousReviewButton;

	//Rating Slider
	@FXML private Slider ratingSlider;

	//review and submit
	@FXML private TextArea movieReview;
	@FXML private Button addReviewButton;
	@FXML private Button editReviewButton;

	int movieID;


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

		//Configure the ListView
		moviesLV.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 12px;");

		//configure rating slider
		ratingSlider.setMin(0);
		ratingSlider.setMax(5);
		ratingSlider.setShowTickLabels(true);
		ratingSlider.setShowTickMarks(true);
		ratingSlider.setSnapToTicks(true);
		ratingSlider.setValue(0);
		ratingSlider.setMinorTickCount(0);
		ratingSlider.setMajorTickUnit(1);

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

		/**
		 *  RATE MOVIE MAIN PAGE BUTTONS
		 */
		addReviewButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				//get rating and review
				int rating = (int) ratingSlider.getValue();
				String review = movieReview.getText();

				//no movie was selected
				if (moviesLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please find a movie already in the database and select it.\n\nIf the movie is not already in the database ask an admin to add it. ");
					alert.show();
					return;
				} else {
					//movie was selected, get the movie information
					String selectedMovie = moviesLV.getSelectionModel().getSelectedItem().toString();
					String[] movieAttribs = selectedMovie.split("  |  ");
					movieID = Integer.parseInt(movieAttribs[0].substring(1, 5));
				}

				//database connection vars
				Connection connection = null;
				PreparedStatement psInsert = null;
				PreparedStatement psCheckUserRatingExists = null;
				ResultSet resultSet = null;

				try {
					// Connect to the database and run the query to gather all current movies.
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
					psCheckUserRatingExists = connection.prepareStatement("SELECT * FROM rating WHERE fk_movie_id = ? AND fk_user_id = ?");
					psCheckUserRatingExists.setInt(1, movieID);
					psCheckUserRatingExists.setInt(2, userID);
					resultSet = psCheckUserRatingExists.executeQuery();

					// If the movie name already exists, throw an error for the user.
					if (resultSet.isBeforeFirst()) {
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("You have already reviewed this movie, please chose another movie to review or: \n\nTry using the \"Edit My Review\" button.");
						alert.show();
					} else {
						// If no error is found, insert the review information into the database.
						psInsert = connection.prepareStatement("INSERT INTO rating (review, rate, fk_movie_id, fk_user_id) VALUES (?, ?, ?, ?)");
						psInsert.setString(1, review);
						psInsert.setInt(2, rating);
						psInsert.setInt(3, movieID);
						psInsert.setInt(4, userID);
						psInsert.executeUpdate();

						// Confirm to the user that they have the movie to the database
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setContentText("Review data added to the Movie Madness database.");
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
					if (psCheckUserRatingExists != null) {
						try {
							psCheckUserRatingExists.close();
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

		// Assigned the action that is caused by the "Donate" button being clicked.
		editReviewButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Connection connection = null;
				PreparedStatement psUpdate = null;
				PreparedStatement psCheckUserRatingExists = null;
				ResultSet resultSet = null;

				//get rating and review
				int rating = (int) ratingSlider.getValue();
				String review = movieReview.getText();

				//no movie was selected
				if (moviesLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please find a movie already in the database and select it.\n\nIf the movie is not already in the database ask an admin to add it.");
					alert.show();
					return;
				} else {
					//movie was selected, get movieID
					String selectedMovie = moviesLV.getSelectionModel().getSelectedItem().toString();
					movieID = Integer.parseInt(selectedMovie.substring(1, 5));

					try {
						// Connect to the database and run the query to gather all current movies.
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
						psCheckUserRatingExists = connection.prepareStatement("SELECT * FROM rating WHERE fk_movie_id = ? AND fk_user_id = ?");
						psCheckUserRatingExists.setInt(1, movieID);
						psCheckUserRatingExists.setInt(2, userID);
						resultSet = psCheckUserRatingExists.executeQuery();

						// If the movie review dosen't already exist, throw error.
						if (!resultSet.isBeforeFirst()) {
							Alert alert = new Alert(Alert.AlertType.ERROR);
							alert.setContentText("You have Not already reviewed this movie, please make an initial review using the \"Submit Review\" button.");
							alert.show();
						} else {
							// If no error is found, update the review information into the database.
							psUpdate = connection.prepareStatement("UPDATE rating SET review = ?, rate = ? WHERE fk_movie_id = ? AND fk_user_id = ?");
							psUpdate.setString(1, review);
							psUpdate.setInt(2, rating);
							psUpdate.setInt(3, movieID);
							psUpdate.setInt(4, userID);
							psUpdate.executeUpdate();

							//tell user movie review was updated
							Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
							alert.setContentText("Movie review was updated.");
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
		}));

		// Assigned the action that is caused by the "Donate" button being clicked.
		showPreviousReviewButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;

				//no movie was selected
				if (moviesLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please find a movie already in the database and select it.\n\nIf the movie is not already in the database ask an admin to add it.");
					alert.show();
					return;
				} else {
					//movie was selected, get the movieID
					String selectedMovie = moviesLV.getSelectionModel().getSelectedItem().toString();
					movieID = Integer.parseInt(selectedMovie.substring(1, 5));

					try {
						// Connect to the database and run the query to gather all current movies.
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
						psQuery = connection.prepareStatement("SELECT * FROM rating WHERE fk_movie_id = ? AND fk_user_id = ?");
						System.out.println("movie ID: " + movieID + " username: " + userID);
						psQuery.setInt(1, movieID);
						psQuery.setInt(2, userID);
						resultSet = psQuery.executeQuery();

						if(resultSet.next() ) { // is cursor not at default position?
							int ratingID = resultSet.getInt(1);
							String review = resultSet.getString(2);
							int rating = resultSet.getInt(3);

							movieReview.setText(review);
							ratingSlider.setValue(rating);
						} else {
							Alert alert = new Alert(Alert.AlertType.ERROR);
							alert.setContentText("You have not yet reviewed this movie. Try adding a review using the \"Submit Review\" Button.");
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
	}
	
	public void setUserInformation(String firstName, String lastName, int userID, String accountType) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userID = userID;
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
