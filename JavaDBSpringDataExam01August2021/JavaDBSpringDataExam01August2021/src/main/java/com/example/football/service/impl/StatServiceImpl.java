package com.example.football.service.impl;

import com.example.football.models.dto.ImportStatDTO;
import com.example.football.models.dto.ImportStatRootDTO;
import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//ToDo - Implement all methods
@Service
public class StatServiceImpl implements StatService {
    private final String READ_STAT_PATH = "src/main/resources/files/xml/stats.xml";
    private final StatRepository statRepository;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public StatServiceImpl(StatRepository statRepository) throws JAXBException {
        this.statRepository = statRepository;

        JAXBContext jaxbContext = JAXBContext.newInstance(ImportStatRootDTO.class);
        unmarshaller = jaxbContext.createUnmarshaller();

        validator = Validation.buildDefaultValidatorFactory().getValidator();
        modelMapper = new ModelMapper();
    }

    @Override
    public boolean areImported() {
        return statRepository.count() > 0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        Path path = Path.of(READ_STAT_PATH);
        return Files.readString(path);
    }

    @Override
    public String importStats() throws IOException, JAXBException {
        BufferedReader bufferedReader = Files.newBufferedReader(Path.of(READ_STAT_PATH));
        ImportStatRootDTO importStatRootDTO = (ImportStatRootDTO) unmarshaller
                .unmarshal(bufferedReader);
        return importStatRootDTO.getStatDTOS().stream().map(importStatDTO -> importStat(importStatDTO))
                .collect(Collectors.joining("\n"));
    }

    private String importStat(ImportStatDTO importStatDTO) {
        Set<ConstraintViolation<ImportStatDTO>> validationErrors = validator.validate(importStatDTO);
        if (!validationErrors.isEmpty()) {
            return "Invalid Stat";
        }
        Optional<Stat> optionalStat = statRepository
                .findByShootingAndPassingAndEndurance(importStatDTO.getShooting()
                        , importStatDTO.getPassing(), importStatDTO.getEndurance());
        if (optionalStat.isPresent()){
            return "Invalid Stat";
        }
        Stat stat = modelMapper.map(importStatDTO, Stat.class);
        statRepository.save(stat);
        return "Successfully imported Stat " + stat.toString();
    }
}
