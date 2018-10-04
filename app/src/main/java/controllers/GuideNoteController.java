package controllers;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gavv.my_groww_project.R;

import adapter.ViewPagerAdapter;
import models.GuideNote;

public class GuideNoteController extends Fragment implements IGuideNoteComponent {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private static final int FIRST_NOTE = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_notes_parent, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.guide_notes_parent);

        // Initialize and set the adapter.
        adapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);

        // Set the guide notes to always show the first note.
        viewPager.setCurrentItem(FIRST_NOTE);

        return view;
    }


    @Override
    public void createGuideNote(String title, String note) {

        // Bundle is used to send the note to the Guide Note Fragment.
        Bundle bundle = new Bundle();
        bundle.putString("note", note);

        // Construct the guide note and send the bundle.
        GuideNote guideNote = new GuideNote();
        guideNote.setArguments(bundle);

        // Add the guide note and notified data changed.
        adapter.addGuideNote(guideNote, title);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void createGuideNote(String title, String note, Location location) {

    }


    @Override
    public void sendGuideNote(GuideNote note, int UserID) {

    }

    @Override
    public void editGuideNote(GuideNote oldNote, GuideNote editedNote) {

    }

    @Override
    public void displayGuideNote(GuideNote note) {

    }
}
