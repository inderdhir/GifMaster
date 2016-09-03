package com.inderdhir.gifmaster.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.inderdhir.gifmaster.R;
import com.inderdhir.gifmaster.core.GifMasterApplication;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GifMasterApplication) getApplication()).component().inject(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
