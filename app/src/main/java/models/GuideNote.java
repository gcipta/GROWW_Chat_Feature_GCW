package models;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gavv.my_groww_project.R;

public class GuideNote extends Fragment {

    private String content;
    TextView textViewGuideNote;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.guide_notes_child, container, false);

        // Get the guide notes' content.
        String content = getArguments().getString("note");

        // Set such that the content is shown.
        textViewGuideNote = (TextView) view.findViewById(R.id.guide_notes_child);
        textViewGuideNote.setText(content);

        return view;

    }

    /**
     * Set the content of the guide notes.
     * @param content
     */
    public void setContent(String content) {

        this.content = content;
    }

    /**
     * Return the text content of the guide note.
     * @return text content
     */
    public String getContent() {

        return content;
    }
}
