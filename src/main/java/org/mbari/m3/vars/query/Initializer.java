package org.mbari.m3.vars.query;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mbari.m3.vars.query.util.LessCSSLoader;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Brian Schlining
 * @since 2017-11-02T14:41:00
 */
public class Initializer {

    public static final Config CONFIG = ConfigFactory.load();
    private static UIToolBox toolBox;
    private static Path settingsDirectory;
    private static ExecutorService  executor = new ForkJoinPool();

    public static UIToolBox getToolBox() {
        if (toolBox == null) {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n",
                    Locale.getDefault());
            // We're using less!! Load it using our custom loader
            LessCSSLoader lessLoader = new LessCSSLoader();
            String stylesheet = lessLoader.loadLess(Initializer.class.getResource("/less/query.less"))
                    .toExternalForm();
            Services services = ServicesBuilder.build(CONFIG, executor);
            toolBox = new UIToolBox(services,
                    new Data(),
                    new EventBus(),
                    bundle,
                    CONFIG,
                    Collections.singletonList(stylesheet),
                    executor);
        }
        return toolBox;
    }


    /**
     * The settingsDirectory is scratch space for VARS
     *
     * @return The path to the settings directory. null is returned if the
     *  directory doesn't exist (or can't be created) or is not writable.
     */
    public static Path getSettingsDirectory() {
        if (settingsDirectory == null) {
            String home = System.getProperty("user.home");
            settingsDirectory = Paths.get(home, ".vars");

            // Make sure the directory exists and we can write to it.
            if (!Files.exists(settingsDirectory)) {
                try {
                    Files.createDirectory(settingsDirectory);
                    if (!Files.isWritable(settingsDirectory)) {
                        settingsDirectory = null;
                    }
                }
                catch (IOException e) {
                    String msg = "Unable to create a setting directory at " + settingsDirectory + ".";
                    LoggerFactory.getLogger(Initializer.class).error(msg, e);
                    settingsDirectory = null;
                }
            }

        }
        return settingsDirectory;
    }

}
