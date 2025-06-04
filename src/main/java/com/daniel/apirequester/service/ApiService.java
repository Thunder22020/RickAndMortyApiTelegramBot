package com.daniel.apirequester.service;

import lombok.AllArgsConstructor;
import com.daniel.apirequester.model.Person;
import org.springframework.stereotype.Service;
import com.daniel.apirequester.model.Location;
import org.springframework.web.client.RestClient;

@Service
@AllArgsConstructor
public class ApiService {
    private final RestClient restClient;

    public Person readPerson(int id) {
        var request = restClient.get().uri("https://rickandmortyapi.com/api/character/{id}", id);
        return request.retrieve().body(Person.class);
    }

    public Location readLocation(int id) {
        var request = restClient.get().uri("https://rickandmortyapi.com/api/location/{id}", id);
        return request.retrieve().body(Location.class);
    }
}
