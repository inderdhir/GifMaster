package com.inderdhir.gifmaster.core.di.module;

import android.content.res.AssetManager;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.inderdhir.gifmaster.BuildConfig;
import com.inderdhir.gifmaster.core.GifDeserializer;
import com.inderdhir.gifmaster.core.GifMasterApplication;
import com.inderdhir.gifmaster.core.GiphyRetrofitService;
import com.inderdhir.gifmaster.model.GifItem;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiModule {

    private static final String DEBUG_PROPERTIES_FILENAME = "config.debug.properties";
    private static final String RELEASE_PROPERTIES_FILENAME = "config.release.properties";

    // Properties
    private static final String API_KEY = "api_key";
    private static final String BASE_URL = "base_url";

    @Inject
    GifMasterApplication mApplication;

    public ApiModule() {
    }

    @Provides
    @Singleton
    public GiphyRetrofitService provideGiphyService(Retrofit retrofit) {
        return retrofit.create(GiphyRetrofitService.class);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(final Properties properties) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(new HttpLoggingInterceptor());
        }
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(final Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter(API_KEY, properties.getProperty(API_KEY))
                        .build();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(Properties properties, OkHttpClient httpClient) {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(properties.getProperty(BASE_URL))
                        .addConverterFactory(buildGsonConverter())
                        .addConverterFactory(GsonConverterFactory.create());

        return builder.client(httpClient).build();
    }

    @Provides
    @Singleton
    public Properties provideProperties(GifMasterApplication application) {
        final Properties properties = new Properties();
        final AssetManager assetManager = application.getAssets();
        try {
            if (BuildConfig.DEBUG) {
                properties.load(assetManager.open(DEBUG_PROPERTIES_FILENAME));
            } else {
                properties.load(assetManager.open(RELEASE_PROPERTIES_FILENAME));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private GsonConverterFactory buildGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Adding custom deserializers
        Type gifItemsListType = new TypeToken<List<GifItem>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(gifItemsListType, new GifDeserializer());
        return GsonConverterFactory.create(gsonBuilder.create());
    }
}
