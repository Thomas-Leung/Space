package com.example.thomas.space;

import android.widget.LinearLayout;

public class EventModel {
    private String item_name, item_place, item_creator, item_start, item_end, id;
    LinearLayout linearLayout;

    // Constructor
    // the StringArray from the MainPageActivity will pass it in this EventModel and it will create the cardView
    public EventModel(String item_name, String item_start, String item_end, String item_place, String item_creator, String id, LinearLayout linearLayout) {
        this.item_name = item_name;
        this.item_place = item_place;
        this.item_creator = item_creator;
        this.item_start = item_start;
        this.item_end = item_end;
        this.id = id;
        this.linearLayout = linearLayout;
    }

    public String getItem_name() {

        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_place() {

        return item_place;

    }

    public void setItem_place(String item_place) {
        this.item_place = item_place;
    }

    public String getItem_creator() {

        return item_creator;
    }

    public void setItem_creator(String item_price) {
        this.item_creator = item_price;
    }

    public String getItem_start() {

        return item_start;
    }

    public String getItem_end() {
        return item_end;
    }

    /**
     * Change text for the cards (for testing purpose)
     *
     * @param text is the text you want to change to
     */
    public void changeText1(String text) {
        item_name = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }
}
