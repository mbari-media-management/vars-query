package org.mbari.m3.vars.query;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

/**
 * @author Brian Schlining
 * @since 2019-08-20T11:54:00
 */
public class AppConfig {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final Config config;
    protected final URL defaultUrl;
    protected Duration defaultTimeout = Duration.ofSeconds(20);

    public AppConfig(Config config) {
        this.config = config;
        try {
            defaultUrl = new URL("http://localhost");
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Unable to create a default URL for config values", e);
        }
    }

    public <T> T read(String path, Function<String, T> fn, T defaultValue) {
        try {
            return fn.apply(path);
        }
        catch (Exception e) {
            log.warn("Unable to find a config value at path {}", path);
            return defaultValue;
        }
    }

    public URL readUrl(String path) {
        String url = null;
        try {
            url = config.getString(path);
            return new URL(url);
        }
        catch (MalformedURLException e) {
            log.warn("The URL {} defined in the config at {} is malformed", url, path);
            return defaultUrl;
        }
        catch (Exception e) {
            log.warn("Unable to find a config value at path {}", path);
            return defaultUrl;
        }
    }

    public Config getConfig() {
        return config;
    }

    public  String getAnnosaurusJdbcDriver() {
        return read("annosaurus.jdbc.driver", config::getString, "");
    }

    public  String getAnnosaurusJdbcPassword() {
        return read("annosaurus.jdbc.password", config::getString, "");
    }

    public  String getAnnosaurusJdbcUrl() {
        return  read("annosaurus.jdbc.url", config::getString, "");
    }

    public  String getAnnosaurusJdbcUser() {
        return read("annosaurus.jdbc.user", config::getString, "");
    }

    public URL getConceptServiceUrl() {
        return readUrl("concept.service.url");
    }

    public Duration getConceptServiceTimeout() {
        return read("concept.service.timeout", config::getDuration, defaultTimeout);
    }

    public int getSharktopodaPort() {
        return read("sharktopoda.port", config::getInt, 8800);
    }

    public String getVarsQueryFrameTitle() {
        return  read("vars.query.frame.title", config::getString, "VARS Query");
    }

    public String getVarsQueryResultsCoalesceKey() {
        return read("vars.query.results.coalesce.key", config::getString, "observation_uuid");
    }

    public String getVarsQueryElapsedTimeColumn() {
        return read("vars.query.elapsed.time.column", config::getString, "index_elapsed_time_millis");
    }

    public List<String> getVarsQueryColumnDefaultReturns() {
        return read("vars.query.column.default.returns", config::getStringList, List.of("concept"));
    }

    public ZonedDateTime getVarsAnnotationStartDate() {
        String startDate = config.getString("vars.annotation.start.date");
        return ZonedDateTime.ofInstant(Instant.parse(startDate), ZoneId.of("UTC"));
    }

}
