package com.inderdhir.gifmaster.util;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class Utils {

    public static void hideSoftKeyboard(FragmentActivity activity, View view) {
        final InputMethodManager im = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getRootView().getWindowToken(), 0);
    }
}
