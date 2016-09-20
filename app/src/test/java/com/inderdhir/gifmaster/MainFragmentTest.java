package com.inderdhir.gifmaster;


import android.view.View;

import com.inderdhir.gifmaster.ui.activity.MainActivity;
import com.inderdhir.gifmaster.ui.fragment.MainFragment;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

public class MainFragmentTest extends RobolectricGradleTestWrapper {

    private MainFragment mFragment;

    @Before
    public void setUp() {
        super.setUp();
        mFragment = new MainFragment();
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);
    }

    @Test
    public void backToTopButtonIsNotBeingShown() {
        assertNotNull(mFragment.getView());
        View backToTop = mFragment.getView().findViewById(R.id.back_to_top_text);
        assertNotNull(backToTop);
        assertFalse(backToTop.isShown());
    }
}
