package com.inderdhir.gifmaster.model;

import java.io.Serializable;


public class GifItem implements Serializable {

    private String mUrl;

    public GifItem(final String mUrl) {
        this.mUrl = mUrl;
    }

    public String getUrl() {
        return mUrl;
    }
}
