package com.inderdhir.gifmaster.core.di;

import com.inderdhir.gifmaster.core.GifMasterApplication;
import com.inderdhir.gifmaster.core.di.module.ApiModule;
import com.inderdhir.gifmaster.core.di.module.AppModule;
import com.inderdhir.gifmaster.ui.activity.MainActivity;
import com.inderdhir.gifmaster.ui.fragment.MainActivityFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface ApplicationComponent {
    void inject(GifMasterApplication app);

    void inject(MainActivity activity);

    void inject(MainActivityFragment fragment);
}
