package ca.nbsoft.whereareyou.backend.api;

/**
 * Created by Nicolas on 2015-12-15.
 */
public class ContactInfo {

    ContactInfo()
    {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public void setFisrtName(String fisrtName) {
        this.fisrtName = fisrtName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String email;
    String userId;
    String fisrtName;
    String lastName;
}
