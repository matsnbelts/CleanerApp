package com.example.matsnbeltsassociate.model;

import com.google.firebase.Timestamp;

import lombok.Getter;

@Getter
public class CustomerCarFireStoreModel {
    private String model;
    private String pack;
    private CustomerPromoCodeFireStoreModel promo;
    private Timestamp startDate;
    private String type;
    private boolean status;
}
