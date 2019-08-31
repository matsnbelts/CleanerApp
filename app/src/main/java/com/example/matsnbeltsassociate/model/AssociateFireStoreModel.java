package com.example.matsnbeltsassociate.model;

import com.google.firebase.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
public class AssociateFireStoreModel {
    private String name;
    private String email;
    private String mobile;
    private Timestamp doj;
    private double totalScores;
    private double totalCustomersRated;
    private String serviceArea;
    private String idProof;
}
