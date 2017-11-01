package org.mbari.m3.vars.query.services;


import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


/**
 * Base interface for generating services from Retrofit interfaces.
 *
 * @author Brian Schlining
 * @since 2017-05-26T11:00:00
 */
public abstract class RetrofitServiceFactory {

    private final Duration timeout;
    private final Retrofit.Builder retrofitBuilder;
    private final Logger log = LoggerFactory.getLogger(getClass());


    public RetrofitServiceFactory(String endpoint) {
        this(endpoint, Duration.ofMillis(10000));
    }

    public RetrofitServiceFactory(String endpoint, Duration timeout) {
        this.timeout = timeout;
        String correctedEndpoint = (endpoint.endsWith("/")) ? endpoint : endpoint + "/";

        retrofitBuilder  = new Retrofit.Builder()
                .baseUrl(correctedEndpoint)
                .addConverterFactory(GsonConverterFactory.create(getGson()));
    }

    public RetrofitServiceFactory(String endpoint, Duration timeout, Executor executor) {
        this.timeout = timeout;
        String correctedEndpoint = (endpoint.endsWith("/")) ? endpoint : endpoint + "/";

        retrofitBuilder  = new Retrofit.Builder()
                .baseUrl(correctedEndpoint)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .callbackExecutor(executor);
    }

    public <S> S create(Class<S> clazz) {

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor(log::debug);
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .writeTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);

        httpClient.addInterceptor(logger);
        retrofitBuilder.client(httpClient.build());

        return retrofitBuilder.build()
                .create(clazz);
    }

    public abstract Gson getGson();

}
