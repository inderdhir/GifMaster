package com.inderdhir.gifmaster;

import android.os.Build;

import com.inderdhir.gifmaster.core.TestApplication;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP_MR1,
        application = TestApplication.class)
public class RobolectricGradleTestWrapper {

    private static final String[] mRequiredPermissions = new String[]{};

    @Before
    public void setUp() {
        ShadowApplication app = Shadows.shadowOf(RuntimeEnvironment.application);
        app.grantPermissions(mRequiredPermissions);
    }
}
