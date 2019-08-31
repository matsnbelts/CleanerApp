package com.example.matsnbeltsassociate.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class Associate implements Serializable {
    private String name;
    private String email;
    private String rating;
    private String serviceArea;
    private Date today;
    private String idProof;
    private Map<String, CustomerCarDetails> associateServiceCarMap;

}
