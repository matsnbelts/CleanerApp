package com.example.matsnbeltsassociate.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class Associate implements Serializable {
    private String name;
    private String email;
    private String rating;
    private String serviceArea;
    private String today;
    private Map<String, CustomerCarDetails> associateServiceCarMap;

}
