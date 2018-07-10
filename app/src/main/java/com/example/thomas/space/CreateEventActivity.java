package com.example.thomas.space;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // all the stuff you want to click on (HandleClick is a class you created below)
        HandleClick handleClick = new HandleClick();
        findViewById(R.id.btn_evCancel).setOnClickListener(handleClick);
        findViewById(R.id.textView_setStartTime).setOnClickListener(handleClick);
        findViewById(R.id.textView_setEndTime).setOnClickListener(handleClick);
        findViewById(R.id.textView_setStartDate).setOnClickListener(handleClick);
        findViewById(R.id.textView_setEndDate).setOnClickListener(handleClick);
        findViewById(R.id.btn_evSave).setOnClickListener(handleClick);

        // if edit events all data will be in the create event page
        Intent getinfo = getIntent();
        final Boolean edit = getinfo.getBooleanExtra("edit", false);
        if (edit) {
            ParseQuery<ParseObject> query = new ParseQuery<>("Events");
            query.whereEqualTo("objectId", getinfo.getStringExtra("id"));
            query.setLimit(1);
            query.findInBackground(new FindCallback<ParseObject>() {
                /**
                 * It will search a list of ParseObject and store it in the List
                 * The result is limited to 1 so we can access the data by get(0)
                 *
                 * @param objects
                 * @param e
                 */
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            EditText editText = findViewById(R.id.editText_event);
                            editText.setText(objects.get(0).getString("eventName"));
                            TextView textView = findViewById(R.id.textView_setStartDate);
                            textView.setText(objects.get(0).getString("startDate"));
                            textView = findViewById(R.id.textView_setStartTime);
                            textView.setText(objects.get(0).getString("startTime"));
                            textView = findViewById(R.id.textView_setEndDate);
                            textView.setText(objects.get(0).getString("endDate"));
                            textView = findViewById(R.id.textView_setEndTime);
                            textView.setText(objects.get(0).getString("endTime"));
                            editText = findViewById(R.id.editText_location);
                            editText.setText(objects.get(0).getString("location"));
                            editText = findViewById(R.id.editText_details);
                            editText.setText(objects.get(0).getString("details"));
                        }
                    }
                }
            });
        }
    }

    private class HandleClick implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_evCancel:
                    finish();
                    break;
                case R.id.textView_setStartTime:
                    setTime(R.id.textView_setStartTime);
                    break;
                case R.id.textView_setEndTime:
                    setTime(R.id.textView_setEndTime);
                    break;
                case R.id.textView_setStartDate:
                    setDate(R.id.textView_setStartDate);
                    break;
                case R.id.textView_setEndDate:
                    setDate(R.id.textView_setEndDate);
                    break;
                case R.id.btn_evSave: // save all the data to the parse server
                    saveToParse();
                    break;

            }
        }

        /**
         * Pop up a timePickerDialog for setting the time
         *
         * @param id findViewById's id is passed in
         */
        public void setTime(final int id) {

            TimePickerDialog timePickerDialog;
            Calendar calendar;
            int currentHour;
            int currentMinute;
            final String[] amPm = new String[1];

            // show the current time in the dialog
            calendar = Calendar.getInstance();
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = calendar.get(Calendar.MINUTE);

            // show dialog when user clicked on it
            timePickerDialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                    // set amPm
                    if (hourOfDay >= 12) {
                        amPm[0] = "PM";
                    } else {
                        amPm[0] = "AM";
                    }

                    // set the time
                    TextView textView = findViewById(id);
                    //chooseTime.setText(hourOfDay + ":" + minutes + amPm);
                    textView.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm[0]);
                }
            }, currentHour, currentMinute, false); // takes hour. minutes and boolean value for 24hrs
            timePickerDialog.show();
        }

        /**
         * Setting the data from DatePickerDialog
         *
         * @param id findViewById's id is passed in
         */
        public void setDate(final int id) {

            DatePickerDialog datePickerDialog;
            final Calendar calendar;
            int currentYear;
            int currentMonth;
            int currentDayOfMonth;

            // show the current date
            calendar = Calendar.getInstance();
            currentYear = calendar.get(Calendar.YEAR);
            currentMonth = calendar.get(Calendar.MONTH);
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    // set the date the user wants
                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.YEAR, year);
                    date.set(Calendar.MONTH, month);
                    date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // create text for the date, DateFormat.FULL will show the date base on your language
                    TextView textView = findViewById(id);
                    textView.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date.getTime()));
                }
            }, currentYear, currentMonth, currentDayOfMonth); // make the current date appears here
            datePickerDialog.show();
        }

    }

    /**
     * Get text from the TextView/EditText through their id (so that they could save to the server)
     *
     * @param id findViewById's id is passed in
     * @return the text content
     */
    public String getTextViewText(int id) {

        TextView textView = findViewById(id);
        String text = textView.getText().toString();
        return text;
    }


    /**
     * Check if there are any empty column in my create event activity form
     *
     * @return true if there is no empty column, false otherwise
     */
    public Boolean eventNotEmpty() {
        if (getTextViewText(R.id.editText_event).equals("")) {
            Toast.makeText(this, "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getTextViewText(R.id.textView_setStartDate).equals("")) {
            Toast.makeText(this, "Start Date cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getTextViewText(R.id.textView_setStartTime).equals("")) {
            Toast.makeText(this, "Start Time cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getTextViewText(R.id.textView_setEndDate).equals("")) {
            Toast.makeText(this, "End Date cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (getTextViewText(R.id.textView_setEndTime).equals("")) {
            Toast.makeText(this, "End Time cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getTextViewText(R.id.editText_location).equals("")) {
            Toast.makeText(this, "Event location cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getTextViewText(R.id.editText_details).equals("")) {
            Toast.makeText(this, "Event detail cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Save all content created
     */
    public void saveToParse() {

        if (eventNotEmpty()) {

            // decide if we are saving new event or editing event
            Intent getinfo = getIntent();
            String id = getinfo.getStringExtra("id");
            Boolean edit = getinfo.getBooleanExtra("edit", false);
            if (edit) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
                query.getInBackground(id, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {

                        if (e == null && object != null) {

                            // update value
                            object.put("username", ParseUser.getCurrentUser().getUsername());
                            object.put("eventName", getTextViewText(R.id.editText_event));
                            object.put("startDate", getTextViewText(R.id.textView_setStartDate));
                            object.put("startTime", getTextViewText(R.id.textView_setStartTime));
                            object.put("endDate", getTextViewText(R.id.textView_setEndDate));
                            object.put("endTime", getTextViewText(R.id.textView_setEndTime));
                            object.put("location", getTextViewText(R.id.editText_location));
                            object.put("details", getTextViewText(R.id.editText_details));
                            object.saveInBackground();

                            Toast.makeText(CreateEventActivity.this, "Event edited successfully", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            } else { // new event

                // Set all info to Events Class in the parse server
                // p.s. objects cannot have space in between it
                ParseObject object = new ParseObject("Events");
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("eventName", getTextViewText(R.id.editText_event));
                object.put("startDate", getTextViewText(R.id.textView_setStartDate));
                object.put("startTime", getTextViewText(R.id.textView_setStartTime));
                object.put("endDate", getTextViewText(R.id.textView_setEndDate));
                object.put("endTime", getTextViewText(R.id.textView_setEndTime));
                object.put("location", getTextViewText(R.id.editText_location));
                object.put("details", getTextViewText(R.id.editText_details));

                object.saveInBackground(new SaveCallback() { // just to make sure it saved successfully
                    @Override
                    public void done(ParseException ex) {
                        if (ex == null) {
                            Toast.makeText(CreateEventActivity.this, "Event saved successfully", Toast.LENGTH_SHORT).show();
                            Log.i("Parse Result", "Successful!");
                        } else {
                            Toast.makeText(CreateEventActivity.this, "Event cannot be saved, please try again later.", Toast.LENGTH_SHORT).show();
                            Log.i("Parse Result", "Failed" + ex.toString());
                        }
                    }
                });

            }
            // close the page after saving the event to the server
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 250);

        }
    }

}
