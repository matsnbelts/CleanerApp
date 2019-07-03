package com.example.matsnbeltsassociate.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class CustomerCar implements Serializable {
    private String name;
        private String model;
    private String customer_number;
    private String size;
        private String apartment;
        private String area;
        private String city;
    private String serviceType;
}
