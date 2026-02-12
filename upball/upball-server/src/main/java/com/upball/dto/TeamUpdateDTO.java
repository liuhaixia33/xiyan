package com.upball.dto;

import lombok.Data;

@Data
public class TeamUpdateDTO {
    
    private String name;
    private String logo;
    private String description;
    private String city;
    private String homeGround;
    private Integer joinType;
}
