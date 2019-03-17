package com.example.alerter.Model;

/**
 * Created by Hamsoft technologies
 */

public class Contacts {

    private String ContactName;
    private String ContactImage;
    private String ContactNo;
    private String ContactId;

    public Contacts() {
    }

    public Contacts(String contactName, String contactImage, String contactNo, String contactId) {
        ContactName = contactName;
        ContactImage = contactImage;
        ContactNo = contactNo;
        ContactId = contactId;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getContactImage() {
        return ContactImage;
    }

    public void setContactImage(String contactImage) {
        ContactImage = contactImage;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setPhoneNo(String contactNo) {
        ContactNo = contactNo;
    }

    public String getContactId() {
        return ContactId;
    }

    public void setContactId(String contactId) {
        ContactId = contactId;
    }
}
