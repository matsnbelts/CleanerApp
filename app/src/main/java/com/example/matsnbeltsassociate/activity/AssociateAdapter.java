package com.example.matsnbeltsassociate.activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsnbeltsassociate.R;
import com.example.matsnbeltsassociate.model.Associate;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

public class AssociateAdapter extends RecyclerView.Adapter<AssociateAdapter.MyViewHolder> {
    private String[] mDataset;
    private MainActivity mMainActivity;
    private Associate associate;
    private final Map<String, Map<String, String>> associateServiceCarMap;
    public AssociateAdapter(MainActivity mainActivity, Associate associate) {
        this.associate = associate;
        this.mMainActivity = mainActivity;
        this.associateServiceCarMap = associate.getAssociateServiceCarMap();
        mDataset = associateServiceCarMap.keySet().toArray(new String[associateServiceCarMap.size()]);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView;
        CardView cardView;
        protected ImageButton infoButton;
        ImageButton editButton;
        String feedbackText;
        protected MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            this.textView = itemView.findViewById(R.id.car_id);
            this.infoButton = itemView.findViewById(R.id.info_button);
            this.editButton= itemView.findViewById(R.id.edit_button);
            feedbackText = "";
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public AssociateAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.associate_item, parent, false);
        MyViewHolder vh = new MyViewHolder(listItem);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String carNo = mDataset[position];
        holder.textView.setText(carNo);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+ carNo,Toast.LENGTH_LONG).show();
            }
        });

        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map carDetails= associateServiceCarMap.get(carNo);

                mMainActivity.callPopup((String) carDetails.get("customerId"), carNo);
//                Intent i = new Intent(v.getContext(), InfoPopupActivity.class);
//                mMainActivity.startActivity(i);
                Toast.makeText(v.getContext(), "INFO CLICKED", Toast.LENGTH_SHORT).show();
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("EditCLickeeddd: ", "ddff" + holder.feedbackText);
                if(!holder.feedbackText.isEmpty()) {
                    Snackbar snackbar = Snackbar
                            .make(mMainActivity.coordinatorLayout, "Cannot edit finished job", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Map<String, String> carDetails= associateServiceCarMap.get(carNo);
                    if(carDetails.get("cleaningStatus").equalsIgnoreCase("Uncleaned")) {
                        mMainActivity.finishCleaning(holder);
                    }
                }
                //Toast.makeText(v.getContext(), "EDIT CLICKED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}