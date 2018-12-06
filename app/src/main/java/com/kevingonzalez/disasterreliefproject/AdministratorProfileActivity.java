package com.kevingonzalez.disasterreliefproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
 *
 * This JAVA class holds the information made available to Administrator Users only. Here, the
 * Administrator can request and add new items into the databse by simply imputing the item tyoe
 * and the amount being requested.
 *
 * The Administrator is also able to see a real-time list of the items that he is requesting to
 * the Community Centers.
 *
 * A logout button is also provided so that the Administrator can safely logout from his account
 * and be redirected to the login page.
 */

public class AdministratorProfileActivity extends AppCompatActivity implements View.OnClickListener{

    // Declaring the Firebase authentication and database reference objects.
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    // Declaring objects to be used within the view.
    private TextView textViewUserEmail;
    private EditText editTextInventory, ediTextAmount;
    private Button buttonAddInventory;

    // Declaring array lists to be used to hold contents of the database when required.
    private ArrayList<String> itemsOnDB = new ArrayList<>();
    private ArrayList<String> databaseIDs = new ArrayList<>();
    ListView listViewInventory;
    List<Inventory> inventoryList;

    // Int used to keep track of the total number of items in the database,
    private int numItemsOnDB = 0;

    // onCreate method.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);

        // Get an instance of the current logged in user.
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is logged in. If not, return the user to the Login page.
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Get the current logged in user.
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Set the database reference to a specific node within Firebase. In this case, the node
        // being created is "Inventory"
        databaseReference = FirebaseDatabase.getInstance().getReference("Inventory");

        // Assign objects and list views to their respective objects within the view.
        editTextInventory = (EditText) findViewById(R.id.editTextInventory);
        ediTextAmount = (EditText) findViewById(R.id.editTextAmount);
        buttonAddInventory = (Button) findViewById(R.id.buttonAddInventory);
        listViewInventory = (ListView) findViewById(R.id.listViewInventory);
        inventoryList = new ArrayList<>();
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserUserEmail);

        // Set welcome text.
        textViewUserEmail.setText("Welcome FEMA");

        // Set onClick listeners for the logout and add inventory buttons.
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

                // Clear the list arrays and reset int variables to avoid bugs.
                inventoryList.clear();
                itemsOnDB.clear();
                databaseIDs.clear();
                numItemsOnDB = 0;

                // Iterate through the children of the database reference previously stated. This
                // allows us to get the items and store them in our array list.
                for(DataSnapshot inventorySnapshot : dataSnapshot.getChildren()){
                    Inventory inventory = inventorySnapshot.getValue(Inventory.class);
                    inventoryList.add(inventory);

                    // As long as the database isn't empty, we continue to add items to the list array
                    // and increase the number of total items in the list.
                    if(inventory != null) {
                        itemsOnDB.add(inventory.getInventory_name());
                        databaseIDs.add(inventory.getId());
                        numItemsOnDB++;
                    }
                }

                // Dump all of the items into the listview that will be displayed to the admin.
                InventoryList adapter = new InventoryList(AdministratorProfileActivity.this, inventoryList);
                listViewInventory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method handles the whole saving-to-database process. Once the items type and amount
     * have been entered, the item object is added to the database from which it can be accessed
     * by the other portions of the app.
     *
     * This process happens in real-time, so any changes can be used by any user at the moment the
     * changes are made.
     */
    private void SaveInventoryInformation(){
        // Set imputed text by user to string variables.
        String inventory = editTextInventory.getText().toString().trim();
        String amount = ediTextAmount.getText().toString().trim();

        // This int variable is used to check for duplcates in the database and avoid them.
        int onDB = 0;

        // Check to see if the inventory type field is empty or not.
        if(TextUtils.isEmpty(inventory)){
            //email is empty
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            //stop the function execution
            return;
        }

        // Check to see if the amount field is empty or not.
        if(TextUtils.isEmpty(amount)){
            //password is empty
            Toast.makeText(this, "Please enter the inventory", Toast.LENGTH_SHORT).show();
            //stop the function execution
            return;
        }

        // If the fields aren't empty, we can add the element to the database.
        if(!TextUtils.isEmpty(inventory) && !TextUtils.isEmpty(amount)){

            // Get the unique id of the item being added to the database and save it to the id
            // string variable.
            String id = databaseReference.push().getKey();

            // Check the array list to see if a duplicate exists. If som, than the amount of the
            // current item is updated.
            for(int i = 0; i < numItemsOnDB; i++){
                if(inventory.equals(itemsOnDB.get(i))){
                    databaseReference.child(databaseIDs.get(i)).child("amount_inventory").setValue(amount);
                    Toast.makeText(this, "Information updated...", Toast.LENGTH_LONG).show();
                    onDB++;
                }
            }

            // If no duplicates were found, then the item can safely be added to the dataabase
            // as a new item.
            if(onDB == 0) {
                Inventory inventoryInformation = new Inventory(id, inventory, amount);
                databaseReference.child(id).setValue(inventoryInformation);
                Toast.makeText(this, "Information saved...", Toast.LENGTH_LONG).show();
            }
        }

        // Set the inventory type and amount fields empty once the item has been added to the
        // database.
        editTextInventory.setText("");
        ediTextAmount.setText("");
    }

    // The onClick method handles all of the different onCLickListeners throughout the JAVA class.
    @Override
    public void onClick(View view){

        // If the buttonAddInventory button is clicked, then the SaveInventoryInformation will
        // be executed.
        if(view == buttonAddInventory){
            SaveInventoryInformation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_driver_and_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // In this case, the user is safely logged out.
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
