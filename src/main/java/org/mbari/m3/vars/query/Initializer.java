package org.mbari.m3.vars.query;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mbari.m3.vars.query.services.ConceptService;
import org.mbari.m3.vars.query.services.varskbserver.v1.KBConceptService;
import org.mbari.m3.vars.query.services.varskbserver.v1.KBWebServiceFactory;
import org.mbari.m3.vars.query.utils.LessCSSLoader;

import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

/**
 * @author Brian Schlining
 * @since 2017-11-02T14:41:00
 */
public class Initializer {

    public static final Config CONFIG = ConfigFactory.load();
    private static UIToolBox toolBox;
    private static Injector injector;


    private static UIToolBox getToolBox() {
        if (toolBox == null) {
            Services services = getInjector().getInstance(Services.class);
            ResourceBundle bundle = ResourceBundle.getBundle("i18n",
                    Locale.getDefault());
            // We're using less!! Load it using our custom loader
            LessCSSLoader lessLoader = new LessCSSLoader();
            String stylesheet = lessLoader.loadLess(Initializer.class.getResource("/less/query.less"))
                    .toExternalForm();
            toolBox = new UIToolBox(services, new EventBus(), bundle, CONFIG,
                    Arrays.asList(stylesheet));
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
}
