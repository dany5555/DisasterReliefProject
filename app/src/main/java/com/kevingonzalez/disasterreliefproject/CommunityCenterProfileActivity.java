package com.kevingonzalez.disasterreliefproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Gonzalez on 11/29/2017.
 * <p>
 * This JAVA class holds the necessary information for the Community Center User. Only this user
 * has access to this section of the app. Here, the CC can view the current items being requested
 * along with their current amounts.
 * <p>
 * The CC can select the item that they have in stock by clicking on the spinner, which will then
 * display a dialog with the list of items. Then, the CC can enter the amount that they have
 * available. The item's amount will be updated accordingly. If the amount hits 0, then it will be
 * removed from the list.
 * <p>
 * The CC can also edit their location by entering the city where they are located or address. This
 * location id displayed to the Driver User.
 * <p>
 * The CC can also safely logout of their account and return to the login page.
 */

public class CommunityCenterProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // Declaring Firebase objects to use Authentication and Database.
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceAddress;

    // Declaring objects to be used with layout elements.
    private TextView textViewUserEmail;
    private Spinner inventorySpinner;
    private EditText ediTextAmount;
    private Button buttonAddInventory;

    // Declaring array lists to be used to hold contents of the database when required.
    private ArrayList<String> itemsOnDB = new ArrayList<>();
    private ArrayList<String> databaseIDs = new ArrayList<>();
    private ArrayList<String> databaseAddressIDs = new ArrayList<>();
    private ArrayList<String> databaseAddresses = new ArrayList<>();
    private ArrayList<String> amountItemsOnDB = new ArrayList<>();

    // Ints to keep track of the number of items and addresses on the database.
    private int numItemsOnDB = 0;
    private int amountAddress = 0;

    // String variable that contains the user id.
    private String userId;

    // Initialize list views and list adapter.
    ListView listViewInventory;
    List<Inventory> inventoryList;
    ArrayAdapter<String> adapter;
    List<String> list;

    // onCreate method.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get current signed in user and its reference.
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Check if user is logged in. If not, return the user to the Login page.
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Check if the user is logged in as an admin. If so, redirect to proper activity.
        if (firebaseAuth.getCurrentUser() != null && user.getEmail().equals("admin@fema.org")) {
            finish();
            startActivity(new Intent(this, AdministratorProfileActivity.class));
        }

        // Check if the user is logged in as a driver. If so, redirect to proper activity.
        if (firebaseAuth.getCurrentUser() != null && user.getEmail().equals("driver@fema.org")) {
            finish();
            startActivity(new Intent(this, DriverProfileActivity.class));
        }

        // Set the database reference to a specific node within Firebase. In this case, the node
        // being created is "Inventory."
        databaseReference = FirebaseDatabase.getInstance().getReference("Inventory");

        // Set the database reference to a specific node within Firebase. In this case, the node
        // being created is "Addresses."
        databaseReferenceAddress = FirebaseDatabase.getInstance().getReference("Addresses");

        // Set the view for the spinner, buttons, and edit text elements from the view.
        inventorySpinner = (Spinner) findViewById(R.id.inventoryAvailableSpinner);
        ediTextAmount = (EditText) findViewById(R.id.editTextAmount);
        buttonAddInventory = (Button) findViewById(R.id.buttonAddInventory);
        listViewInventory = (ListView) findViewById(R.id.listViewInventory);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserUserEmail);


        // Initialize list view and adapter.
        inventoryList = new ArrayList<>();
        list = new ArrayList<String>();

        // Get the uer id of the current logged in user and store it in a string variable.
        userId = user.getUid().toString();

        // Set welcome message.
        textViewUserEmail.setText("Welcome " + user.getEmail());

        // Set  onClickListeners to the logout, add, and edit address objects.
        buttonAddInventory.setOnClickListener(this);


    }

    // The onStart method runs every time the activity is started.
    @Override
    protected void onStart() {
        super.onStart();

        // Get a database listener so that we can read the data within the database.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clear the list arrays and set int variables to 0 to avoid bugs.
                inventoryList.clear();
                list.clear();
                itemsOnDB.clear();
                databaseIDs.clear();
                amountItemsOnDB.clear();
                numItemsOnDB = 0;

                // Iterate through the children of the database reference previously stated. This
                // allows us to get the items and store then in our list array.
                for (DataSnapshot inventorySnapshot : dataSnapshot.getChildren()) {
                    Inventory inventory = inventorySnapshot.getValue(Inventory.class);
                    inventoryList.add(inventory);

                    // As long as the database isn't empty, we continue to add items to the list array
                    // and increase the number of total items in the list.
                    if (inventory != null) {
                        list.add(inventory.getInventory_name());
                        itemsOnDB.add(inventory.getInventory_name());
                        databaseIDs.add(inventory.getId());
                        amountItemsOnDB.add(inventory.getAmount_inventory());
                        numItemsOnDB++;
                    }
                }

                // Set up the adapter for the spinner that displays the selesction of items
                // available in the database.
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                inventorySpinner.setAdapter(adapter);

                // Dump all of the items into the listview that will be displayed to the Community Center.
                InventoryList adapter = new InventoryList(CommunityCenterProfileActivity.this, inventoryList);
                listViewInventory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Get a database listener so that we can read the data within the database.
        databaseReferenceAddress.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clear the list array to avoid bugs.
                databaseAddressIDs.clear();

                // Iterate through the children of the database reference previously stated. This
                // allows us to get the addresses and store then in our list array.
                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                    Address address = addressSnapshot.getValue(Address.class);

                    // As long as the database isn't empty, we continue to add items to the list array
                    // and increase the number of total addresses in the list.
                    if (address != null) {
                        databaseAddressIDs.add(address.getUserId());
                        databaseAddresses.add(address.getAddress());
                        amountAddress++;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * This method handles the process of updating item amounts in the databse. When an item is
     * selected by the CC and the amount is added, then this amount is subtracted from the amount
     * currently held in the database and then updated
     * <p>
     * This method also removes items that have been successfully completed.
     */
    private void UpdateInventoryInformation() {

        // Set imputed text by user to string variables.
        String inventory = inventorySpinner.getSelectedItem().toString();
        String amount = ediTextAmount.getText().toString().trim();


        // Takes the amount imputed by user.
        int amountInput;

        // Takes the amount left in the database of a certain item.
        int amountLeft;
        int tempNumAvailable;


        // Store the address or location in a string variable.
        String getAddress = "";

        // Check to see if the inventory type field is empty.
        if (TextUtils.isEmpty(inventory)) {
            Toast.makeText(this, "Please enter the inventory type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check to see if the amount field is empty.
        if (TextUtils.isEmpty(amount)) {
            //password is empty
            Toast.makeText(this, "Please enter the amount of inventory", Toast.LENGTH_SHORT).show();
            //stop the function execution
            return;
        }

        // Match the current user with the address location within the array list that contains all
        // addresses in the database.
        for (int i = 0; i < amountAddress; i++) {
            if (userId.equals(databaseAddressIDs.get(i))) {
                getAddress = databaseAddresses.get(i);
            }
        }

        // if the fields are not empty, then go ahead and execute innet code.
        if (!TextUtils.isEmpty(inventory) && !TextUtils.isEmpty(amount)) {

            // Check the item number.
            for (int i = 0; i < numItemsOnDB; i++) {

                // If the item number on the array list matches the one entered by user, then get
                // the amount entered by user.
                if (inventory.equals(itemsOnDB.get(i))) {
                    tempNumAvailable = Integer.parseInt(amountItemsOnDB.get(i));
                    amountInput = Integer.parseInt(amount);

                    // If the amount is greater than the requested amount, prompt user.
                    if (amountInput > tempNumAvailable) {
                        Toast.makeText(this, "Please enter a number that is equal or less than amount requested", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Else, subtract the amount on database minus the amount entered by user.
                    else {
                        amountLeft = tempNumAvailable - amountInput;
                    }

                    // if the new amount is 0, remove it from the database.
                    if (amountLeft == 0) {
                        databaseReference.child(databaseIDs.get(i)).removeValue();
                        Address address = new Address(userId, getAddress);
                        Toast.makeText(this, "Item amount has been satisfied and removed from the list", Toast.LENGTH_LONG).show();
                    }

                    // Else, update the item amount in the database. We also set the location for
                    // the Driver User.
                    else {
                        databaseReference.child(databaseIDs.get(i)).child("amount_inventory").setValue(Integer.toString(amountLeft));
                        Address address = new Address(userId, getAddress);
                        Toast.makeText(this, "Item quantity has been updated", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }

        // Set the text view to empty.
        ediTextAmount.setText("");
    }


    // The onClick method handles all of the different onCLickListeners throughout the JAVA class.
    @Override
    public void onClick(View view) {

        // When the add button is clicked, the UpdateInventoryInformation method is executed
        // and the spinner is set to its default position.
        if (view == buttonAddInventory) {
            if (inventorySpinner != null && inventorySpinner.getSelectedItem() != null) {
                UpdateInventoryInformation();
                inventorySpinner.setSelection(0);
            }
        }
    }

    // Creates my options menu on the action bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_community_center, menu);
        return true;
    }

    // When an option from the options menu is selected, a certain action is executed. This method
    // handles these actions.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // In this case, the user is safely logged out.
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;

            // In this case, the user is directed to an activity in which he can edit the
            // address of the community center.
            case R.id.edit_address:
                startActivity(new Intent(getApplicationContext(), EditAddressActivity.class));
                return true;

            // Default method required.
            default:
                return super.onOptionsItemSelected(item);


        }
    }

    @Override
    public void onBackPressed() {

    }
}
