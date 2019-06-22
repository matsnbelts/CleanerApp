package com.example.matsnbeltsassociate.model;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class Associate {
    private String name;
    private String email;
    private String pwd;
    private String mobile;
    private String serviceArea;
    private String today;
    private Map<String, Map<String, String>> associateServiceCarMap;

}
