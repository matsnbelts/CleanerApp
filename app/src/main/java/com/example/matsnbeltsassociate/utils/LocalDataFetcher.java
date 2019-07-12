package com.example.matsnbeltsassociate.utils;

import android.content.Context;
import android.util.Log;

import com.example.matsnbeltsassociate.R;
import com.example.matsnbeltsassociate.activity.AssociateAdapter;
import com.example.matsnbeltsassociate.activity.MainActivity;
import com.example.matsnbeltsassociate.model.Associate;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class LocalDataFetcher {
    private static final String TAG = "LocalDataFetcherLog";
    private static LocalDataFetcher localDataFetcher;

    private LocalDataFetcher() {

    }

    public static LocalDataFetcher getInstance() {
        if (localDataFetcher == null) {
            localDataFetcher = new LocalDataFetcher();
        }
        return localDataFetcher;
    }

    public Associate getAssociate(MainActivity mainActivity) {
        Context context = mainActivity.getApplicationContext();
        try {
            return  (Associate) InternalStorage.readObject(context, context.getResources().getString(R.string.associate_file));
        } catch (
                IOException e) {
            Log.i(TAG, e.getMessage());
            Snackbar snackbar = Snackbar
                    .make(mainActivity.getCoordinatorLayout(), "Something went wrong", Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (ClassNotFoundException e) {
            Log.i(TAG, e.getMessage());
            Snackbar snackbar = Snackbar
                    .make(mainActivity.getCoordinatorLayout(), "Something went wrong", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        return null;
    }

    public void fetchAssociateDetails(final MainActivity mainActivity) {
        Context context = mainActivity.getApplicationContext();
        try {
            final Associate associate = (Associate) InternalStorage.readObject(context, context.getResources().getString(R.string.associate_file));
            mainActivity.populateAssociateContactTextView(associate);
            AssociateAdapter associateAdapter = new AssociateAdapter(mainActivity, associate);
            mainActivity.setRecyclerView(associateAdapter);

        } catch (
                IOException e) {
            Log.i(TAG, e.getMessage());
            Snackbar snackbar = Snackbar
                    .make(mainActivity.getCoordinatorLayout(), "Something went wrong", Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (ClassNotFoundException e) {
            Log.i(TAG, e.getMessage());
            Snackbar snackbar = Snackbar
                    .make(mainActivity.getCoordinatorLayout(), "Something went wrong", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
