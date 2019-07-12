package com.example.matsnbeltsassociate.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsnbeltsassociate.R;
import com.example.matsnbeltsassociate.activity.AssociateAdapter;
import com.example.matsnbeltsassociate.activity.MainActivity;
import com.example.matsnbeltsassociate.model.Associate;
import com.example.matsnbeltsassociate.model.CarServiceHistory;
import com.example.matsnbeltsassociate.model.CustomerCar;
import com.example.matsnbeltsassociate.model.CustomerCarDetails;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FireBaseHelper {
    private static FireBaseHelper fireBaseHelper;
    private static final String TAG = "FirebaseHelper";
    private FirebaseDatabase firebaseInstance;

    private FireBaseHelper() {
        firebaseInstance = FirebaseDatabase.getInstance();
        firebaseInstance.setPersistenceEnabled(true);
    }

    public static FireBaseHelper getInstance() {
        if(fireBaseHelper == null) {
            fireBaseHelper = new FireBaseHelper();
        }
        return fireBaseHelper;
    }

    private DatabaseReference getServiceHistoryReference(String carNo) {
        Log.i(TAG, carNo + " :iiii: " + firebaseInstance.toString());
        return firebaseInstance.getReference("ServiceHistory").child(CommonUtils.today()).child("Cars").child(carNo).getRef();
    }

    public DatabaseReference getAssociateReference(String userID) {
        Log.i(TAG, userID + " :uuuu: " + firebaseInstance.toString());
        return firebaseInstance.getReference("Associate").child(userID).getRef();
    }

    private DatabaseReference getCustomerReference(String customerId) {
        Log.i(TAG, customerId + " :ccccc: " + firebaseInstance.toString());
        return firebaseInstance.getReference("Customer").child(customerId).getRef();
    }

    private void fetchCustomerDetailsForCar(final String customerId, final String carNo) {
        if (customerId.equalsIgnoreCase("+918072809252")) {
            DatabaseReference userIdReference = getCustomerReference(customerId);
            userIdReference.addValueEventListener(new ValueEventListener() {
                @Override
                @SuppressWarnings("unchecked")
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CustomerCar.CustomerCarBuilder customerCarBuilder = CustomerCar.builder();
                    Log.i("CCCC", "CID: " + customerId + " carNO: " + carNo);
                    Log.i("Customer Attributes: ", " " + dataSnapshot.child("Cars").child(carNo).getValue()
                            + " ;;;; " + dataSnapshot.child("name").getValue(String.class));

                    //if no customer is found for car then something is not linked in the database
                    if (dataSnapshot.child("Cars").child(carNo).getValue() == null) {
                        Log.i("Customer Attributes: ", "No customer found for the car " + carNo);
                        return;
                    }
                    customerCarBuilder.name(dataSnapshot.child("name").getValue(String.class));
                    customerCarBuilder.area(dataSnapshot.child("area").getValue(String.class));
                    customerCarBuilder.apartment(dataSnapshot.child("apartment").getValue(String.class));
                    customerCarBuilder.customer_number(customerId);
                    customerCarBuilder.model(((Map<String, String>) dataSnapshot.child("Cars").child(carNo).getValue()).get("Model"));
                    customerCarBuilder.size(((Map<String, String>) dataSnapshot.child("Cars").child(carNo).getValue()).get("size"));
                    customerCarBuilder.city(dataSnapshot.child("city").getValue(String.class));
                    Log.i(TAG, ":::::::: " + customerCarBuilder.build().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void finishCleaning(final CustomerCarDetails customerCarDetails, final String carNo, final AssociateAdapter.MyViewHolder holder, final MainActivity mainActivity) {
        LayoutInflater inflater = (LayoutInflater) mainActivity
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        final PopupWindow popup;
        final View editCleanView;
        editCleanView = inflater.inflate(R.layout.edit_clean, null);
        popup = new PopupWindow(
                editCleanView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            popup.setElevation(20);
        }

        Button editCancelButton = editCleanView.findViewById(R.id.editCancel);

        // Set a click listener for the popup window close button
        editCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                popup.dismiss();
            }
        });
        Button editDoneButton = editCleanView.findViewById(R.id.editDone);

        // Set a click listener for the popup window close button
        editDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCleaningStatus(customerCarDetails, carNo, editCleanView, mainActivity, holder);
                // Dismiss the popup window
                popup.dismiss();
            }
        });
        popup.showAtLocation(mainActivity.getCoordinatorLayout(), Gravity.FILL_HORIZONTAL, 0, 0);

//        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorYellow));
//                // Do something with value!
//            }
//        });
    }

    private void updateCleaningStatus(CustomerCarDetails customerCarDetails, String carNo, View editCleanView, final MainActivity mainActivity, AssociateAdapter.MyViewHolder holder) {
        DatabaseReference userIdReference = getAssociateReference(mainActivity.getAssociateId());
        userIdReference.keepSynced(true);
        final boolean isCustomerNotAvailable = ((CheckBox) editCleanView.findViewById(R.id.chkCustomerAvailable)).isChecked();
        String cleanStatus = (!isCustomerNotAvailable) ? CustomerCarDetails.CleaningStatus.CLEANED : CustomerCarDetails.CleaningStatus.CANNOT_BE_CLEANED;
        customerCarDetails.setCleaningStatus(cleanStatus);
        userIdReference.child("Dates").child(CommonUtils.today()).child("Cars").child(carNo).setValue(customerCarDetails);
        holder.getCardView().setBackgroundColor(mainActivity.getResources().getColor(R.color.colorYellow));
        updateCache(customerCarDetails, carNo, mainActivity);
        updateServiceHistory(carNo, editCleanView, mainActivity, holder);
    }

    private void updateServiceHistory(String carNo, final View editCleanView, final MainActivity mainActivity, final AssociateAdapter.MyViewHolder holder) {
        final DatabaseReference carReference = getServiceHistoryReference(carNo);
        carReference.keepSynced(true);
        carReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CarServiceHistory carServiceHistory = dataSnapshot.getValue(CarServiceHistory.class);

                carServiceHistory.setAssociateFeedback(((EditText) editCleanView.findViewById(R.id.associate_feedback)).getText().toString());
                final boolean isCustomerNotAvailable = ((CheckBox) editCleanView.findViewById(R.id.chkCustomerAvailable)).isChecked();
                carServiceHistory.setCustomerAvailability(!isCustomerNotAvailable);
                String cleanStatus = (!isCustomerNotAvailable) ? CustomerCarDetails.CleaningStatus.CLEANED : CustomerCarDetails.CleaningStatus.CANNOT_BE_CLEANED;
                carServiceHistory.setCleaningStatus(cleanStatus);
                carReference.setValue(carServiceHistory);
                // pushNotificationToCustomer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateCache(CustomerCarDetails customerCarDetails, String carNo, MainActivity mainActivity) {
        Associate associate = LocalDataFetcher.getInstance().getAssociate(mainActivity);
        associate.getAssociateServiceCarMap().put(carNo, customerCarDetails);
        try {
            Context context = mainActivity.getApplicationContext();
            InternalStorage.writeObject(context, context.getResources().getString(R.string.associate_file), associate);
        } catch (IOException e) {
            Log.i("FirebaseHelperLog", e.getMessage());
            Snackbar snackbar = Snackbar
                    .make(mainActivity.getCoordinatorLayout(), "Something went wrong", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void fetchCustomerDetails(final CustomerCarDetails customerCarDetails, final String carNo, final MainActivity mainActivity) {
        DatabaseReference userIdReference = getCustomerReference(customerCarDetails.getCustomerId());
        userIdReference.keepSynced(true);
        userIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CustomerCar.CustomerCarBuilder customerCarBuilder = CustomerCar.builder();
                Log.i("CCCC", "CID: " + customerCarDetails.getCustomerId() + " carNO: " + carNo);
                Log.i("Customer Attributes: ", " " + dataSnapshot.child("Cars").child(carNo).getValue());
                //if no customer is found for car then something is not linked in the database
                if (dataSnapshot.child("Cars").child(carNo).getValue() == null) {
                    Log.i("Customer Attributes: ", "No customer found for the car " + carNo);
                    Snackbar snackbar = Snackbar
                            .make(mainActivity.getCoordinatorLayout(), "No customer details found!!!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                customerCarBuilder.name(dataSnapshot.child("name").getValue(String.class));
                customerCarBuilder.area(dataSnapshot.child("area").getValue(String.class));
                customerCarBuilder.apartment(dataSnapshot.child("apartment").getValue(String.class));
                customerCarBuilder.customer_number(customerCarDetails.getCustomerId());
                customerCarBuilder.model(((Map<String, String>)dataSnapshot.child("Cars").child(carNo).getValue()).get("Model"));
                customerCarBuilder.size(((Map<String, String>) dataSnapshot.child("Cars").child(carNo).getValue()).get("size"));
                customerCarBuilder.city(dataSnapshot.child("city").getValue(String.class));
                customerCarBuilder.serviceType(customerCarDetails.getServiceType());
                // cache customer car details
                //mainActivity.inflatePopupInfo(customerCarBuilder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fetchAssociateDetails(final String userID, final MainActivity mainActivity) {
        DatabaseReference userIdReference = getAssociateReference(userID);
        userIdReference.keepSynced(true);
        userIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.exists();
                if (!connected) {
                    Log.d(TAG, "connected");
                    Snackbar snackbar = Snackbar
                            .make(mainActivity.getCoordinatorLayout(), "Could not fetch associate details", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                Associate.AssociateBuilder associateBuilder = Associate.builder();
                associateBuilder.name(dataSnapshot.child("name").getValue(String.class));
                associateBuilder.email(dataSnapshot.child("email").getValue(String.class));
                associateBuilder.rating(Long.toString(dataSnapshot.child("rating").getValue(Long.class)));
                associateBuilder.serviceArea(dataSnapshot.child("serviceArea").getValue(String.class));

                String today = CommonUtils.today();
                associateBuilder.today(today);
                TextView emptyView = mainActivity.findViewById(R.id.empty_view);
                RecyclerView recyclerView = mainActivity.findViewById(R.id.my_recycler_view);
                Log.i(TAG, "today: " + dataSnapshot.child("Dates").child(today).getValue());
                if (!dataSnapshot.child("Dates").child(today).exists()) {
                    Log.i(TAG, "today: " + today);
                    associateBuilder.associateServiceCarMap(new LinkedHashMap<String, CustomerCarDetails>());
                    mainActivity.populateAssociateContactTextView(associateBuilder.build());
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    return;
                }
                Log.i("Associate Attributes: ", " " + dataSnapshot.child("email").getValue().toString());
                Map<String, CustomerCarDetails> associateCustomerCarMap = new LinkedHashMap<>();
                Map<String, Map<String, String>> associateServiceCarMap = (Map<String, Map<String, String>>) dataSnapshot.child("Dates").child(today).child("Cars").getValue();

                for (Map.Entry<String, Map<String, String>> associateServiceCarMapEntry : associateServiceCarMap.entrySet()) {
                    String carNo = associateServiceCarMapEntry.getKey();
//                    CustomerCarDetails.CustomerCarDetailsBuilder customerCarDetailsBuilder = CustomerCarDetails.builder();
//                    Map<String, String> carMap = associateServiceCarMapEntry.getValue();
//                    customerCarDetailsBuilder.cleaningStatus(carMap.get("cleaningStatus"));
//                    customerCarDetailsBuilder.serviceType(carMap.get("serviceType"));
//                    customerCarDetailsBuilder.customerId(carMap.get("customerId"));
//                    associateCustomerCarMap.put(carNo, customerCarDetailsBuilder.build());
                } // end of associateServiceCarMapEntry
                associateBuilder.associateServiceCarMap(associateCustomerCarMap);

                final Associate associate = associateBuilder.build();
                ///////
                ///////
                writeToCache(associate, mainActivity);
                mainActivity.populateAssociateContactTextView(associate);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                AssociateAdapter associateAdapter = new AssociateAdapter(mainActivity, associate);
                mainActivity.setRecyclerView(associateAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void writeToCache(Associate associate, final MainActivity mainActivity) {
        try {
            Context context = mainActivity.getApplicationContext();
            InternalStorage.writeObject(context, context.getResources().getString(R.string.associate_file), associate);
        } catch (IOException e) {
            Log.i("FirebaseHelperLog", e.getMessage());
            Snackbar snackbar = Snackbar
                    .make(mainActivity.getCoordinatorLayout(), "Something went wrong", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}