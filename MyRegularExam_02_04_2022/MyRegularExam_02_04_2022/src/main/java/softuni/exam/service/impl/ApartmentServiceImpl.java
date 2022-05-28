package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportApartmentDTO;
import softuni.exam.models.dto.ImportApartmentRootDTO;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.ApartmentService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApartmentServiceImpl implements ApartmentService {
    private final String READ_APARTMENT_PATH = "src/main/resources/files/xml/apartments.xml";
    private final ApartmentRepository apartmentRepository;
    private final TownRepository townRepository;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public ApartmentServiceImpl(ApartmentRepository apartmentRepository
            , TownRepository townRepository, Validator validator, ModelMapper modelMapper) throws JAXBException {
        this.apartmentRepository = apartmentRepository;
        this.townRepository = townRepository;

        this.validator = validator;
        this.modelMapper = modelMapper;
        JAXBContext jaxbContext = JAXBContext.newInstance(ImportApartmentRootDTO.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return apartmentRepository.count() > 0;
    }

    @Override
    public String readApartmentsFromFile() throws IOException {
        Path path = Path.of(READ_APARTMENT_PATH);
        return Files.readString(path);
    }

    @Override
    public String importApartments() throws IOException, JAXBException {
        BufferedReader bufferedReader = Files.newBufferedReader(Path.of(READ_APARTMENT_PATH));
        ImportApartmentRootDTO importApartmentRootDTO =
                (ImportApartmentRootDTO) unmarshaller.unmarshal(bufferedReader);
        return importApartmentRootDTO.getApartmentDTOS().stream()
                .map(importApartmentDTO -> importApartment(importApartmentDTO))
                .collect(Collectors.joining("\n"));
    }

    private String importApartment(ImportApartmentDTO importApartmentDTO) {
        Set<ConstraintViolation<ImportApartmentDTO>> validateErrors = validator.validate(importApartmentDTO);
        if (!validateErrors.isEmpty()) {
            return "Invalid apartment";
        }
        Optional<Apartment> optApartment = apartmentRepository
                .findByTownTownNameAndArea(importApartmentDTO.getTownName(), importApartmentDTO.getArea());
        if (optApartment.isPresent()) {
            return "Invalid apartment";
        }
        Apartment apartment = modelMapper.map(importApartmentDTO, Apartment.class);

        Optional<Town>town = townRepository.findByTownName(importApartmentDTO.getTownName());
        apartment.setTown(town.get());
        apartmentRepository.save(apartment);
        return "Successfully imported apartment " + apartment;
    }
}
