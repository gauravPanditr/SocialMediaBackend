package com.example.AuthorServices.enities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "FOLLOW")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "FOLLOW_ID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "FOLLOWER_ID")
    private Author follower;

    @ManyToOne
    @JoinColumn(name = "FOLLOWING_ID")
    private Author following;

    @Column(name = "CREATE_AT")
    private Timestamp createAt;
}
