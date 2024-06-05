package com.example.ArticlesServices.dtos;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class LikeDetailsResponse {

    private List<String> users;

}
