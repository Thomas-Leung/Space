package com.example.thomas.space;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity {

    // ListView setup
    ArrayList<String> users = new ArrayList<>();

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        // toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set title
        setTitle("All users");

        // ListView setup
        final ListView listView = findViewById(R.id.listView_members);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);

        listView.setAdapter(arrayAdapter);

        // Loop through all users to show in the listView
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername()); // we don't want user follow he/herself

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) { // check if we have objects (user)

                        for (ParseUser user : objects) { // loop through all the parse users

                            users.add(user.getUsername());
                        }

                        arrayAdapter.notifyDataSetChanged(); // update array adapter

                    }
                }

            }
        });
    }

    /**
     * Set up for menu
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.member_menu, menu);
        // the second parameter is the menu we get pass there, which is our menu
        return true;
    }

    /**
     * Another setup for menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                // Toast.makeText(this, "should be a pop up card", Toast.LENGTH_SHORT).show();
                Intent toCreateEventActivity = new Intent(getApplicationContext(), CreateEventActivity.class);
                finish();
                startActivity(toCreateEventActivity);
                return true;
            case R.id.menu_back:
                finish();
                return true;
            case R.id.menu_logout:
                ParseUser.logOut();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
