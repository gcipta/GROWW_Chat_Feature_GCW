package com.example.gavv.my_groww_project;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> rActivityTest = new ActivityTestRule<RegisterActivity>(RegisterActivity.class);

    private RegisterActivity rActivity = null;

    @Before
    public void setUp() throws Exception {
        rActivity = rActivityTest.getActivity();
    }

    @Test
    public void testLaunch() {
        View name = rActivity.findViewById(R.id.reg_display_name);
        View email = rActivity.findViewById(R.id.reg_email);
        View password = rActivity.findViewById(R.id.reg_password);
        View button = rActivity.findViewById(R.id.reg_create_btn);

        TestCase.assertNotNull(name);
        TestCase.assertNotNull(email);
        TestCase.assertNotNull(password);
        TestCase.assertNotNull(button);
    }

    @After
    public void tearDown() throws Exception {
        rActivity = null;
    }
}