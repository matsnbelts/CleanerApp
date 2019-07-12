package com.example.matsnbeltsassociate.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CustomerCarDetails implements Serializable {
    public static class CleaningStatus {
        public static String NOT_CLEANED = "NotCleaned";
        public static String CLEANED = "Cleaned";
        public static String CANNOT_BE_CLEANED = "CannotBeCleaned";
    }
    private String customerId;
    private String serviceType;
    private String cleaningStatus;
}
