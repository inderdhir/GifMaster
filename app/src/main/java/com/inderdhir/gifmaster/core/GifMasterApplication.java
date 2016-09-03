package com.inderdhir.gifmaster.core;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.inderdhir.gifmaster.core.di.ApplicationComponent;
import com.inderdhir.gifmaster.core.di.DaggerApplicationComponent;
import com.inderdhir.gifmaster.core.di.module.ApiModule;
import com.inderdhir.gifmaster.core.di.module.AppModule;


public class GifMasterApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule())
                .build();

        Fresco.initialize(this);
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }

}
