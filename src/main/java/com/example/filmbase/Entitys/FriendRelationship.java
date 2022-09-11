package com.example.filmbase.Entitys;

import com.example.filmbase.Entitys.User;
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
public class FriendRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "useridone")
    private User usersone;

    @ManyToOne
    @JoinColumn(name = "useridtwo")
    private User userstwo;


}
