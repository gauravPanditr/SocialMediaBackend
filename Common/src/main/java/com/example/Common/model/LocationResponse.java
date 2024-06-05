package com.example.Common.model;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {
    private int userId;
    private Double latitude;
    private Double longitude;
    private String message;
    private Long timestamp;
}
