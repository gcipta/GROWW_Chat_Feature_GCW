package models;

/**
 * class of a user
 */
public class User {

    private String firstName;
    private String lastName;
    private String email;
    private Long id;

    public User(String firstName, String lastName, String email){
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
