package com.example.gavv.my_groww_project;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View pager = mActivity.findViewById(R.id.main_tabPager);
        View nav_button = mActivity.findViewById(R.id.nav_button);

        assertNotNull(pager);
        assertNotNull(nav_button);
    }

    @After
    public void tearDown() throws Exception {

        mActivity = null;
    }
}