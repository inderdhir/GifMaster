package com.inderdhir.gifmaster.core.di.module;


import com.inderdhir.gifmaster.core.GiphyRetrofitService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.mockito.Mockito.mock;

public class TestGiphyModule extends GiphyModule {

    public TestGiphyModule() {
        super();
    }

//    @Override
//    protected GiphyRetrofitService provideGiphyService(Retrofit retrofit) {
//        return mock(GiphyRetrofitService.class);
//    }

//    @Override
//    protected GsonConverterFactory provideGsonConverterFactory() {
//        return mock(GsonConverterFactory.class);
//    }
}
