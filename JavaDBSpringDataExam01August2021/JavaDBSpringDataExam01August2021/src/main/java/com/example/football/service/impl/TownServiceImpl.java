package com.example.football.service.impl;

import com.example.football.models.dto.ImportTownDTO;
import com.example.football.models.entity.Town;
import com.example.football.repository.TownRepository;
import com.example.football.service.TownService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


//ToDo - Implement all methods
@Service
public class TownServiceImpl implements TownService {
    private final String READ_TOWN_PATH = "src/main/resources/files/json/towns.json";
    private final TownRepository townRepository;
    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public TownServiceImpl(TownRepository townRepository) {
        this.townRepository = townRepository;
        this.modelMapper = new ModelMapper();
        gson = new GsonBuilder().create();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        Path path = Path.of(READ_TOWN_PATH);
        return String.join("\n", Files.readAllLines(path));
    }

    @Override
    public String importTowns() throws IOException {
        String jsonString = readTownsFileContent();
        ImportTownDTO[] importTownDTOS = gson.fromJson(jsonString, ImportTownDTO[].class);
        List<String> results = new ArrayList<>();

        for (ImportTownDTO dto : importTownDTOS) {
            Set<ConstraintViolation<ImportTownDTO>> validationErrors = this.validator.validate(dto);

            if (validationErrors.isEmpty()) {
                Optional<Town> optionalTown = townRepository.findByName(dto.getName());
                if (optionalTown.isEmpty()) {
                    Town town = modelMapper.map(dto, Town.class);
                    townRepository.save(town);
                    String message = String.format("Successfully imported Town %s - %d"
                            , town.getName(), town.getPopulation());
                    results.add(message);
                } else {
                    results.add("Invalid Town");
                }
            } else {
                results.add("Invalid Town");
            }
        }
        return String.join("\n", results);
    }
}
