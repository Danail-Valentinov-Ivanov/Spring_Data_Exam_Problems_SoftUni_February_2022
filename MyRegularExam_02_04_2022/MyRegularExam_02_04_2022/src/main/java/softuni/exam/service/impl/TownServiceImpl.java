package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportTownDTO;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TownServiceImpl implements TownService {
    private final String READ_TOWN_PATH = "src/main/resources/files/json/towns.json";
    private final TownRepository townRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public TownServiceImpl(TownRepository townRepository, Gson gson, Validator validator
            , ModelMapper modelMapper) {
        this.townRepository = townRepository;

        this.gson = gson;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        Path path = Path.of(READ_TOWN_PATH);
        return Files.readString(path);
    }

    @Override
    public String importTowns() throws IOException {
        String jsonString = readTownsFileContent();
        ImportTownDTO[] importTownDTOS = gson.fromJson(jsonString, ImportTownDTO[].class);
        return Arrays.stream(importTownDTOS).map(dto -> importTown(dto))
                .collect(Collectors.joining("\n"));
    }

    private String importTown(ImportTownDTO dto) {
        Set<ConstraintViolation<ImportTownDTO>> validationErrors = validator.validate(dto);

        if (validationErrors.isEmpty()){
            Optional<Town> optionalTown = townRepository.findByTownName(dto.getTownName());
            if (optionalTown.isEmpty()){
                Town town = modelMapper.map(dto, Town.class);

                townRepository.save(town);
                return "Successfully imported Town " + town.toString();
            } else {
                return "Invalid Town";
            }
        }
        return "Invalid Town";
    }
}
