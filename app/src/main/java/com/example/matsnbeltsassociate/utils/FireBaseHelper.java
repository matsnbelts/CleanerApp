package com.example.matsnbeltsassociate.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.matsnbeltsassociate.activity.AssociateAdapter;
import com.example.matsnbeltsassociate.activity.MainActivity;
import com.example.matsnbeltsassociate.model.Associate;
import com.example.matsnbeltsassociate.model.CustomerCar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class FireBaseHelper {
    private static FireBaseHelper fireBaseHelper;

    private FireBaseHelper() {

    }

    public static FireBaseHelper getInstance() {
        if(fireBaseHelper == null) {
            fireBaseHelper = new FireBaseHelper();
        }
        return fireBaseHelper;
    }

    private DatabaseReference getAssociateReference(String userID) {
        Log.i("FirebaseHelper: ", userID);
        return FirebaseDatabase.getInstance().getReference("Associate").child(userID).getRef();
    }

    private DatabaseReference getCustomerReference(String customerId) {
        return FirebaseDatabase.getInstance().getReference("Customer").child(customerId).getRef();
    }

    public void fetchCustomerDetails(final String customerId, final String carNo, final MainActivity mainActivity) {
        DatabaseReference userIdReference = getCustomerReference(customerId);
        userIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CustomerCar.CustomerCarBuilder customerCarBuilder = CustomerCar.builder();
                Log.i("CCCC", "CID: " + customerId + " carNO: " + carNo);
                Log.i("Customer Attributes: ", " " + dataSnapshot.child("Cars").child(carNo).getValue());
                customerCarBuilder.area(dataSnapshot.child("area").getValue(String.class));
                customerCarBuilder.apartment(dataSnapshot.child("apartment").getValue(String.class));
                customerCarBuilder.model(((Map<String, String>)dataSnapshot.child("Cars").child(carNo).getValue()).get("Model"));
                customerCarBuilder.city(dataSnapshot.child("city").getValue(String.class));
                mainActivity.setCustomerDetailsInfoView(customerCarBuilder.build());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void fetchAssociateDetails(final String userID, final MainActivity mainActivity) {
        DatabaseReference userIdReference = getAssociateReference(userID);
        userIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Associate.AssociateBuilder associateBuilder = Associate.builder();
                Log.i("Associate Attributes: ", " " + dataSnapshot.child("email").getValue().toString());
                associateBuilder.name(dataSnapshot.child("name").getValue(String.class));
                associateBuilder.email(dataSnapshot.child("email").getValue(String.class));
                associateBuilder.pwd(dataSnapshot.child("password").getValue(String.class));
                associateBuilder.mobile(dataSnapshot.child("mobile").getValue(String.class));
                associateBuilder.serviceArea(dataSnapshot.child("serviceArea").getValue(String.class));
                String today = CommonUtils.today();
                associateBuilder.associateServiceCarMap((Map<String, Map<String, String>>) dataSnapshot.child("Dates").child(today).child("Cars").getValue());
                final Associate associate = associateBuilder.build();
                AssociateAdapter associateAdapter = new AssociateAdapter(mainActivity, associate);
                mainActivity.setRecyclerView(associateAdapter);
                mainActivity.populateAssociateContactTextView(associate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}