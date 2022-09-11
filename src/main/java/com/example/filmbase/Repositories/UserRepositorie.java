package com.example.filmbase.Repositories;

import com.example.filmbase.Entitys.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepositorie extends JpaRepository<User, Integer> {
    User findByLogin(String a);
    User findById(int i);
    List<User> findByShowforaddfr(boolean v);

    List<User> findByLoginContainingAndShowforaddfr(String a, boolean v);
    User findByEmail(String a);
}
