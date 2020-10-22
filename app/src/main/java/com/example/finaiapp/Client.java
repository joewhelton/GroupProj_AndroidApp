package com.example.finaiapp;

public class Client {
    private String memberID;
    private String firstName;
    private String surname;

    private String email;

    public Client(String firstName, String surname, String email){
        //this.memberID = memberID;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
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
}
