package controllers;

import models.GuideNote;
import models.IGuideNoteComponent;
import models.Location;

public class GuideNoteController implements IGuideNoteComponent {
    @Override
    public GuideNote createGuideNote(String note) {
        return null;
    }

    @Override
    public GuideNote createGuideNote(String note, Location location) {
        return null;
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
