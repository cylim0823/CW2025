package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The entry point for the TetrisJFX application.
 * <p>
 * This class extends {@link Application} to launch the JavaFX lifecycle.
 * Its primary responsibility is to configure the primary stage (window),
 * load the initial scene (Main Menu), and display the application to the user.
 * </p>
 * @author Chen Yu
 * @version 1.0
 */
public class Main extends Application {

    /**
     * The main entry method for all JavaFX applications.
     * <p>
     * This method is called after the system is ready for the application to begin running.
     * It performs the following initialization steps:
     * <ol>
     * <li>Loads the {@code mainMenu.fxml} resource using {@link FXMLLoader}.</li>
     * <li>Constructs the main {@link Scene} with dimensions 1000x700.</li>
     * <li>Sets the application title to "TetrisJFX".</li>
     * <li>Displays the primary stage.</li>
     * </ol>
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * @throws Exception if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load the new mainMenu.fxml instead of gameLayout.fxml
        URL location = getClass().getClassLoader().getResource("fxml/mainMenu.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}