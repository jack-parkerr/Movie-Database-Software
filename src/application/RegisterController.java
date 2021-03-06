package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private Button button_register;
    @FXML private Button button_log_in;

    @FXML private TextField tf_firstname;
    @FXML private TextField tf_lastname;
    @FXML private TextField tf_email;
    @FXML private PasswordField pf_password;
    @FXML private PasswordField pf_confirm_password;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup accountToggle = new ToggleGroup();

        // Assigned the action that is caused by the "Register" button being clicked.
        button_register.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {


                if (!tf_email.getText().trim().isEmpty() && !pf_password.getText().trim().isEmpty() && !pf_confirm_password.getText().trim().isEmpty() && !tf_firstname.getText().trim().isEmpty()) {
                    if(pf_confirm_password.getText().equals(pf_password.getText())) {
                        DBUtils.signUpUser(event, tf_email.getText(), pf_password.getText(), tf_firstname.getText(), tf_lastname.getText(), "User");
                    } else {
                        System.out.println("Passwords do not match.");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Passwords do not match!");
                        alert.show();
                    }
                } else {
                    System.out.println("Please fill in all information");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please fill in all information to sign up!");
                    alert.show();
                }
            }
        });

        // Assigned the action that is caused by the "Log in!" button being clicked.
        button_log_in.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "LogIn.fxml", "Log in!", -1, null, null, null);
            }
        });
    }
}
