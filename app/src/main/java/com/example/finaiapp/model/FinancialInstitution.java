package com.example.finaiapp.model;

public class FinancialInstitution {
    private String address;
    private String category;
    private String email;
    private String name;
    private String phoneNumber;

    public FinancialInstitution() {
    }

    public FinancialInstitution(String address, String category, String email, String name, String phoneNumber) {
        this.address = address;
        this.category = category;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "FinancialInstitution{" +
                "address='" + address + '\'' +
                ", category='" + category + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }


}
