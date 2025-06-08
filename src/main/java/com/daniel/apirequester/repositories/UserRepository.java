package com.daniel.apirequester.repositories;

import com.daniel.apirequester.model.db.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
