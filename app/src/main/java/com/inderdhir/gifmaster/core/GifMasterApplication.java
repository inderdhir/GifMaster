package com.inderdhir.gifmaster.core;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.inderdhir.gifmaster.BuildConfig;
import com.inderdhir.gifmaster.core.di.ApplicationComponent;
import com.inderdhir.gifmaster.core.di.DaggerApplicationComponent;
import com.inderdhir.gifmaster.core.di.module.AppModule;
import com.inderdhir.gifmaster.core.di.module.GiphyModule;
import com.inderdhir.gifmaster.core.di.module.NetworkModule;
import com.squareup.leakcanary.LeakCanary;

import static android.os.Build.VERSION_CODES.GINGERBREAD;


public class GifMasterApplication extends Application {

    protected ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initDagger();

        Fresco.initialize(this);
        if (BuildConfig.DEBUG) {
            enabledStrictMode();
            LeakCanary.install(this);
        }
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }

    protected void initDagger(){
        applicationComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .giphyModule(new GiphyModule())
                .build();
    }

    private void enabledStrictMode() {
        if (Build.VERSION.SDK_INT >= GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }
}
