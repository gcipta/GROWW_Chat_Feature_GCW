package controllers;

import models.GuideNote;
import models.Location;

public interface IGuideNoteComponent {

    /**
     * create a guide note
     * @param note text string of the message
     * @return the a new GuideNote
     */
    GuideNote createGuideNote(String note);

    /**
     * create a guide note
     * @param note     text string of the message
     * @param location location attached to note
     * @return the a new GuideNote
     */
    GuideNote createGuideNote(String note, Location location);

    /**
     * send a GuideNote to a helpee
     * @param note   guide note to be sent to helpee
     * @param UserID user id of the helpee that the note is sent to
     */
    void sendGuideNote(GuideNote note, int UserID);

    /**
     * edit a guide note
     * @param oldNote to be edited
     * @param editedNote new edited note
     */
    void editGuideNote(GuideNote oldNote, GuideNote editedNote);

    /**
     * display a guide note to the UI
     * @param note the guide note to be displayed
     */
    void displayGuideNote(GuideNote note);
}

