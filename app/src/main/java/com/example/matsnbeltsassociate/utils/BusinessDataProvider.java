package com.example.matsnbeltsassociate.utils;

import com.example.matsnbeltsassociate.activity.MainActivity;

public interface BusinessDataProvider {
    void fetchAssociateDetails(final String userID, String carNo, final MainActivity mainActivity);
    void fetchCustomerDetails(final String customerId, final String carNo, final String serviceType, final MainActivity mainActivity);
}
