package com.daniel.apirequester.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Location {
    private Integer id;
    private String name;
    private String type;
    private String dimension;
}
