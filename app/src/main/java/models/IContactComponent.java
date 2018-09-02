package models;

public interface IContactComponent {

    /**
     * find a user
     * @param userID    id of user to find
     * @return          the user associated with the userID
     */
    User findUser(int userID);

    /**
     * display a user
     * this could be to show a profile, or to show a the name and icon in a list of contacts
     * @param user  the user to be displayed
     */
    void displayUser(User user);

    /**
     * add a user as a new contact
     * @param userID    the user id of the new contact
     */
    void addContact(int userID);
}
