package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SearchPageController implements Initializable{

	//sidebar vars
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_edit_director;
	@FXML private Button button_edit_movie;
	@FXML private Button button_edit_rating;
	@FXML private Button button_profile;
	@FXML private Button button_search;
	@FXML private Button button_rateMovie;
	@FXML public VBox nav_admin;
	@FXML public VBox nav_guest;
	@FXML private Label label_name;
	@FXML private Label label_account_type;

	//search vars
	@FXML private Button submitSearch;
	@FXML private TableView tableview_results;
	@FXML private ChoiceBox searchTypeCB;
//	@FXML private ChoiceBox sortByCB;
//	@FXML private ChoiceBox sortTypeCB;
	@FXML private TextField searchTF;
	@FXML private Button advancedSearch;


	private String firstName = "",lastName = "",  accountType = "";
	int userID;

	/**
	 * Method that runs listening for Action Events.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Configure the style of the events list
		tableview_results.setEditable(true);
		tableview_results.getColumns().clear();
		// Configure the choice boxes
		String[] searchTypes = new String[]{"Movie", "Review", "Director"};

		searchTypeCB.setItems(FXCollections.observableArrayList("Movie", "Review", "Director"));
		searchTypeCB.setValue("Movie"); // set the default value
		searchTypeCB.setTooltip(new Tooltip("Select Search type"));

//		sortByCB.setItems(FXCollections.observableArrayList("Movie ID", "Title", "Earnings", "Release Date", "Run Time", "Director", "Average Rating", "Description"));
//		searchTypeCB.setValue("Title"); // set the default value
//		sortByCB.setTooltip(new Tooltip("Sort by Attribute"));

		TableColumn<Movie, String> movieName = new TableColumn<>("Title");
		movieName.setCellValueFactory(new PropertyValueFactory<>("movieName"));

		TableColumn<Movie, String> earnings = new TableColumn<>("Earnings");
		earnings.setCellValueFactory(new PropertyValueFactory<>("earnings"));

		TableColumn<Movie, String> releaseDate = new TableColumn<>("Release Date");
		releaseDate.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));

		TableColumn<Movie, String> runTime = new TableColumn<>("Run Time");
		runTime.setCellValueFactory(new PropertyValueFactory<>("runTime"));

		TableColumn<Movie, String> director = new TableColumn<>("Director");
		director.setCellValueFactory(new PropertyValueFactory<>("director"));

		TableColumn<Movie, String> aveRating = new TableColumn<>("Average Rating");
		aveRating.setCellValueFactory(new PropertyValueFactory<>("aveRating"));

		TableColumn<Movie, String> description = new TableColumn<>("Description");
		description.setCellValueFactory(new PropertyValueFactory<>("description"));

		tableview_results.getColumns().addAll(movieName, earnings, releaseDate, runTime, director, aveRating, description);

		// Assigned the action that is caused by the "Logout" button being clicked.
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

		// Assigned the action that is caused by the "Donate" button being clicked by an admin.
		button_edit_rating.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "EditRatingPage.fxml", "Edit a Rating", userID, firstName, lastName, accountType);
			}
		}));

		advancedSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "AdvancedSearchPage.fxml", "Search Movies - Advanced", userID, firstName, lastName, accountType);
			}
		});


		searchTypeCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number value, Number new_value) {
				String table = searchTypes[new_value.intValue()].toLowerCase();
				tableview_results.getItems().clear();
				tableview_results.getColumns().clear();

				if(table.equals("movie")) {
					TableColumn<Movie, String> movieName = new TableColumn<>("Title");
					movieName.setCellValueFactory(new PropertyValueFactory<>("movieName"));

					TableColumn<Movie, String> earnings = new TableColumn<>("Earnings");
					earnings.setCellValueFactory(new PropertyValueFactory<>("earnings"));

					TableColumn<Movie, String> releaseDate = new TableColumn<>("Release Date");
					releaseDate.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));

					TableColumn<Movie, String> runTime = new TableColumn<>("Run Time");
					runTime.setCellValueFactory(new PropertyValueFactory<>("runTime"));

					TableColumn<Movie, String> director = new TableColumn<>("Director");
					director.setCellValueFactory(new PropertyValueFactory<>("director"));

					TableColumn<Movie, String> aveRating = new TableColumn<>("Average Rating");
					aveRating.setCellValueFactory(new PropertyValueFactory<>("aveRating"));

					TableColumn<Movie, String> description = new TableColumn<>("Description");
					description.setCellValueFactory(new PropertyValueFactory<>("description"));

					tableview_results.getColumns().addAll(movieName, earnings, releaseDate, runTime, director, aveRating, description);
	//				sortByCB.setItems(FXCollections.observableArrayList("Movie ID", "Title", "Earnings", "Release Date", "Run Time", "Director", "Average Rating", "Description"));
				} else if(table.equals("review")) {
					TableColumn<Movie, String> movieName = new TableColumn<>("Title");
					movieName.setCellValueFactory(new PropertyValueFactory<>("movieName"));

					TableColumn<Movie, String> rating = new TableColumn<>("Rating");
					rating.setCellValueFactory(new PropertyValueFactory<>("rating"));

					TableColumn<Movie, String> username = new TableColumn<>("Username");
					username.setCellValueFactory(new PropertyValueFactory<>("username"));

					TableColumn<Movie, String> review = new TableColumn<>("Review");
					review.setCellValueFactory(new PropertyValueFactory<>("review"));

					tableview_results.getColumns().addAll(movieName, rating, username, review);
	//				sortByCB.setItems(FXCollections.observableArrayList("Title", "Rating", "Username", "Review", "Run Time", "Director", "Average Rating", "Description"));
				} else if(table.equals("director")) {
					TableColumn<Movie, String> name = new TableColumn<>("Director Name");
					name.setCellValueFactory(new PropertyValueFactory<>("directorName"));

					TableColumn<Movie, String> dirBDay = new TableColumn<>("Birthday");
					dirBDay.setCellValueFactory(new PropertyValueFactory<>("birthdate"));

					TableColumn<Movie, String> directorID = new TableColumn<>("Director ID");
					directorID.setCellValueFactory(new PropertyValueFactory<>("directorID"));

					tableview_results.getColumns().addAll(name, dirBDay, directorID);
	//				sortByCB.setItems(FXCollections.observableArrayList("Director Name", "Birthday", "Director ID"));
				}
			}
		});

		// Assigned the action that is caused by the "Donate" button being clicked.
		submitSearch.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Clear the current items in the tableview
				tableview_results.getItems().clear();

				// Configure the ListView to display all the user's events
				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;

				String searchValue = "";
				String sortBy = "";
				String sortType = "";
				String table = "";

				// Check if a table to search on is selected
				if(!searchTypeCB.getSelectionModel().isEmpty()) {
					table = searchTypeCB.getValue().toString().toLowerCase();
				} else {
					System.out.println("No search type selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select a search type.");
					alert.show();
					return;
				}

				// Check if the search bar is empty
				if(!searchTF.getText().trim().isEmpty()) {
					searchValue = searchTF.getText();
				}
				// Check if the attribute to sort by is empty
//				if(!sortByCB.getSelectionModel().isEmpty()) {
//					sortBy = sortByCB.getValue().toString();
//				}

				try {
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
					if(table.equals("movie")) {
						if(searchValue.equals("")) {
							psQuery = connection.prepareStatement("SELECT movie_id, movie.name AS movieName, gross, release_date, run_time, director.name AS dirName, avg(rate) as AveRating, description " +
																		"FROM rating JOIN movie JOIN directed_by JOIN director " +
																			"ON rating.fk_movie_id = movie.movie_id " +
																			"AND movie.movie_id = directed_by.movie_id_fk " +
																			"AND directed_by.director_id_fk = director.director_id " +
																		"GROUP BY movie_id");
						} else {
							psQuery = connection.prepareStatement("SELECT movie_id, movie.name AS movieName, gross, release_date, run_time, director.name AS dirName, avg(rate) as AveRating, description " +
																		"FROM rating JOIN movie JOIN directed_by JOIN director " +
																			"ON rating.fk_movie_id = movie.movie_id " +
																			"AND movie.movie_id = directed_by.movie_id_fk " +
																			"AND directed_by.director_id_fk = director.director_id " +
																		"WHERE movie.name LIKE ? " +
																		"GROUP BY movie_id");
							psQuery.setString(1, "%" + searchValue + "%");
						}
					} else if(table.equals("review")) {
						if(searchValue.equals("")) {
							psQuery = connection.prepareStatement("SELECT rating_id, movie_id, movie.name AS movieName, rate, review, user_id, username " +
																			"FROM user JOIN rating JOIN movie " +
																				"ON user.user_id = rating.fk_user_id " +
																				"AND rating.fk_movie_id = movie.movie_id ");
						} else {
							psQuery = connection.prepareStatement("SELECT rating_id, movie_id, movie.name AS movieName, rate, review, user_id, username " +
																			"FROM user JOIN rating JOIN movie " +
																				"ON user.user_id = rating.fk_user_id " +
																				"AND rating.fk_movie_id = movie.movie_id " +
																				"WHERE user.username LIKE ?");
							psQuery.setString(1, "%" + searchValue + "%");
						}
					} else if(table.equals("director")){
						if(searchValue.equals("")) {
							psQuery = connection.prepareStatement("SELECT director_id, Name, birthdate FROM director");
						} else {
							psQuery = connection.prepareStatement("SELECT director_id, Name, birthdate FROM director WHERE Name LIKE ?");
							psQuery.setString(1, "%" + searchValue + "%");
						}
					}
					resultSet = psQuery.executeQuery();

					while(resultSet.next()) {
						if(table.equals("movie")) {
							String curMovieID = resultSet.getInt("movie_id") + "";
							String curName = resultSet.getString("movieName");
							String curEarnings = resultSet.getInt("gross") + "";
							String curReleaseDate = resultSet.getString("release_date");
							String curRunTime = resultSet.getInt("run_time") + "";
							String curDirector = resultSet.getString("dirName");
							String curAveRating = resultSet.getInt("aveRating") + "/5";
							String curDescription = resultSet.getString("description");

							tableview_results.getItems().add(new Movie(curMovieID, curName, curEarnings, curReleaseDate, curRunTime, curDirector, curAveRating, curDescription));
						} else if(table.equals("review")) {
							String curRatingID = resultSet.getInt("rating_id") + "";
							String curMovieID = resultSet.getInt("movie_id") + "";
							String curMovieName = resultSet.getString("movieName");
							String curRating = resultSet.getInt("rate") + "/5";
							String curReview = resultSet.getString("review");
							String curUserID = resultSet.getInt("user_id") + "";
							String curUsername = resultSet.getString("username");

							tableview_results.getItems().add(new Review(curRatingID, curMovieID, curMovieName, curRating, curReview, curUserID, curUsername));
						} else if(table.equals("director")){
							String curDirectorID = resultSet.getInt("director_id") + "";
							String curName = resultSet.getString("Name");
							String curBday = resultSet.getString("birthdate");

							tableview_results.getItems().add(new Director(curDirectorID, curName, curBday));
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
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
		label_account_type.setText(accountType);

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

			label_account_type.setText("Login");
		}

	}

	protected static class Movie {

		private final SimpleStringProperty movieID;
		private final SimpleStringProperty movieName;
		private final SimpleStringProperty earnings;
		private final SimpleStringProperty releaseDate;
		private final SimpleStringProperty runTime;
		private final SimpleStringProperty director;
		private final SimpleStringProperty aveRating;
		private final SimpleStringProperty description;

		private Movie(String movieID, String name, String earnings, String releaseDate, String runTime, String director, String aveRating, String description) {
			this.movieID = new SimpleStringProperty(movieID);
			this.movieName = new SimpleStringProperty(name);
			this.earnings = new SimpleStringProperty(earnings);
			this.releaseDate = new SimpleStringProperty(releaseDate);
			this.runTime = new SimpleStringProperty(runTime);
			this.director = new SimpleStringProperty(director);
			this.aveRating = new SimpleStringProperty(aveRating);
			this.description = new SimpleStringProperty(description);

		}

		public String getMovieID() {return movieID.get();}
		public void setMovieID(String movieID) {this.movieID.set(movieID);}

		public String getMovieName() {return movieName.get();}
		public void setMovieName(String name) {this.movieName.set(name);}

		public String getEarnings() {return earnings.get();}
		public void setEarnings(String earnings) {this.earnings.set(earnings);}

		public String getReleaseDate() {return releaseDate.get();}
		public void setReleaseDate(String releaseDate) {this.releaseDate.set(releaseDate);}

		public String getRunTime() {return runTime.get();}
		public void setRunTime(String runTime) {this.runTime.set(runTime);}

		public String getDirector() {return director.get();}
		public void setDirector(String director) {this.director.set(director);}

		public String getAveRating() {return aveRating.get();}
		public void setAveRating(String aveRating) {this.aveRating.set(aveRating);}

		public String getDescription() {return description.get();}
		public void setDescription(String description) {this.description.set(description);}
	}

	protected static class Review {

		private final SimpleStringProperty ratingID;
		private final SimpleStringProperty movieID;
		private final SimpleStringProperty movieName;
		private final SimpleStringProperty rating;
		private final SimpleStringProperty review;
		private final SimpleStringProperty userID;
		private final SimpleStringProperty username;


		public Review(String ratingID, String movieID, String movieName, String rating, String review, String userID, String  username) {
			this.ratingID = new SimpleStringProperty(ratingID);
			this.movieID = new SimpleStringProperty(movieID);
			this.movieName = new SimpleStringProperty(movieName);
			this.rating = new SimpleStringProperty(rating);
			this.review = new SimpleStringProperty(review);
			this.userID = new SimpleStringProperty(userID);
			this.username = new SimpleStringProperty(username);
		}

		public String getRatingID() {return ratingID.get();}
		public void setRatingID(String reviewID) {this.ratingID.set(reviewID);}

		public String getMovieID() {return movieID.get();}
		public void setMovieID(String movieID) {this.movieID.set(movieID);}

		public String getMovieName() {return movieName.get();}
		public void setMovieName(String movieName) {this.movieName.set(movieName);}

		public String getRating() {return rating.get();}
		public void setRating(String rating) {this.rating.set(rating);}

		public String getReview() {return review.get();}
		public void setReview(String review) {this.review.set(review);}

		public String getUserID() {return userID.get();}
		public void setUserID(String userID) {this.userID.set(userID);}

		public String getUsername() {return username.get();}
		public void setUsername(String username) {this.username.set(username);}
	}

	protected static class Director {

		private final SimpleStringProperty directorID;
		private final SimpleStringProperty directorName;
		private final SimpleStringProperty birthdate;


		public Director(String directorID, String name, String birthdate) {
			this.directorID = new SimpleStringProperty(directorID);
			this.directorName = new SimpleStringProperty(name);
			this.birthdate = new SimpleStringProperty(birthdate);
		}

		public String getDirectorID() {return directorID.get();}
		public void setDirectorID(String directorID) {this.directorID.set(directorID);}

		public String getDirectorName() {return directorName.get();}
		public void setDirectorName(String name) {this.directorName.set(name);}

		public String getBirthdate() {return birthdate.get();}
		public void setBirthdate(String birthdate) {this.birthdate.set(birthdate);}
	}


}
