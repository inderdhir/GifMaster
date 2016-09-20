package com.inderdhir.gifmaster;

import com.inderdhir.gifmaster.ui.activity.MainActivity;
import com.inderdhir.gifmaster.ui.fragment.MainFragment;

import org.junit.Test;
import org.robolectric.Robolectric;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


public class MainActivityTest extends RobolectricGradleTestWrapper {

    @Test
    public void shouldLaunchCorrectFragment() throws Exception {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        assertNotNull(activity);

        assertEquals(activity.getSupportFragmentManager().getFragments().size(), 1);
        assertTrue(activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                instanceof MainFragment);
    }
}