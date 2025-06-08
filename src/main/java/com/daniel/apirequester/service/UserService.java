package com.daniel.apirequester.service;

import com.daniel.apirequester.model.db.User;
import com.daniel.apirequester.model.db.UserMessage;
import com.daniel.apirequester.repositories.UserMessageRepository;
import com.daniel.apirequester.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMessageRepository userMessageRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public void createMessage(User user, UserMessage userMessage) {
        userMessage.setUser(user);
        userRepository.save(user);
        userMessageRepository.save(userMessage);
    }

    public User read(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
