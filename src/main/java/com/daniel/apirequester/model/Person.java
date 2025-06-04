package com.daniel.apirequester.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Person {
    private Integer id;
    private String name;
    private String status;
    private String species;
    private Origin origin;
    private String image;
}
