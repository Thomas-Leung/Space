package com.example.thomas.space;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    RelativeLayout rellay1, rellay2;
    private static Boolean doAnimation = true;

    // This code is for showing the login page
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rellay1 = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);

        if (doAnimation) { // only show animation when start up
            // 2000 is the timeout for the splash animation
            handler.postDelayed(runnable, 2000);
            doAnimation = false;
        } else {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }

        // Parse setup
       // Parse.enableLocalDatastore(this); // for local data base

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("dd323b5a428b08e1222ba3a563b311c0bdcc222f")
                .clientKey("38ed5829ccaa61454d1bc1c67a7e1e55fbd1bc35")
                .server("http://35.183.102.201:80/parse/") // this will change when you restart the server every time
                .enableLocalDataStore()
                .build()
        );

//        ParseUser.enableRevocableSessionInBackground();


        // USER ALREADY LOGIN WHEN THEY OPEN THE APP
        if (ParseUser.getCurrentUser() != null) {

            toMainPage();
        }

        // Hide keyboard when backgound is touched
        RelativeLayout backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.rellay3);

        ImageView logoImageView = (ImageView) findViewById(R.id.imgView_logo);

        backgroundRelativeLayout.setOnClickListener(this);

        logoImageView.setOnClickListener(this);

        // Sign in when key is touched
        EditText passwordEditText = (EditText) findViewById(R.id.editText_password);

        passwordEditText.setOnKeyListener(this);

        // Login button
        Button loginButton = findViewById(R.id.button_login);

        loginButton.setOnClickListener(this);

        // Signup button
        Button signupButton = findViewById(R.id.button_signup);

        signupButton.setOnClickListener(this);

        // Forgot button
        Button forgotButton = findViewById(R.id.button_forgot);

        forgotButton.setOnClickListener(this);

    }


    public void onClick(View view) {

        if (view.getId() == R.id.button_login) { // login when login button is pressed
            login(view);
        } else if (view.getId() == R.id.button_signup) {
            toSignup();
        } else if (view.getId() == R.id.button_forgot) {
            Toast.makeText(this, "Sorry I can't help you then.", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.rellay3 || view.getId() == R.id.imgView_logo) { // hide the keyboard if the relative layout or in image logo is touched

            // when relative layout and image view is touched, then hide the keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); //gets the keyboard
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); // get current window (keyboard) and hide it
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * Sign in when enter key is pressed
     *
     * @param view
     * @param i
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) { // i is the code of the key is being pressed and the key is pressed down
            // if enter key is pressed the sign-up method is called
            // sign up requires a view but i can be any view (password view in this case)
            login(view);
        }
        return false;
    }

    /**
     * Login for parse user, it will call back a toast whether it is successful or not.
     * login button or keyboard will trigger this method
     *
     * @param view (the text from username and password)
     */
    public void login(View view) {

        final EditText usernameEditText = (EditText) findViewById(R.id.editText_username);

        final EditText passwordEditText = (EditText) findViewById(R.id.editText_password);

        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            // matches is used to compare regular expression

            Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();

        } else {
            // (username, pw, callback)
            ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                    if (e == null) {

                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        toMainPage();

                    } else {

                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

    /**
     * Go to the main page of the application.
     */
    public void toMainPage() {

        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
        startActivity(intent);
        finish(); // prevent going back to main page

    }

    /**
     * Go to signup page
     */
    public void toSignup() {

        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        finish();
    }
}
