package models;

public class Helper extends User {

    private static final String HELPER = "helper";

    public Helper(String firstName, String lastName, String email){
        super(firstName, lastName,email, HELPER);
    }
}
