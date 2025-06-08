package com.daniel.apirequester.service;

import com.daniel.apirequester.model.db.UserMessage;
import com.daniel.apirequester.repositories.UserMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserMessageService {
    private final UserMessageRepository userMessageRepository;

    public List<UserMessage> findAll() {
        return userMessageRepository.findAll();
    }

    public void create(UserMessage userMessage) {
        userMessageRepository.save(userMessage);
    }

    public UserMessage read(Long id) {
        return userMessageRepository.findById(id).orElse(null);
    }

    public void delete(UserMessage userMessage) {
        userMessageRepository.delete(userMessage);
    }
}
