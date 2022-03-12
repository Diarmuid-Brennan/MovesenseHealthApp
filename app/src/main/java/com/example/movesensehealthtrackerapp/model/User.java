/**
 * Diarmuid Brennan
 * 13/03/22
 * User class - Contains the details of an logged in User
 */
package com.example.movesensehealthtrackerapp.model;

public class User {
    private String userUID;
    private String firstname;
    private String lastName;
    private String email;

    /**
     * Constructor
     * @param userUID - Logged in users firestores UID
     * @param firstname - Users first name
     * @param lastName - Users last name
     * @param email - Users email
     */
    public User(String userUID, String firstname, String lastName, String email) {
        this.userUID = userUID;
        this.firstname = firstname;
        this.lastName = lastName;
        this.email = email;
    }

    public User(){

    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
