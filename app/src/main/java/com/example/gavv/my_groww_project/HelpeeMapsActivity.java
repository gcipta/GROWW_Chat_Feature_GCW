package com.example.gavv.my_groww_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import controllers.GuideNoteController;

public class HelpeeMapsActivity extends AppCompatActivity {

    GuideNoteController guideNoteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpee_maps);

        guideNoteController = (GuideNoteController) this.getSupportFragmentManager()
                .findFragmentById(R.id.guideNoteFragment);

        guideNoteController.createGuideNote("Faraday Street",
                "On Faraday Street Take Bus no. 767");
        guideNoteController.createGuideNote("University of Melbourne",
                "Get off at University of Melbourne Stop");

    }
}
