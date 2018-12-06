package com.kevingonzalez.disasterreliefproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Kevin Gonzalez on 11/29/2017.
 *
 * This JAVA class holds the necessary information to register a new user into the system. It first
 * checks to see if a special user is logged in and redirects them to the proper activity.
 *
 * If the user already has an account, the prompt is clickable and redirects him to the proper
 * activity.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    // Declaring objects to be used within the view.
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignIn;
    private ProgressDialog progressDialog;

    // Declaring Firebase object to use Authentication.
    private FirebaseAuth firebaseAuth;

    // onCreate method.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get current signed in user.
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Check if the current user is Admin. Redirect to proper activity.
        if(firebaseAuth.getCurrentUser() != null && user.getEmail().equals("admin@fema.org")){
            finish();
            startActivity(new Intent(getApplicationContext(), AdministratorProfileActivity.class));
            Toast.makeText(getApplicationContext(), "Admin", Toast.LENGTH_SHORT).show();
        }

        // Check if the current user is Driver. Redirect to proper activity.
        else if(firebaseAuth.getCurrentUser() != null && user.getEmail().equals("driver@fema.org")){
            finish();
            startActivity(new Intent(getApplicationContext(), DriverProfileActivity.class));
            Toast.makeText(getApplicationContext(), "Driver", Toast.LENGTH_SHORT).show();

        }

        // Check if the current user is Community Center. Redirect to proper activity.
        else if(firebaseAuth.getCurrentUser() != null){
            // start profile activity
            finish();
            startActivity(new Intent(getApplicationContext(), CommunityCenterProfileActivity.class));
            Toast.makeText(getApplicationContext(), "CC", Toast.LENGTH_SHORT).show();

        }

        // Set new progress dialog.
        progressDialog = new ProgressDialog(this);

        // Assign objects within the view.
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);

        // Set onClickListeners for button and text view.
        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    /**
     * This method handles the whole process registration. The user enters an email and a password,
     * and then Firebase takes care of the rest.
     */
    private void registerUser(){

        // Set imputed text by user to string variables.
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Check to see of the email field is empty.
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stop the function execution
            return;
        }

        // Check to see of the password field is empty.
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stop the function execution
            return;
        }

        //if validations are ok, progrss dialog is displayed.
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        // Create a new user with email and password.
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Stop the progress dialog.
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            // Once the user has been successfully registered, the proper activity
                            // is started.
                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), CommunityCenterProfileActivity.class));
                        }

                        // If the registration process was not successful, then an error message
                        // is displayed.
                        else{
                            Toast.makeText(RegisterActivity.this, "Could Not Register, Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // The onClick method handles all of the different onCLickListeners throughout the JAVA class.
    @Override
    public void onClick(View view){

        // This button, once clicked, executes the registerUssr method.
        if(view == buttonRegister){
            registerUser();
        }

        // This button, when clicked, opens the login activity.
        if(view == textViewSignIn){
            // will open login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
    }
}
