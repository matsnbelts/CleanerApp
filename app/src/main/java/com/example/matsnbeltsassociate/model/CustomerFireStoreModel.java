package com.example.matsnbeltsassociate.model;

import java.util.Map;

import lombok.Getter;

@Getter
public class CustomerFireStoreModel {
    private String name;
    private String email;
    private String mobile;
    private String apartment;
    private String apartmentNo;
    private boolean active;
    private String area;
    private Map<String, CustomerCarFireStoreModel> Cars;
}
