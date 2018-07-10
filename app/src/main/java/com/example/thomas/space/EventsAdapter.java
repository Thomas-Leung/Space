package com.example.thomas.space;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {


    private Context mContext;
    private ArrayList<EventModel> mList;

    private OnItemClickListener mListener;

    /**
     * Click on the recycling view
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public EventsAdapter(Context context, ArrayList<EventModel> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.rv_items, parent, false);

        // Return a new holder instance
        // pass mListener to ViewHolder
        ViewHolder viewHolder = new ViewHolder(contactView, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        EventModel contact = mList.get(position);

        // Set item views based on your views and data model
        TextView item_name = holder.item_name;
        TextView item_place = holder.item_place;
        TextView item_creator = holder.item_creator;
        TextView item_start = holder.item_start;
        TextView item_end = holder.item_end;// these are referring the item in the ViewHolder
        final LinearLayout item_linearLayout = holder.linearLayout;

        item_name.setText(contact.getItem_name());
        item_place.setText(contact.getItem_place());
        item_creator.setText(contact.getItem_creator());
        item_start.setText(contact.getItem_start());
        item_end.setText(contact.getItem_end());

        // Change card colour if user already selected the event
        // Get the parse ID
        final ParseQuery<ParseObject> query = new ParseQuery<>("Events");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                // get the item id in that specific position
                query.getInBackground(objects.get(position).getObjectId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        // we need to use try catch because we will have null if there is no participants join the event
                        try {
                            if (object.getList("participants").contains(ParseUser.getCurrentUser().getUsername())) {
                                holder.linearLayout.setBackgroundColor(Color.parseColor("#50d2c2"));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView item_name, item_place, item_creator, item_start, item_end;
        public LinearLayout linearLayout;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, final OnItemClickListener listener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            item_name = itemView.findViewById(R.id.rv_item_name);
            item_place = itemView.findViewById(R.id.rv_item_place);
            item_creator = itemView.findViewById(R.id.rv_item_creator);
            item_start = itemView.findViewById(R.id.rv_item_start);
            item_end = itemView.findViewById(R.id.rv_item_end);
            linearLayout = itemView.findViewById(R.id.rv_linearLayout);

            // for clicking the view
            // when we click on the card, we will call onItemClick method
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // we want the listener to call the interface method
                    if (listener != null) {
                        int position = getAdapterPosition(); // get the position
                        // make sure position is valid e.g. not something we click on it while it is deleting
                        if (position != RecyclerView.NO_POSITION) {
                            // we click on the item view (our card), get adapter position, pass th position to our interface method
                            // then we get a click and a position from our adapter to our activity
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
