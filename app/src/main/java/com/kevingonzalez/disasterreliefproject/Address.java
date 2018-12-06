package com.kevingonzalez.disasterreliefproject;

/**
 * Created by Kevin Gonzalez on 12/6/2017.
 *
 * This JAVA file contains the necessary constructor, getters, and setters for the Address
 * object that is used to populate the database and that is also used within the app.
 */

public class Address {

    // Declare string variables.
    public String userId;
    public String address;

    // Constructor
    public Address(String userId, String address){
        this.userId = userId;
        this.address = address;
    }

    // Empty Constructor.
    public Address(){
    }

    // Creating getters and setters for the userId and address fields.
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
