package com.kevingonzalez.disasterreliefproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
 * This JAVA class holds the necessary information for the Driver User Profile. Only a specific
 * Driver User has access to this section. Here, the driver will be able to see the current
 * locations that he should go to in order to pick up the materials requested. The locations
 * should be the Community Centers willing to donate items for a given problem.
 * <p>
 * Every time a Community Center indicates that they have a certain item, their location will
 * be made available for the driver to see. If the same Community Center enters a new item, no
 * duplicate location will be created. If a new Community Center enters a new item, then their
 * location will also be displayed to the driver.
 */

public class DriverProfileActivity extends AppCompatActivity {

    // Declaring Firebase objects to use Authentication and Database.
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    // Declaring array lists to be used to hold contents of the database when required.
    private ArrayList<String> databaseAddresses = new ArrayList<>();
    ListView listViewAddress;
    List<Address> addressList;

    // onCreate method.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        // Check if user is logged in. If not, return the user to the Login page.
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {

            // Finish current activity.
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        // Set the database reference to a specific node within Firebase. In this case, the node
        // being created is "Available Destinations."
        databaseReference = FirebaseDatabase.getInstance().getReference("Addresses");

        // Assign array list, button,  and list adapter to their objects within the view.
        addressList = new ArrayList<>();
        listViewAddress = (ListView) findViewById(R.id.listViewAddress);
    }

    // The onStart method runs every time the activity is started.
    @Override
    protected void onStart() {
        super.onStart();

        // Get a database listener so that we can read the data within the database.
        databaseReference.orderByChild("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clear the list arrays to avoid bugs.
                addressList.clear();
                databaseAddresses.clear();

                // Iterate through the children of the database reference previously stated. This
                // allows us to get the addresses and store then in our list array.
                for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                    Address address = addressSnapshot.getValue(Address.class);
                    addressList.add(address);
                    if (address != null) {
                        databaseAddresses.add(address.getAddress());
                    }
                }

                // Dump all of the addresses into the listview that will be displayed to the driver.
                AddressList adapter = new AddressList(DriverProfileActivity.this, addressList);
                listViewAddress.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
