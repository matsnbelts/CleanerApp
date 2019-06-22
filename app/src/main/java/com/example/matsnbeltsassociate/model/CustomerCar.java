package com.example.matsnbeltsassociate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CustomerCar {
        private String model;
        private String apartment;
        private String area;
        private String city;
    }
