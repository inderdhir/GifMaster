package com.inderdhir.gifmaster.core;


import com.inderdhir.gifmaster.core.di.DaggerApplicationComponent;
import com.inderdhir.gifmaster.core.di.module.TestAppModule;
import com.inderdhir.gifmaster.core.di.module.TestGiphyModule;
import com.inderdhir.gifmaster.core.di.module.TestNetworkModule;

public class TestApplication extends GifMasterApplication {

    @Override
    protected void initDagger() {
        applicationComponent = DaggerApplicationComponent.builder()
                .appModule(new TestAppModule(this))
                .networkModule(new TestNetworkModule())
                .giphyModule(new TestGiphyModule())
                .build();
    }
}
