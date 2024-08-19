package com.example.sw_planet_api.web;

import com.example.sw_planet_api.domain.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.sw_planet_api.common.PlanetConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanetService planetService;

    @Test
    public void createPlanet_WithValidData_ReturnsCreated() throws Exception{
        when(planetService.create(PLANET)).thenReturn(PLANET);

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(PLANET))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void createPlanet_WithInvalidData_ReturnsBadRequest() throws Exception{
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(emptyPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(invalidPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void createPlanet_WithExistingName_ReturnsConflict() throws Exception{
        when(planetService.create(any())).thenThrow();

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(PLANET))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() throws Exception{
        when(planetService.getByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

        mockMvc.perform(get("/planets/name" + PLANET.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsEmpty() throws Exception{
        mockMvc.perform(get("/planets?" + String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void listPlanets_ReturnsFilteredPlanets() throws Exception{
        when(planetService.list(null, null, null )).thenReturn(PLANETS);
        when(planetService.list(TATOOINE.getName(), TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));

        when(planetService.list(null,null,null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/planets" + PLANET.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$[0]").value(TATOOINE));
    }

    @Test
    public void listPlanets_ReturnsNoPlanets() throws Exception{
        when(planetService.list(null, null, null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/planets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void removePlanet_WWithExistingId_ReturnsNoContent() throws Exception{
        mockMvc.perform(delete("/planets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void removePlanet_WithUniexustingId_ReturnsNotFound() throws Exception{
        final Long planetId = 1L;

       doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(planetId);

        mockMvc.perform(delete("/planets/1"))
                .andExpect(status().isNotFound());
    }
}
