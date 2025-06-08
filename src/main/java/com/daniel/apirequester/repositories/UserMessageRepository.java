package com.daniel.apirequester.repositories;

import com.daniel.apirequester.model.db.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {}
