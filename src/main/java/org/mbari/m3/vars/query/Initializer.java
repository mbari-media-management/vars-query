package org.mbari.m3.vars.query;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mbari.m3.vars.query.services.AsyncQueryService;
import org.mbari.m3.vars.query.util.LessCSSLoader;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Brian Schlining
 * @since 2017-11-02T14:41:00
 */
public class Initializer {

    public static final Config CONFIG = ConfigFactory.load();
    private static UIToolBox toolBox;
    private static Injector injector;
    private static Path settingsDirectory;

    public static UIToolBox getToolBox() {
        if (toolBox == null) {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n",
                    Locale.getDefault());
            // We're using less!! Load it using our custom loader
            LessCSSLoader lessLoader = new LessCSSLoader();
            String stylesheet = lessLoader.loadLess(Initializer.class.getResource("/less/query.less"))
                    .toExternalForm();
            AsyncQueryService queryService = getInjector().getInstance(AsyncQueryService.class);
            toolBox = new UIToolBox(queryService, new EventBus(), bundle, CONFIG,
                    Arrays.asList(stylesheet), new ForkJoinPool());
        }
        return toolBox;
    }

    public static Injector getInjector() {
        if (injector == null) {
            String moduleName = CONFIG.getString("app.injector.module.class");
            try {
                Class clazz = Class.forName(moduleName);
                Module module = (Module) clazz.newInstance();
                injector = Guice.createInjector(module);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create dependency injector", e);
            }
        }
        return injector;
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
