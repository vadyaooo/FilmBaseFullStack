package com.example.filmbase.Entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CinemaUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @ManyToOne
    @JoinColumn(name = "cinemaid")
    private Cinema cinemas;


    @ManyToOne
    @JoinColumn(name = "userid")
    private User users;

    private String statuscinema;
    private int ratinguser;
}
