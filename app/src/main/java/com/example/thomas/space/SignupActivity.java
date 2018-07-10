package com.example.thomas.space;

import android.content.Intent;
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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Hide keyboard when backgound is touched
        RelativeLayout backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.rellay3);

        ImageView logoImageView = (ImageView) findViewById(R.id.imgView_logo);

        backgroundRelativeLayout.setOnClickListener(this);

        logoImageView.setOnClickListener(this);

        // Sign in when key is touched
        EditText passwordEditText = (EditText) findViewById(R.id.editText_supassword);

        passwordEditText.setOnKeyListener(this);

        // Back button
        Button backButton = findViewById(R.id.button_back);

        backButton.setOnClickListener(this);

        // Sing up Now button
        Button signUpNow = findViewById(R.id.button_signupNOW);

        signUpNow.setOnClickListener(this);
    }

    public void onClick(View view) {

        if (view.getId() == R.id.button_signupNOW) { // login when login button is pressed
            signup();
        } else if (view.getId() == R.id.button_back) {
            toMainActivity();
        } else if (view.getId() == R.id.rellay3 || view.getId() == R.id.imgView_logo) { // hide the keyboard if the relative layout or in image logo is touched

            // when relative layout and image view is touched, then hide the keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); //gets the keyboard
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); // get current window (keyboard) and hide it
        }
    }

    /**
     * Sign up user
     */
    private void signup() {

        ParseUser user = new ParseUser();

        EditText username = findViewById(R.id.editText_suusername);
        EditText password = findViewById(R.id.editText_supassword);

        if (username.getText().toString().matches("") || password.getText().toString().matches("")) {
            // matches is used to compare regular expression

            Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();
        } else {

            user.setUsername(username.getText().toString());
            user.setPassword(password.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        Log.i("Signup", "Sucessful");

                        toMainpage();

                        Toast.makeText(SignupActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    /**
     * Back to main activity when the button is clicked
     */
    public void toMainActivity() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        // stop animation when going back
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivityForResult(intent, 0);
//        overridePendingTransition(0, 0);
        // stop going back to sign-up activity
        finish();
        startActivity(intent);
    }

    public void toMainpage() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        finish(); //prevent going back to sign-up page

        startActivity(intent);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signup();
        }
        return false;
    }
}
