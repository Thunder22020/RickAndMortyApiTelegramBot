package com.daniel.apirequester.controller;

import lombok.AllArgsConstructor;
import com.daniel.apirequester.model.Person;
import com.daniel.apirequester.service.ApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ApiController {
    private final ApiService apiService;

    @GetMapping("/{id}")
    public Person readPerson(@PathVariable int id) {
        return apiService.readPerson(id);
    }
}
