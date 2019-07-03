package com.example.matsnbeltsassociate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CarServiceHistory {
    private String associateFeedback;
    private String associateId;
    private String cleaningStatus;
    private String customerFeedback;
    private String customerId;
    private boolean customerAvailability;
    private String rating;
    private String serviceType;
    private String supervisorId;
    private String supervisorFeedback;
    private SupervisorRemarks supervisorRemarks;
}
