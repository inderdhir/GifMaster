package com.inderdhir.gifmaster.core.di.module;

import com.inderdhir.gifmaster.core.GifMasterApplication;
import com.inderdhir.gifmaster.core.di.module.NetworkModule;

import java.util.Properties;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.mockito.Mockito.mock;


public class TestNetworkModule extends NetworkModule {

    public TestNetworkModule() {
        super();
    }

    @Override
    protected Cache provideCache(GifMasterApplication application) {
        return super.provideCache(application);
    }

    @Override
    public OkHttpClient provideOkHttpClient(Properties properties, Cache cache, GifMasterApplication application) {
        return mock(OkHttpClient.class);
    }

    @Override
    public Retrofit provideRetrofit(Properties properties, OkHttpClient httpClient, GsonConverterFactory factory) {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(properties.getProperty(BASE_URL))
                        .addConverterFactory(factory)
                        .addConverterFactory(GsonConverterFactory.create());

        return builder.client(httpClient).build();
    }

//    @Override
//    public Properties provideProperties(GifMasterApplication application) {
//        return mock(Properties.class);
//    }
}
