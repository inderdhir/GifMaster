package com.inderdhir.gifmaster.core;

import com.inderdhir.gifmaster.core.di.ApplicationComponent;
import com.inderdhir.gifmaster.core.di.module.TestAppModule;
import com.inderdhir.gifmaster.core.di.module.TestGiphyModule;
import com.inderdhir.gifmaster.core.di.module.TestNetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestAppModule.class, TestNetworkModule.class, TestGiphyModule.class})
public interface TestApplicationComponent extends ApplicationComponent {
}
