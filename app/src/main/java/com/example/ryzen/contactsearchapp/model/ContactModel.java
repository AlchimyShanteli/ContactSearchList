package com.example.ryzen.contactsearchapp.model;

public class ContactModel {
    private int id;
    private String name;
    private String phoneNumber;
    private String contactImagge;

    public String getContactImagge(String name, String phoneNumber) {
        return contactImagge;
    }

    public void setContactImagge(String contactImagge) {
        this.contactImagge = contactImagge;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
