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



public class AdvancedSearchPageController implements Initializable{

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
	@FXML private Button dirRatingButton;
	@FXML private TextField dirRatingNameTF;
	@FXML private TextField dirRatingRateTF;

	@FXML private Button dirNumberButton;
	@FXML private TextField dirNumberNumTF;

	@FXML private Button dirListButton;
	@FXML private TextField dirListTF1;
	@FXML private TextField dirListTF2;
	@FXML private TextField dirListTF3;
	@FXML private TextField dirListTF4;
	@FXML private TextField dirListTF5;

	@FXML private Label aveMoviesLabel;

	@FXML private ListView resultsLV;



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
		resultsLV.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 12px;");

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

		dirRatingButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				String dirName = "";
				int rating;

				resultsLV.getItems().clear();

				if(dirRatingNameTF.getText().equals("")) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please Enter a Director who's name you'd like to search");
					alert.show();
					return;
				} else {
					dirName = dirRatingNameTF.getText();
				}

				if(dirRatingRateTF.getText().equals("")) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please Enter a rating");
					alert.show();
					return;
				} else if(!dirRatingRateTF.getText().matches("[0-5]")){			//contain something other than #'s
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Rating must be a number 1-5");
					alert.show();
					return;
				} else {
					rating = Integer.parseInt(dirRatingRateTF.getText());
				}

				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;

				try {
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");

					psQuery = connection.prepareStatement("SELECT movieName, dirName, AveRating " +
																   "FROM(SELECT movie_id, movie.name AS movieName, gross, release_date, run_time, director.name AS dirName, avg(rate) AS AveRating, description "+
																			"FROM rating JOIN movie JOIN directed_by JOIN director " +
																				"ON rating.fk_movie_id = movie.movie_id " +
																				"AND movie.movie_id = directed_by.movie_id_fk " +
																				"AND directed_by.director_id_fk = director.director_id " +
																				"GROUP BY movie_id) AS a " +
																		"WHERE dirName LIKE ? AND AveRating >= ?");

					psQuery.setString(1, "%" + dirName + "%");
					psQuery.setInt(2, rating);
					resultSet = psQuery.executeQuery();

					while(resultSet.next()) {
						String movieName = resultSet.getString("movieName");
						String retDirName = resultSet.getString("dirName");
						int aveRating = resultSet.getInt("AveRating");
						String result =  "Title: " + movieName + "  |  Director: " + retDirName  + "  |  Average Rating: " + aveRating;
						resultsLV.getItems().add(result);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}


			}
		}));

		dirNumberButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				int num;

				resultsLV.getItems().clear();

				if(dirNumberNumTF.getText().equals("")) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please Enter a number of movies");
					alert.show();
					return;
				} else if(!dirNumberNumTF.getText().matches("[0-9]+")){			//contain something other than #'s
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Rating must be a number");
					alert.show();
					return;
				} else {
					num = Integer.parseInt(dirNumberNumTF.getText());
				}

				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;

				try {
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");

					psQuery = connection.prepareStatement("SELECT count(*) AS Ct, director.name " +
																	"FROM movie JOIN directed_by JOIN director " +
																		"ON movie.movie_id = directed_by.movie_id_fk " +
																		"AND directed_by.director_id_fk = director.director_id " +
																	"GROUP BY director_id " +
																	"HAVING count(*) >= ? " +
																	"ORDER BY Ct DESC");

					psQuery.setInt(1, num);
					resultSet = psQuery.executeQuery();

					while(resultSet.next()) {
						int count = resultSet.getInt("Ct");
						String retDirName = resultSet.getString("director.name");
						String result =  "Director: " + retDirName + "  |  Number of Movies: " + count;
						resultsLV.getItems().add(result);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}));

		dirListButton.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				String dir1 = "";
				String dir2 = "";
				String dir3 = "";
				String dir4 = "";
				String dir5 = "";

				resultsLV.getItems().clear();

				if(dirListTF1.getText() == "" && dirListTF2.getText() == "" && dirListTF3.getText() == "" && dirListTF4.getText() == "" && dirListTF5.getText() == ""){
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please Enter at lease 1 director name.");
					alert.show();
					return;
				} else {
					dir1 = dirListTF1.getText();
					dir2 = dirListTF2.getText();
					dir3 = dirListTF3.getText();
					dir4 = dirListTF4.getText();
					dir5 = dirListTF5.getText();
				}

				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;

				try {
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
					System.out.println("here");
					psQuery = connection.prepareStatement("SELECT movie.name, director.name " +
																	"FROM movie JOIN directed_by JOIN director " +
																	"ON movie.movie_id = directed_by.movie_id_fk " +
																	"AND directed_by.director_id_fk = director.director_id " +
																	"WHERE director.name IN (?, ?, ?, ?, ?)");

					psQuery.setString(1, dir1);
					psQuery.setString(2, dir2);
					psQuery.setString(3, dir3);
					psQuery.setString(4, dir4);
					psQuery.setString(5, dir5);
					resultSet = psQuery.executeQuery();

					while(resultSet.next()) {
						String movieName = resultSet.getString("movie.name");
						String retDirName = resultSet.getString("director.name");
						String result =  "Title: " + movieName + "  |  Director: " + retDirName;
						resultsLV.getItems().add(result);
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

		Connection connection = null;
		PreparedStatement psQuery = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mmdb", "root", "admin");
			System.out.println("here");
			psQuery = connection.prepareStatement("SELECT avg(moviesPerDirector) AS avgMovies " +
														"FROM(SELECT director.director_id, count(*) AS moviesPerDirector " +
																"FROM movie JOIN directed_by JOIN director " +
																	"ON movie.movie_id = directed_by.movie_id_fk " +
																	"AND directed_by.director_id_fk = director.director_id " +
																"GROUP BY director.director_id) AS a");


			resultSet = psQuery.executeQuery();

			if(resultSet.next()) {
				aveMoviesLabel.setText(resultSet.getInt("avgMovies") + "");
			}

		} catch (SQLException e) {
			e.printStackTrace();
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

			label_account_type.setText("Login");
		}

	}

}
