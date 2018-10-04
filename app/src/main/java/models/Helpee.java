package models;

public class Helpee extends User {

    private static final String HELPEE = "helpee";

    public Helpee(String firstName, String lastName, String email){
        super(firstName, lastName,email, HELPEE);
    }
}
