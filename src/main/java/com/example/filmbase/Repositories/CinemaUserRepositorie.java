package com.example.filmbase.Repositories;

import com.example.filmbase.Entitys.Cinema;
import com.example.filmbase.Entitys.CinemaUser;
import com.example.filmbase.Entitys.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaUserRepositorie extends JpaRepository<CinemaUser, Integer> {
    CinemaUser findByCinemasAndUsers(Cinema a, User b);
    List<CinemaUser> findCinemaUsersByCinemas_HeadnameContainingAndUsers(String a, User b);
    CinemaUser findById(int id);
    List<CinemaUser> findAllByUsers(User user);
}
