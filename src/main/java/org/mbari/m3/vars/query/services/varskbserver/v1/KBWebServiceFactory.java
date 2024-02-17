package org.mbari.m3.vars.query.services.varskbserver.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mbari.m3.vars.query.gson.ByteArrayConverter;
import org.mbari.m3.vars.query.gson.DurationConverter;
import org.mbari.m3.vars.query.gson.InstantConverter;
import org.mbari.m3.vars.query.gson.TimecodeConverter;
import org.mbari.m3.vars.query.services.RetrofitServiceFactory;
import org.mbari.vcr4j.time.Timecode;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;

/**
 * @author Brian Schlining
 * @since 2017-10-31T12:50:00
 */
public class KBWebServiceFactory extends RetrofitServiceFactory {


    public KBWebServiceFactory(String endpoint,
                               Duration timeout,
                               Executor executor) {
        super(endpoint, timeout, executor);
    }


    public Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .registerTypeAdapter(Instant.class, new InstantConverter())
                .registerTypeAdapter(Duration.class, new DurationConverter())
                .registerTypeAdapter(Timecode.class, new TimecodeConverter())
                .registerTypeAdapter(byte[].class, new ByteArrayConverter());

        // Register java.time.Instant
        return gsonBuilder.create();

    }

}