package com.kevingonzalez.disasterreliefproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by Kevin Gonzalez on 11/29/2017.
 *
 * This JAVA class holds the necessary information to update a Community Center address or location.
 * The CC will be able to enter a new address or update an existing one within the database.
 */

public class EditAddressActivity extends AppCompatActivity implements View.OnClickListener {

    // Declaring Firebase object to use Authentication and Database.
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    // Declaring objects to be used within the view.
    private TextView textViewUserEmail;
    private EditText editTextAddress;
    private Button buttonSubmit;

    // Declaring arrray list and int that holds the total number of items in the database.
    private ArrayList<String> databaseIDs = new ArrayList<>();
    private int numItemsOnDB = 0;

    // String variable that will hold the user id.
    private String userId;

    // onCreate method.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_address);

        // Get current signed in user.
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Set the database reference to a specific node within Firebase. In this case, the node
        // being created is "Addresses."
        databaseReference = FirebaseDatabase.getInstance().getReference("Addresses");

        // Assign objects within the view.
        textViewUserEmail = (TextView) findViewById(R.id.textViewEmail);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        // Set welcome text.
        textViewUserEmail.setText("Welcome " + user.getEmail());

        // Set user id.
        userId = user.getUid().toString();

        // Set onClickListner for the submit button.
        buttonSubmit.setOnClickListener(this);

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
                databaseIDs.clear();
                numItemsOnDB = 0;

                // Iterate through the children of the database reference previously stated. This
                // allows us to get the addresses and store them in our array list.
                for (DataSnapshot inventorySnapshot : dataSnapshot.getChildren()) {
                    Address address = inventorySnapshot.getValue(Address.class);

                    // As long as the database isn't empty, we continue to add items to the list array
                    // and increase the number of total items in the list.
                    if (address != null) {
                        databaseIDs.add(address.getUserId());
                        numItemsOnDB++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method is in charge of adding and updating addresses for the Community Centers.
     */
    private void updateAddress() {

        // Set imputed text by user to string variables.
        String addressText = editTextAddress.getText().toString().trim();
        int onDB = 0;

        // Check to see if the address field is empty.
        if (TextUtils.isEmpty(addressText)) {
            Toast.makeText(this, "Please enter address...", Toast.LENGTH_SHORT).show();
            return;
        }

        // if the field isn't emopty, execute inner code.
        if (!TextUtils.isEmpty(addressText)) {

            // iterate through the addresses in the list.
            for (int i = 0; i < numItemsOnDB; i++) {

                // If there is a match, then the address is updated to that user id.
                if (userId.equals(databaseIDs.get(i))) {
                    databaseReference.child(userId).child("address").setValue(addressText);
                    Toast.makeText(this, "Address updated.", Toast.LENGTH_LONG).show();
                    onDB++;
                }
            }

            // If it doesn't exist, create new address for that suer id.
            if (onDB == 0) {
                Address address = new Address(userId, addressText);
                databaseReference.child(userId).setValue(address);
                Toast.makeText(this, "Address created.", Toast.LENGTH_LONG).show();
            }
        }

        // Set the text empty.
        editTextAddress.setText("");
    }

    // The onClick method handles all of the different onCLickListeners throughout the JAVA class.
    @Override
    public void onClick(View view) {

        // If the submit button is clicked, then the updateAddress method is executed and we are
        // redirected to the previous activity.
        if (view == buttonSubmit) {
            updateAddress();
            startActivity(new Intent(getApplicationContext(), CommunityCenterProfileActivity.class));
        }
    }
}

