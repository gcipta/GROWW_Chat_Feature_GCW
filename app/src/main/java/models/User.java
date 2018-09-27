package models;

/**
 * class of a user
 */
public class User {

    private String firstName;
    private String lastName;
    private String email;
    private Long id;
    private String role;

    public User(String firstName, String lastName, String email, String role){
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setRole(role);
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

    public void setRole(String role) {

        this.role = role;

    }

    public String getRole() {

        return this.role;
    }
}
