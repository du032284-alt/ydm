package com.example.ydm.repo;

import com.example.ydm.models.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface repoAppUser extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}