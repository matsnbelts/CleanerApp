package com.example.matsnbeltsassociate.model;

import com.google.firebase.Timestamp;

import lombok.Getter;

@Getter
public class CustomerPromoCodeFireStoreModel {
    private String code;
    private Timestamp expiry;
}
