package com.inderdhir.gifmaster.core;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.inderdhir.gifmaster.core.di.ApplicationComponent;
import com.inderdhir.gifmaster.core.di.DaggerApplicationComponent;
import com.inderdhir.gifmaster.core.di.module.AppModule;
import com.inderdhir.gifmaster.core.di.module.GiphyModule;
import com.inderdhir.gifmaster.core.di.module.NetworkModule;


public class GifMasterApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .giphyModule(new GiphyModule())
                .build();

        Fresco.initialize(this);
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }
}
