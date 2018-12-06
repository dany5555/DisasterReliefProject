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
 * This JAVA class holds the necessary information for users to login to the app. This activity is
 * the first thing that the user should see. The layout should prompt the user to enter his
 * credentials (email and password), Firebase takes care of the authentication process by
 * validating if the user exists and if the password was entered correctly.
 *
 * Depending on the type of account, the user will be redirercted to the proper activity. This
 * allows for security and not having regular users mess with admin content and break the whole
 * intention of the app.
 *
 * If the user doesn't have an account, he can then create one by clicking on the indicated link.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    // Declaring objects to be used within the view.
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;

    // Declaring the Firebase authentication object for user verification.
    private FirebaseAuth firebaseAuth;

    // onCreate method.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Assign objects to their respective objects within the view.
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        // Setting the onClick listeners.
        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

        // Setting a progress dialog while loading or waiting for a request.
        progressDialog = new ProgressDialog(this);
    }

    /**
     * This method handles the whole log in process. it checks for empty fields and then runs
     * the validation through the Firebase backend.
     */
    private void userLogin(){

        // Set inputed text to string variables.
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Check to see if the email field is empty.
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stop the function execution
            return;
        }

        // Check to see if the password field is empty.
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stop the function execution
            return;
        }

        //If checking for empty fields was successful, then a progress dialog will be displayed.
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        // Firebase checks user authentication.
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Close the progress dialog.
                        progressDialog.dismiss();

                        // If the user was successfully authenticated, check if the user is an Admin, Driver, or
                        // Community Center and redirect them to the proper activity.
                        if(task.isSuccessful()){
                            if (email.equals("admin@fema.org")) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), AdministratorProfileActivity.class));
                            } else if (email.equals("driver@fema.org")) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), DriverProfileActivity.class));
                            } else {
                                finish();
                                startActivity(new Intent(getApplicationContext(), CommunityCenterProfileActivity.class));
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Error logging in. Check yout credentials or internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // The onClick method handles all of the different onCLickListeners throughout the JAVA class.
    @Override
    public void onClick(View view){

        // Once the sign in button is clicked, the userLogin method is executed.
        if(view == buttonSignIn){
            userLogin();
        }

        // Once the sign up textview is clicked, the registration activity is started.
        if(view == textViewSignUp){
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get an instance of the current logged in user.
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // If the user logging in happens to be the admin, he will be redirected to the admin
        // section of the app.
        if(firebaseAuth.getCurrentUser() != null && user.getEmail().equals("admin@fema.org")){
            finish();
            startActivity(new Intent(this, AdministratorProfileActivity.class));
            Toast.makeText(getApplicationContext(), "Logged in as admin", Toast.LENGTH_SHORT).show();
        }

        // If the user logging in happens to be the driver, he will be redirected to the driver
        // section of the app.
        else if(firebaseAuth.getCurrentUser() != null && user.getEmail().equals("driver@fema.org")){
            finish();
            startActivity(new Intent(this, DriverProfileActivity.class));
            Toast.makeText(getApplicationContext(), "Logged in as driver", Toast.LENGTH_SHORT).show();
        }

        // If the user logging in happens to be a Community Center, he will be redirected to the
        // Community Center section of the app.
        else if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, CommunityCenterProfileActivity.class));
            Toast.makeText(getApplicationContext(), "Logged in as Community Center", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {

    }
}
