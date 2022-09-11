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
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @JsonIgnore
    @OneToMany(mappedBy = "cinemas")
    private Set<CinemaUser> cinemaUsers;

    private String urlimage;
    private String headname;
    private String director;
    private String zhanr;
    private String aboutis;
    private int year;
    private int marks;
    private float rating;
}
