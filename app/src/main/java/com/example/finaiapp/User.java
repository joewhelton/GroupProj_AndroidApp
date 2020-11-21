package com.example.finaiapp;

public class User {
    private String firstName;
    private String surname;
    private String userRole;
    private String email;

    public User(){

    }

    public User(String firstName, String surname, String email){
        setFirstName(firstName);
        setSurname(surname);
        setEmail(email);
        setUserRole("client");
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
