package exam.service.impl;

import exam.model.dto.ImportTownDTO;
import exam.model.dto.ImportTownRootDTO;
import exam.model.entity.Town;
import exam.repository.TownRepository;
import exam.service.TownService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class TownServiceImpl implements TownService {

    private final String READ_TOWN_PATH = "src/main/resources/files/xml/towns.xml";
    private final TownRepository townRepository;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;

    @Autowired
    public TownServiceImpl(TownRepository townRepository, Validator validator
            , ModelMapper modelMapper) throws JAXBException {
        this.townRepository = townRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;

        JAXBContext jaxbContext = JAXBContext.newInstance(ImportTownRootDTO.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        Path path = Path.of(READ_TOWN_PATH);
        return Files.readString(path);
    }

    @Override
    public String importTowns() throws JAXBException {
        ImportTownRootDTO importTownRootDTO = (ImportTownRootDTO) unmarshaller
                .unmarshal(new File(READ_TOWN_PATH));
        return importTownRootDTO.getTownDTOList().stream()
                .map(importTownDTO -> importTown(importTownDTO))
                .collect(Collectors.joining("\n"));
    }

    private String importTown(ImportTownDTO importTownDTO) {
        Set<ConstraintViolation<ImportTownDTO>> validateErrors = validator.validate(importTownDTO);
        if (!validateErrors.isEmpty()){
            return "Invalid town";
        }
        Optional<Town>optionalTown = townRepository.findByName(importTownDTO.getName());
        if (optionalTown.isPresent()){
            return "Invalid town";
        }
        Town town = modelMapper.map(importTownDTO, Town.class);
        townRepository.save(town);
        return String.format("Successfully imported Town %s", town.getName());
    }
}
