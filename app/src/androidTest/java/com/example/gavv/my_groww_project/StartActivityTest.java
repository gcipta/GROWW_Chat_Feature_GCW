package com.example.gavv.my_groww_project;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static junit.framework.TestCase.assertNotNull;

public class StartActivityTest {

    @Rule
    public ActivityTestRule<StartActivity> sActivityTestRule = new ActivityTestRule<StartActivity>(StartActivity.class);

    private StartActivity startActivity = null;

    @Before
    public void setUp() throws Exception {

        startActivity = sActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View loginButton  = startActivity.findViewById(R.id.start_login_btn);
        View regButton = startActivity.findViewById(R.id.start_reg_btn);

        assertNotNull(loginButton);
        assertNotNull(regButton);
    }

    @After
    public void tearDown() throws Exception {
    }
}