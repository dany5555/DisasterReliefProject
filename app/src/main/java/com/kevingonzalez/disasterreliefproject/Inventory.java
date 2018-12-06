package com.kevingonzalez.disasterreliefproject;

/**
 * Created by Kevin Gonzalez on 11/19/2017.
 *
 * This JAVA file contains the necessary constructor, getters, and setters for the Inventory
 * object that is used to populate the database and that is also used within the app.
 */

public class Inventory {

    // Declare string variables.
    public String inventory_name;
    public String amount_inventory;
    public String id;

    // Constructor.
    public Inventory(String id, String inventory_name, String amount_inventory) {
        this.id = id;
        this.inventory_name = inventory_name;
        this.amount_inventory = amount_inventory;
    }

    // Empty Constructor.
    public Inventory(){

    }

    // Creating getters and setters for the userId and address fields.
    public String getInventory_name() {
        return inventory_name;
    }

    public void setInventory_name(String inventory_name) {
        this.inventory_name = inventory_name;
    }

    public String getAmount_inventory() {
        return amount_inventory;
    }

    public void setAmount_inventory(String amount_inventory) {
        this.amount_inventory = amount_inventory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
