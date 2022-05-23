package com.example.football.service.impl;

import com.example.football.models.dto.ImportTeamDTO;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.TeamService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

//ToDo - Implement all methods
@Service
public class TeamServiceImpl implements TeamService {
    private final String READ_TEAM_PATH = "src/main/resources/files/json/teams.json";
    private final TeamRepository teamRepository;
    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final TownRepository townRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, TownRepository townRepository) {
        this.teamRepository = teamRepository;
        this.townRepository = townRepository;
        gson = new GsonBuilder().create();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        modelMapper = new ModelMapper();

    }

    @Override
    public boolean areImported() {
        return teamRepository.count() > 0;
    }

    @Override
    public String readTeamsFileContent() throws IOException {
        Path path = Path.of(READ_TEAM_PATH);
        return String.join("\n", Files.readAllLines(path));
    }

    @Override
    public String importTeams() throws IOException {
        String jsonString = readTeamsFileContent();
        ImportTeamDTO[] importTeamDTOS = gson.fromJson(jsonString, ImportTeamDTO[].class);
        return Arrays.stream(importTeamDTOS).map(dto -> importTeam(dto))
                .collect(Collectors.joining("\n"));
    }

    private String importTeam(ImportTeamDTO dto) {
        Set<ConstraintViolation<ImportTeamDTO>> validationErrors = validator.validate(dto);

        if (validationErrors.isEmpty()){
            Optional<Team> optionalTeam = teamRepository.findByName(dto.getName());
            if (optionalTeam.isEmpty()){
                Team team = modelMapper.map(dto, Team.class);

                Optional<Town>town = townRepository.findByName(dto.getTownName());
                team.setTown(town.get());
                teamRepository.save(team);
                return "Successfully imported Team " + team.toString();
            } else {
                return "Invalid Team";
            }
        }
        return "Invalid Team";
    }
}
