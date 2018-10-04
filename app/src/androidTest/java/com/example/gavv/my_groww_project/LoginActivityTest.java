package com.example.gavv.my_groww_project;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTest = new ActivityTestRule<LoginActivity>(LoginActivity.class);
    private LoginActivity loginActivity = null;

    @Before
    public void setUp() throws Exception {
        loginActivity = loginActivityTest.getActivity();
    }

    @Test
    public void testLaunch(){
        View email = loginActivity.findViewById(R.id.login_email);
        View password =loginActivity.findViewById(R.id.login_password);
        View button = loginActivity.findViewById(R.id.login_btn);

        TestCase.assertNotNull(email);
        TestCase.assertNotNull(password);
        TestCase.assertNotNull(button);

    }

    @After
    public void tearDown() throws Exception {
        loginActivity = null;
    }
}