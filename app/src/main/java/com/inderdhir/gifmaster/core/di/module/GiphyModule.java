package com.inderdhir.gifmaster.core.di.module;


import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.inderdhir.gifmaster.core.GifDeserializer;
import com.inderdhir.gifmaster.core.GiphyRetrofitService;
import com.inderdhir.gifmaster.model.GifItem;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class GiphyModule {

    @Inject
    Retrofit retrofit;

    public GiphyModule() {
    }

    @Provides
    @Singleton
    GiphyRetrofitService provideGiphyService(Retrofit retrofit) {
        return retrofit.create(GiphyRetrofitService.class);
    }

    @Provides
    @Singleton
    GsonConverterFactory provideGsonConverterFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Adding custom deserializers
        Type gifItemsListType = new TypeToken<List<GifItem>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(gifItemsListType, new GifDeserializer());
        return GsonConverterFactory.create(gsonBuilder.create());
    }
}
