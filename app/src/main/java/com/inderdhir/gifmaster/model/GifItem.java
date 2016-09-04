package com.inderdhir.gifmaster.model;

import android.os.Parcel;
import android.os.Parcelable;


public class GifItem implements Parcelable {

    private String mUrl;

    public GifItem(final String mUrl) {
        this.mUrl = mUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    protected GifItem(Parcel in) {
        mUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GifItem> CREATOR = new Parcelable.Creator<GifItem>() {
        @Override
        public GifItem createFromParcel(Parcel in) {
            return new GifItem(in);
        }

        @Override
        public GifItem[] newArray(int size) {
            return new GifItem[size];
        }
    };
}
