package org.mbari.m3.vars.query;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.Preferences;

public class App extends Application {

    private static final String WIDTH_KEY = "stage-width";
    private static final String HEIGHT_KEY = "stage-height";
    private static Logger log;

    public static void main(String[] args) {
        System.getProperties().setProperty("user.timezone", "UTC");
        log = LoggerFactory.getLogger(App.class);
        //Log uncaught Exceptions
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            log.error("Exception in thread [" + thread.getName() + "]", ex);
        });
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load size from local pregs
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        double width = prefs.getDouble(WIDTH_KEY, 1000D);
        double height = prefs.getDouble(HEIGHT_KEY, 800D);

        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.setOnCloseRequest(e -> {
            // Save size on exit
            prefs.putDouble(WIDTH_KEY, primaryStage.getWidth());
            prefs.putDouble(HEIGHT_KEY, primaryStage.getHeight());
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }
}