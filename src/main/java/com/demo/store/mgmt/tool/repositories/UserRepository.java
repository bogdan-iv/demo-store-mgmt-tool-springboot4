package com.demo.store.mgmt.tool.repositories;

import com.demo.store.mgmt.tool.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username); // Spring Security needs this method
}
