package com.example.template.api.repository;

import com.example.template.api.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query
    boolean existsByName(String name);

    @Query
    Page<User> findByName(String name, Pageable pageable);

}
