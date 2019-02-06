package com.thehappybit.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    // declare the views
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private ImageButton loginBtn;
    private MaterialButton createNewBtn;
    private MaterialButton signOut;
    private TextView status;
    // declare Firebase Auth
    private FirebaseAuth firebaseAuth;

    //when create a new account
    private boolean createNew = false;


    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init the views
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.passwd_input);
        status = findViewById(R.id.status);
        loginBtn = findViewById(R.id.login_btn);
        createNewBtn = findViewById(R.id.create_new_text);
        signOut = findViewById(R.id.logout_btn);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();


        // when click on login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if user want to create new or login
                if (createNew)
                    // user create new account
                    createAccount(emailInput.getText().toString(), passwordInput.getText().toString());
                else
                    // user login
                    signIn(emailInput.getText().toString(), passwordInput.getText().toString());
            }
        });

        // when click on log out button
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sign out user
                signOut();
            }
        });

        // when create on create new text
        createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //put variable true
                createNew = true;
                //set login button text to sign up
                TextView textView = findViewById(R.id.login_btn_text);
                textView.setText("Sign up");
                // focus on email input to lead user to create email
                emailInput.requestFocus();
            }
        });

        // init progress bar
        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is signed in (non-null)
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);

    }

    // To validate the form
    private boolean validateForm(){
        boolean valid = true;

        //get email input content
        String email = emailInput.getText().toString();
        // check if the string email is empty
        if (TextUtils.isEmpty(email)){
            emailInput.setError("Email Required.");
            valid = false;
        }else {
            emailInput.setError(null);
        }

        String password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(password)){
            passwordInput.setError("Password Required.");
            valid = false;
        }else{
            passwordInput.setError(null);
        }

        return valid;
    }

    // Create a user password based account
    private void createAccount(String email, String password){

        //first we validate the form
        if (!validateForm()){
            return;
        }
        showProgressBar(true);
        // create user with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // check if the task is successful
                if (task.isSuccessful()){
                    // the created user
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);

                }else{
                    //task failed
                    // the exception message to debug
                    Log.w("login", "createUserWithEmailandPassword",task.getException());
                    // show message to the user
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);

                }
                showProgressBar(false);
            }
        });
    }

    //Update the UI
    private void updateUI(FirebaseUser user){

        if (user != null){
            // hide sign up/ sign in views
            findViewById(R.id.email_input_layout).setVisibility(View.GONE);
            findViewById(R.id.passwd_input_layout).setVisibility(View.GONE);
            findViewById(R.id.or_layout).setVisibility(View.GONE);
            findViewById(R.id.login_btn_layout).setVisibility(View.GONE);
            findViewById(R.id.facebook_login).setVisibility(View.GONE);
            findViewById(R.id.dont_have_account).setVisibility(View.GONE);
            findViewById(R.id.create_new_text).setVisibility(View.GONE);

            // show sign out views
            findViewById(R.id.logout_btn).setVisibility(View.VISIBLE);
            findViewById(R.id.status).setVisibility(View.VISIBLE);

            status.setText("You are logged in");


        }else{
            // show sign up/ sign in views
            findViewById(R.id.email_input_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.passwd_input_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.or_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.login_btn_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.facebook_login).setVisibility(View.VISIBLE);
            findViewById(R.id.dont_have_account).setVisibility(View.VISIBLE);
            findViewById(R.id.create_new_text).setVisibility(View.VISIBLE);

            // hide sign out views
            findViewById(R.id.logout_btn).setVisibility(View.GONE);
            findViewById(R.id.status).setVisibility(View.GONE);

            status.setText("Sign out");
        }

    }

    // User sign in using email and password
    private void signIn(String email, String password){
        // validate the form shouldn't be empty
        if (!validateForm()){
            return;
        }

        showProgressBar(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);

                }else{

                    //task failed
                    // the exception message for debugging
                    Log.w("login", "signInUserWithEmailAndPassword",task.getException());
                    // show message to the user
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);

                    status.setText("Authentication failed, Try to create new account if you don't have one");

                }
                showProgressBar(false);
            }
        });
    }

    // to sign out
    private void signOut(){
        firebaseAuth.signOut();
        updateUI(null);
    }

    // show/hide progress bar
    private void showProgressBar(boolean state){
        if (state)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }
}
