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

public class EditRatingController implements Initializable{

	//sidebar vars
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_edit_director;
	@FXML private Button button_edit_movie;
	@FXML private Button button_edit_rating;
	@FXML private Button button_profile;
	@FXML private Button button_search;
	@FXML private Button button_rateMovie;
	@FXML private Label label_name;
	@FXML private Label label_account_type;
	@FXML public VBox nav_admin;
	@FXML public VBox nav_guest;

	//search movie vars
	@FXML private TextField searchMovieTF;
	@FXML private Button searchMovieButton;
	@FXML private ListView moviesLV;

	//search user vars
	@FXML private TextField searchUserTF;
	@FXML private Button searchUserButton;
	@FXML private ListView usersLV;

	//show result vars
	@FXML private Button showReviewsMovieButton;
	@FXML private Button showReviewsUserButton;
	@FXML private Button showComboButton;
	@FXML private ListView reviewsLV;

	@FXML private Button deleteReviewButton;

	private String firstName = "";
	private String lastName = "";
	private String accountType = "";
	int movieID, userID;

	/**
	 * Method that runs listening for Action Events.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {


		//Configure the ListViews
		moviesLV.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 11px;");
		usersLV.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 11px;");
		reviewsLV.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 11px;");

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


		/**
		 * EDIT RATING MAIN PAGE BUTTONS
		 */
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
							movieID = resultSet.getInt("movie_id");
							String movieName = resultSet.getString("name");
							int movieGross = resultSet.getInt("gross");
							String movieReleaseDate = resultSet.getString("release_date");
							String movieRunTime = resultSet.getString("run_time");
							String movieDescription = resultSet.getString("description");
							String movie =  "[" + movieID + "]  |  Name: " + movieName + "  |  Release Date: " + movieReleaseDate  + "  |  Desc: " + movieDescription;
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

		searchUserButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;

				String searchedUser = searchUserTF.getText();

				usersLV.getItems().clear();

				if(searchedUser.equals(null)){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please enter a username to search.");
					alert.show();
				} else {


					try {
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
						psQuery = connection.prepareStatement("SELECT user_id, username, firstName, lastName FROM user WHERE username LIKE ? ORDER BY lastName ASC");
						psQuery.setString(1, "%" + searchedUser + "%");
						resultSet = psQuery.executeQuery();

						while(resultSet.next()) {
							int curUserID = resultSet.getInt("user_id");
							String username = resultSet.getString("username");
							String firstName = resultSet.getString("firstName");
							String lastName = resultSet.getString("lastName");
							String user =  "[" + curUserID + "]  |  Userame: " + username + "  |  Name: " + firstName + " " + lastName;
							usersLV.getItems().add(user);
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

		showReviewsMovieButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;


				if (moviesLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select a movie that you would like to see the reviews of.");
					alert.show();
					return;
				} else {
					String movie = moviesLV.getSelectionModel().getSelectedItem().toString();
					movieID = Integer.parseInt(movie.substring(1, 5));

					reviewsLV.getItems().clear();

					try {
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
						psQuery = connection.prepareStatement("SELECT rating_id, review, rate, fk_user_id, username FROM rating JOIN user ON rating.fk_user_id = user.user_id  WHERE fk_movie_id = ?");
						psQuery.setInt(1, movieID );
						resultSet = psQuery.executeQuery();

						while (resultSet.next()) {
							int ratingID = resultSet.getInt("rating_id");
							String review = resultSet.getString("review");
							int rating = resultSet.getInt("rate");
							int ratedByUserID = resultSet.getInt("fk_user_id");
							String username = resultSet.getString("username");
							String reviewUser = "[" + ratingID + "]  |  Username: " + username + "  |  Rating: " + rating + "/5  |  Review: " + review;
							reviewsLV.getItems().add(reviewUser);
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

		showReviewsUserButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;


				if (usersLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select a user who's reviews you'd like to see");
					alert.show();
					return;
				} else {
					String user = usersLV.getSelectionModel().getSelectedItem().toString();
					int selectedUserID = Integer.parseInt(user.substring(1, 5));

					reviewsLV.getItems().clear();

					try {
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
						psQuery = connection.prepareStatement("SELECT rating_id, review, rate, fk_movie_id, name FROM rating JOIN movie ON rating.fk_movie_id = movie.movie_id  WHERE fk_user_id = ?");
						psQuery.setInt(1, selectedUserID );
						resultSet = psQuery.executeQuery();


						while (resultSet.next()) {
							int ratingID = resultSet.getInt("rating_id");
							String review = resultSet.getString("review");
							int rating = resultSet.getInt("rate");
							int movieIDRatedByUser = resultSet.getInt("fk_movie_id");
							String movieName = resultSet.getString("name");
							String reviewUser = "[" + ratingID + "]  |  Movie Name: " + movieName + "  |  Rating: " + rating + "/5  |  Review: " + review;
							reviewsLV.getItems().add(reviewUser);
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

		showComboButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;


				if (usersLV.getSelectionModel().getSelectedItem() == null || moviesLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select a user and movie you'd like to see");
					alert.show();
					return;
				} else {
					String user = usersLV.getSelectionModel().getSelectedItem().toString();
					int selectedUserID = Integer.parseInt(user.substring(1, 5));

					String movie = moviesLV.getSelectionModel().getSelectedItem().toString();
					movieID = Integer.parseInt(movie.substring(1, 5));

					reviewsLV.getItems().clear();

					try {
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
						psQuery = connection.prepareStatement("SELECT rating_id, review, rate FROM rating  WHERE fk_user_id = ? AND fk_movie_id = ?" );
						psQuery.setInt(1, selectedUserID );
						psQuery.setInt(2, movieID);
						resultSet = psQuery.executeQuery();


						while (resultSet.next()) {
							int ratingID = resultSet.getInt("rating_id");
							String review = resultSet.getString("review");
							int rating = resultSet.getInt("rate");

							String reviewUser = "[" + ratingID + "]  |  Rating: " + rating + "/5  |  Review: " + review;
							reviewsLV.getItems().add(reviewUser);
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

		deleteReviewButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				Connection connection = null;
				PreparedStatement psDelete = null;
				ResultSet resultSet = null;


				if (reviewsLV.getSelectionModel().getSelectedItem() == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select a review you'd like to delete.");
					alert.show();
					return;
				} else {
					String review = reviewsLV.getSelectionModel().getSelectedItem().toString();
					int reviewID = Integer.parseInt(review.substring(1, 5));

					reviewsLV.getItems().clear();

					try {
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
						psDelete = connection.prepareStatement("DELETE FROM rating WHERE rating_id = ?" );
						psDelete.setInt(1, reviewID );
						psDelete.executeUpdate();

						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setContentText("Review was deleted.");
						alert.show();

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
						if (psDelete != null) {
							try {
								psDelete.close();
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
