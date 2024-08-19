package com.example.sw_planet_api.web;

import com.example.sw_planet_api.domain.Planet;
import com.example.sw_planet_api.domain.PlanetRepository;
import com.example.sw_planet_api.domain.QueryBuilder;
import io.micrometer.observation.ObservationFilter;
import org.hibernate.boot.model.internal.QueryBinder;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanetService {

    private PlanetRepository planetRepository;

    public PlanetService(PlanetRepository planetRepository){
        this.planetRepository = planetRepository;
    }

    public Planet create(Planet planet){
        return planetRepository.save(planet);
    }

    public Optional<Planet> get(Long id) {
        return planetRepository.findById(id);
    }

    public Optional<Planet> getByName(String name) {
        return planetRepository.findByName(name);
    }

    public List<Planet> list(String name, String terrain, String climate) {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(name,climate, terrain));
        return planetRepository.findAll(query);
    }

    public void remove(Long id) {
        planetRepository.deleteById(id);
    }
}
