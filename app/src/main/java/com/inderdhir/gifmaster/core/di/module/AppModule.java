package com.inderdhir.gifmaster.core.di.module;

import com.inderdhir.gifmaster.core.GifMasterApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private GifMasterApplication mApplication;

    public AppModule(GifMasterApplication mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    @Singleton
    GifMasterApplication providesApplication() {
        return mApplication;
    }
}

