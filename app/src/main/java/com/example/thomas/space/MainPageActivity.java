package com.example.thomas.space;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "MainPageActivity";

    RecyclerView recyclerView;
    ArrayList<EventModel> eventList;
    Dialog eventDialog;
    EventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // add event dialog (this dialog ended up not being used)
        eventDialog = new Dialog(this);

        // recyclerView setup
        recyclerView = findViewById(R.id.rv);

        // ParseQuery setup
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Events");

        // store locally
        //query.fromLocalDatastore();

        query.orderByAscending("createdAt");

        // run query
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                // create an arrayList to store our data (With dummy data)
//                ArrayList<String> eventName = new ArrayList<>(Arrays.asList("Holiday", "Party", "Chicken dinner", "War game"));
//                ArrayList<String> date = new ArrayList<>(Arrays.asList("June 27,2018", "June 27,2018", "June 27,2018", "June 27,2018"));
//                ArrayList<String> time = new ArrayList<>(Arrays.asList("13:00PM", "13:00PM", "13:00PM", "13:00PM"));
//                ArrayList<String> location = new ArrayList<>(Arrays.asList("Library", "Home", "Airport", "noWhere"));
//                ArrayList<String> creator = new ArrayList<>(Arrays.asList("Milos", "Tony", "Linda", "Ling"));
                ArrayList<String> eventName = new ArrayList<>();
                ArrayList<String> date = new ArrayList<>();
                ArrayList<String> time = new ArrayList<>();
                ArrayList<String> location = new ArrayList<>();
                ArrayList<String> creator = new ArrayList<>();
                ArrayList<String> id = new ArrayList<>();
                ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
                String temp;

                // if not error
                if (e == null) {
                    // if there is something in the parse server
                    if (objects.size() > 0) {  // I think object.size() is similar to query.count()
//                        for (int i = 0; i < objects.size(); i++) { // without for-each loop
//                            creator.add(objects.get(i).getString("username"));
//                        }
                        // ADDING AND FORMATTING THE ARRAYLIST TO COMBINE IT TO THE VIEW LIST AFTERWARDS
                        for (ParseObject data : objects) {
                            eventName.add(data.getString("eventName"));
                            // get rid of the date
                            temp = data.getString("startDate");
                            temp = temp.substring(temp.indexOf(",") + 2);
                            date.add(temp);
                            // check if it is the same date
                            if (data.getString("startDate").equals(data.getString("endDate"))) {
                                temp = data.getString("startTime") + " - " + data.getString("endTime");
                                time.add(temp);
                            } else {
                                time.add(data.getString("startTime"));
                            }

                            location.add(data.getString("location"));
                            creator.add(data.getString("username"));
                            id.add(data.getObjectId());

                            // it is save in the array for changing colours later on
                            LinearLayout linearLayout = findViewById(R.id.rv_linearLayout);
                            linearLayouts.add(linearLayout);
                        }
                    }
                    Log.i("Parse Result", "Successful!");
                } else {
                    Log.i("Parse Result", "Failed" + e.toString());
                }
                eventList = new ArrayList<>();

                // adding stuff in the recyclerView
                for (int i = 0; i < eventName.size(); i++) {

                    eventList.add(new EventModel(eventName.get(i), date.get(i), time.get(i), location.get(i), creator.get(i), id.get(i), linearLayouts.get(i)));

                }

                // adding stuff into the adapter, EventsAdapter is our custom adapter
                adapter = new EventsAdapter(MainPageActivity.this, eventList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

                RecyclerView.LayoutManager mLayoutManager = linearLayoutManager;

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);

                // click on the adapter setup (usually put it after setAdapter)
                adapter.setOnItemClickListener(new EventsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        //changeItem(position, "Clicked");
                        // notify adapter that things changed
                        //adapter.notifyItemChanged(position);
                        Log.i("position", Integer.toString(position));
                        toEventActivity(eventList.get(position).getId());

                        // String searchString = eventList.get(position).getItem_place();
                        // Log.i("Testing",  searchString );
                    }
                });

            }

        });


        // set Hi username
        TextView textview = findViewById(R.id.textView_name);
        try {
            String title = "Hi " + ParseUser.getCurrentUser().getUsername() + "!";
            textview.setText(title);
        } catch (Exception e) {
            textview.setText("Hi!");
        }

    }

    /**
     * Change the text after the a card is clicked (testing purpose)
     */
    public void changeItem(int position, String text) {
        eventList.get(position).changeText1(text);
    }

    /**
     * Go to EventActivity and sending the specific eventList (ArrayList) info to the intent
     * so that we can access the data in the server
     *
     * @param id is the id of the content in the eventList
     */
    public void toEventActivity(String id) {
        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
        intent.putExtra("id", id);
        finish();
        startActivity(intent);
    }

    /**
     * This method is for popup menu
     *
     * @param v
     */
    public void showPopup(View v) {

        PopupMenu popup = new PopupMenu(this, v); // (context, anchor aka where we want to show it)
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.main_menu);
        popup.show();
    }

    /**
     * This method is for users to click on the menu
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_member:
                Intent toMemberActivity = new Intent(getApplicationContext(), MemberActivity.class);
                startActivity(toMemberActivity);
                return true;

            case R.id.menu_logout:
                ParseUser.logOut();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return false;
        }
    }

    /**
     * This method is for adding an event. The commented part was the previous design.
     *
     * @param v
     */
    public void add(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//
//        eventDialog.setContentView(R.layout.create_event_popup);
//        TextView txtclose = eventDialog.findViewById(R.id.txtclose);
//        Button btnSubmit = eventDialog.findViewById(R.id.button_submit);
//
//        txtclose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                eventDialog.dismiss();
//            }
//        });
//
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainPageActivity.this, "should be a pop up card", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        eventDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        eventDialog.show();
//
//        popup.show();
        Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
        intent.putExtra("edit", false);
        startActivity(intent);
    }
}
