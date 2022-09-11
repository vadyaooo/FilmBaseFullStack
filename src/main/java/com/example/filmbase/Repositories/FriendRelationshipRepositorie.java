package com.example.filmbase.Repositories;

import com.example.filmbase.Entitys.FriendRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRelationshipRepositorie extends JpaRepository<FriendRelationship, Integer> {
    FriendRelationship findFriendRelationshipsByUsersone_IdAndUserstwo_Id(int a, int b);
    List<FriendRelationship> findFriendRelationshipsByUsersone_Id(int r);
    List<FriendRelationship> findFriendRelationshipsByUserstwo_LoginContainingAndUsersone_Id(String a, int b);
}
