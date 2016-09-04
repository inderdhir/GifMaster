package com.inderdhir.gifmaster.core.di.module;

import android.content.res.AssetManager;

import com.inderdhir.gifmaster.BuildConfig;
import com.inderdhir.gifmaster.core.CacheControlInterceptor;
import com.inderdhir.gifmaster.core.GifMasterApplication;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    private static final String DEBUG_PROPERTIES_FILENAME = "config.debug.properties";
    private static final String RELEASE_PROPERTIES_FILENAME = "config.release.properties";
    private static final int OKHTTP_CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB

    // Properties
    private static final String API_KEY = "api_key";
    private static final String BASE_URL = "base_url";


    public NetworkModule() {
    }

    @Provides
    @Singleton
    Cache provideCache(GifMasterApplication application) {
        return new Cache(application.getCacheDir(), OKHTTP_CACHE_SIZE);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(final Properties properties,
                                            final Cache cache,
                                            final GifMasterApplication application) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(httpLoggingInterceptor);
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
        // Caching
        CacheControlInterceptor cacheControlInterceptor = new CacheControlInterceptor(application);
        httpClient.cache(cache);
        httpClient.addNetworkInterceptor(cacheControlInterceptor);
        httpClient.addInterceptor(cacheControlInterceptor); // For offline mode

        httpClient.connectTimeout(10, TimeUnit.SECONDS);
        httpClient.readTimeout(10, TimeUnit.SECONDS);

        return httpClient.build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(Properties properties, OkHttpClient httpClient,
                                    GsonConverterFactory factory) {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(properties.getProperty(BASE_URL))
                        .addConverterFactory(factory)
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
}
