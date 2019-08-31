package com.example.matsnbeltsassociate.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.matsnbeltsassociate.R;
import com.example.matsnbeltsassociate.model.Associate;
import com.example.matsnbeltsassociate.model.CustomerCarDetails;
import com.example.matsnbeltsassociate.utils.CloudStoreHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

public class AssociateAdapter extends RecyclerView.Adapter<AssociateAdapter.MyViewHolder> {
    private static final String TAG = "AssociateAdapter";
    private String[] mDataset;
    private MainActivity mMainActivity;
    @NonNull private final Map<String, CustomerCarDetails> associateServiceCarMap;
    public AssociateAdapter(MainActivity mainActivity, @NonNull Associate associate) {
        this.mMainActivity = mainActivity;
        this.associateServiceCarMap = associate.getAssociateServiceCarMap();
        mDataset = associateServiceCarMap.keySet().toArray(new String[0]);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AssociateAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.associate_item, parent, false);
        return new MyViewHolder(listItem);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final String carNo = mDataset[position];
        holder.textView.setText(carNo);
        @NonNull CustomerCarDetails customerCarDetails = associateServiceCarMap.get(carNo);
        if (!customerCarDetails.getCleaningStatus().equalsIgnoreCase(CustomerCarDetails.CleaningStatus.NOT_CLEANED)) {
            holder.cardView.setBackgroundColor(mMainActivity.getResources().getColor(R.color.colorYellow));
        }

        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @NonNull CustomerCarDetails customerCarDetails = associateServiceCarMap.get(carNo);
                if (customerCarDetails.getCustomerId() != null) {
                    CloudStoreHelper.getInstance().fetchCustomerCarDetails(customerCarDetails, carNo, mMainActivity);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(mMainActivity.coordinatorLayout, "No customer details found!!!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhoneAuthActivity.isNetworkAvailable(mMainActivity.getApplicationContext())) {
                    @NonNull CustomerCarDetails customerCarDetails = associateServiceCarMap.get(carNo);
                    if (!customerCarDetails.getCleaningStatus().equalsIgnoreCase(CustomerCarDetails.CleaningStatus.NOT_CLEANED)) {
                        Snackbar snackbar = Snackbar
                                .make(mMainActivity.coordinatorLayout, "Cannot edit finished job", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        CloudStoreHelper.getInstance().finishCleaning(customerCarDetails, carNo, holder, mMainActivity);
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(mMainActivity.coordinatorLayout, "Connect to internet to edit status", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView;
        CardView cardView;
        ImageButton infoButton;
        ImageButton editButton;
        String feedbackText;

        MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            this.textView = itemView.findViewById(R.id.car_id);
            this.infoButton = itemView.findViewById(R.id.info_button);
            this.editButton = itemView.findViewById(R.id.edit_button);
            feedbackText = "";
        }

        public CardView getCardView() {
            return cardView;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}