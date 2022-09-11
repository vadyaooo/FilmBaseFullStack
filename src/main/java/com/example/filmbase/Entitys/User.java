package com.example.filmbase.Entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @JsonIgnore
    @OneToMany(mappedBy = "users")
    private Set<CinemaUser> cinemaUsers;

    @JsonIgnore
    @OneToMany(mappedBy = "usersone")
    private Set<FriendRelationship> friendRelationships;

    @JsonIgnore
    @OneToMany(mappedBy = "userstwo")
    private Set<FriendRelationship> friendRelationships2;


    private String email;
    private String login;
    private String password;
    private boolean showotherfilms;
    private boolean showforaddfr;
}
