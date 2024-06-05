package com.example.Common.model;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest {



    private int userId;
    private Double latitude;
    private Double longitude;



}
