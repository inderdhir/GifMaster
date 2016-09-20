package com.inderdhir.gifmaster.core.di.module;

import com.inderdhir.gifmaster.core.GifMasterApplication;
import com.inderdhir.gifmaster.core.TestApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestAppModule extends AppModule {

    public TestAppModule(TestApplication mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    @Singleton
    protected GifMasterApplication providesApplication() {
        return mApplication;
    }
}
