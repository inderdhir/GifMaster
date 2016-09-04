package com.inderdhir.gifmaster.core.di;

import com.inderdhir.gifmaster.core.GifMasterApplication;
import com.inderdhir.gifmaster.core.di.module.GiphyModule;
import com.inderdhir.gifmaster.core.di.module.NetworkModule;
import com.inderdhir.gifmaster.core.di.module.AppModule;
import com.inderdhir.gifmaster.ui.activity.MainActivity;
import com.inderdhir.gifmaster.ui.fragment.MainFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class, GiphyModule.class})
public interface ApplicationComponent {
    void inject(GifMasterApplication app);

    void inject(MainActivity activity);

    void inject(MainFragment fragment);
}
