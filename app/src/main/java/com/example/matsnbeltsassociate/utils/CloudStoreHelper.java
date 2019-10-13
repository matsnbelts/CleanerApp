package com.example.matsnbeltsassociate.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
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
import com.example.matsnbeltsassociate.activity.PhoneAuthActivity;
import com.example.matsnbeltsassociate.constants.CloudStoreConstants;
import com.example.matsnbeltsassociate.model.Associate;
import com.example.matsnbeltsassociate.model.AssociateFireStoreModel;
import com.example.matsnbeltsassociate.model.CustomerCarDetails;
import com.example.matsnbeltsassociate.model.CustomerFireStoreModel;
import com.example.matsnbeltsassociate.services.MyFirebaseInstanceService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CloudStoreHelper {
    private static CloudStoreHelper cloudStoreHelper;
    private static final String TAG = "CloudStoreHelper";
    private FirebaseFirestore db;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private CloudStoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public static CloudStoreHelper getInstance() {
        if(cloudStoreHelper == null) {
            cloudStoreHelper = new CloudStoreHelper();
        }
        return cloudStoreHelper;
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

        Button editPhotoButton = editCleanView.findViewById(R.id.editPhoto);

        // Set a click listener for the popup window close button
        editPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mainActivity.setCurrentCarPhoto(CommonUtils.todayString() + "/" + carNo + ".jpg");
                mainActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
        popup.showAtLocation(mainActivity.getCoordinatorLayout(), Gravity.FILL_HORIZONTAL, 0, 0);
    }

    private void updateCleaningStatus(CustomerCarDetails customerCarDetails, String carNo, View editCleanView, final MainActivity mainActivity, AssociateAdapter.MyViewHolder holder) {
        final boolean isCustomerNotAvailable = ((CheckBox) editCleanView.findViewById(R.id.chkCustomerAvailable)).isChecked();
        String cleanStatus = (!isCustomerNotAvailable) ? CustomerCarDetails.CleaningStatus.CLEANED : CustomerCarDetails.CleaningStatus.CANNOT_BE_CLEANED;
        customerCarDetails.setCleaningStatus(cleanStatus);
        ////////////
        Calendar cal = Calendar.getInstance();
        db.collection(CloudStoreConstants.JOB_ALLOCATION)
                .document(String.valueOf(cal.get(Calendar.YEAR))).collection(String.valueOf(cal.get(Calendar.MONTH) + 1))
                .document(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)))
                .collection(CloudStoreConstants.CARS).document(carNo)
                .update(
                        CloudStoreConstants.CUSTOMERS_AVAILABILITY, !isCustomerNotAvailable,
                        CloudStoreConstants.CLEANING_STATUS, cleanStatus,
                        CloudStoreConstants.ASSOCIATE_FEEDBACK, ((EditText)editCleanView.findViewById(R.id.associate_feedback)).getText().toString()
                );
        ////////////
        holder.getCardView().setBackgroundColor(mainActivity.getResources().getColor(R.color.colorYellow));
        updateCache(customerCarDetails, carNo, mainActivity);

        MyFirebaseInstanceService.sendToTopic(mainActivity, customerCarDetails.getCustomerId(),
                customerCarDetails.getServiceType(), mainActivity.getAssociateName(),
                mainActivity.getImgDownloadURL());
    }

    private void updateCache(CustomerCarDetails customerCarDetails, String carNo, MainActivity mainActivity) {
        Associate associate = LocalDataFetcher.getInstance().getAssociate(mainActivity);
        associate.getAssociateServiceCarMap().put(carNo, customerCarDetails);
        try {
            Context context = mainActivity.getApplicationContext();
            InternalStorage.writeObject(context, context.getResources().getString(R.string.associate_file), associate);
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            Snackbar snackbar = Snackbar
                    .make(mainActivity.getCoordinatorLayout(), "Something went wrong", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void fetchCustomerCarDetails(final CustomerCarDetails customerCarDetails, final String carNo, final MainActivity mainActivity) {
        DocumentReference associateDocRef = db.collection(CloudStoreConstants.CUSTOMERS).document(customerCarDetails.getCustomerId());
        associateDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mainActivity.showProgress(true);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        mainActivity.inflatePopupInfo(document.toObject(CustomerFireStoreModel.class), customerCarDetails, carNo);
                    } else {
                        Log.d(TAG, "No such document: " + customerCarDetails.getCustomerId());
                        mainActivity.showProgress(false);
                        Snackbar snackbar = Snackbar
                                .make(mainActivity.getCoordinatorLayout(), "Could not fetch customer details", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    mainActivity.showProgress(false);
                    Snackbar snackbar = Snackbar
                            .make(mainActivity.getCoordinatorLayout(), "Could not fetch customer details", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    public void doesAssociateExist(@NonNull final String userId, @NonNull final PhoneAuthActivity phoneAuthActivity) {
        DocumentReference docRef = db.collection(CloudStoreConstants.ASSOCIATES).document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document == null || !document.exists() || !phoneAuthActivity.validatePhoneNumber()) {
                        phoneAuthActivity.getmPhoneNumberField().setError("Not a registered Mobile Number");
                        phoneAuthActivity.getmPhoneNumberField().requestFocus();
                    } else {
                        phoneAuthActivity.startPhoneNumberVerification(userId);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    phoneAuthActivity.getmPhoneNumberField().setError("Not a registered Mobile Number");
                    phoneAuthActivity.getmPhoneNumberField().requestFocus();
                }
            }
        });
    }
    public void fetchAssociateDetails(@NonNull final String userID, @NonNull final MainActivity mainActivity) {
        DocumentReference associateDocRef = db.collection(CloudStoreConstants.ASSOCIATES).document(mainActivity.getAssociateId());
        associateDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mainActivity.showProgress(true);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Associate associate = getAssociate(document);
                        if(associate == null) {
                            Log.d(TAG, "No such document: " + userID);
                            Snackbar snackbar = Snackbar
                                    .make(mainActivity.getCoordinatorLayout(), "Could not fetch associate details", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }
                        mainActivity.populateAssociateContactTextView(associate);
                        fetchAssignedCars(associate, userID, mainActivity);
                        mainActivity.showProgress(false);
                    } else {
                        Log.d(TAG, "No such document: " + userID);
                        mainActivity.showProgress(false);
                        Snackbar snackbar = Snackbar
                                .make(mainActivity.getCoordinatorLayout(), "Could not fetch associate details", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    mainActivity.showProgress(false);
                    Snackbar snackbar = Snackbar
                            .make(mainActivity.getCoordinatorLayout(), "Could not fetch associate details", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private void fetchAssignedCars(Associate associate, String userID, final MainActivity mainActivity) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(associate.getToday());
        db.collection(CloudStoreConstants.JOB_ALLOCATION)
                .document(String.valueOf(cal.get(Calendar.YEAR))).collection(String.valueOf(cal.get(Calendar.MONTH) + 1))
        .document(String.valueOf(cal.get(Calendar.DAY_OF_MONTH))).collection(CloudStoreConstants.CARS).whereEqualTo(CloudStoreConstants.ASSOCIATE_ID, userID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                TextView emptyView = mainActivity.findViewById(R.id.empty_view);
                RecyclerView recyclerView = mainActivity.findViewById(R.id.my_recycler_view);
                if (task.isSuccessful()) {
                    if(task.getResult() == null || task.getResult().size() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        return;
                    }
                    Map<String, CustomerCarDetails> associateCustomerCarMap = new LinkedHashMap<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        associateCustomerCarMap.put(document.getId(), document.toObject(CustomerCarDetails.class));
                    }
                    associate.setAssociateServiceCarMap(associateCustomerCarMap);
                    TextView associateCarsAssignedText = mainActivity.findViewById(R.id.associate_cars_assigned);
                    associateCarsAssignedText.setText("Cars Assigned: " + associate.getAssociateServiceCarMap().size() + "");
                    // write to cache
                    writeToCache(associate, mainActivity);

                    // generate recycler view
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    AssociateAdapter associateAdapter = new AssociateAdapter(mainActivity, associate);
                    mainActivity.setRecyclerView(associateAdapter);
                }
                else {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private Associate getAssociate(DocumentSnapshot document) {
        AssociateFireStoreModel associateFireStoreModel = document.toObject(AssociateFireStoreModel.class);
        if(associateFireStoreModel == null) {
            return null;
        }
        Associate.AssociateBuilder associateBuilder = Associate.builder();
        associateBuilder.name(associateFireStoreModel.getName());
        associateBuilder.email(associateFireStoreModel.getEmail());
        associateBuilder.serviceArea(associateFireStoreModel.getServiceArea());
        double associateRating = associateFireStoreModel.getTotalScores() /
               associateFireStoreModel.getTotalCustomersRated();
        String associateRatingInString = String.valueOf(associateRating);
        if(Double.isNaN(associateRating)) {
            associateRatingInString = "No Ratings Yet";
        }
        associateBuilder.rating(associateRatingInString);

        Date today = CommonUtils.today().getTime();
        associateBuilder.today(today);
        return associateBuilder.build();
    }

    private void writeToCache(Associate associate, final MainActivity mainActivity) {
        try {
            Context context = mainActivity.getApplicationContext();
            InternalStorage.writeObject(context, context.getResources().getString(R.string.associate_file), associate);
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            Snackbar snackbar = Snackbar
                    .make(mainActivity.getCoordinatorLayout(), "Something went wrong", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
