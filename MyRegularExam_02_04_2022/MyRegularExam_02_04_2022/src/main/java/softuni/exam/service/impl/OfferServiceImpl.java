package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportOfferDTO;
import softuni.exam.models.dto.ImportOfferRootDTO;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.ApartmentType;
import softuni.exam.models.entity.Offer;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.OfferService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {
    private final String READ_OFFER_PATH = "src/main/resources/files/xml/offers.xml";
    private final OfferRepository offerRepository;
    private final AgentRepository agentRepository;
    private final ApartmentRepository apartmentRepository;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository
            , AgentRepository agentRepository, ApartmentRepository apartmentRepository
            , Validator validator, ModelMapper modelMapper) throws JAXBException {
        this.offerRepository = offerRepository;
        this.agentRepository = agentRepository;
        this.apartmentRepository = apartmentRepository;

        this.validator = validator;
        this.modelMapper = modelMapper;
        JAXBContext jaxbContext = JAXBContext.newInstance(ImportOfferRootDTO.class);
        unmarshaller = jaxbContext.createUnmarshaller();

        //        convert from String to LocalDate
        this.modelMapper.addConverter(mappingContext -> LocalDate.parse(mappingContext.getSource()
                , DateTimeFormatter.ofPattern("dd/MM/yyyy")), String.class, LocalDate.class);
    }

    @Override
    public boolean areImported() {
        return offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        Path path = Path.of(READ_OFFER_PATH);
        return Files.readString(path);
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
//        BufferedReader bufferedReader = Files.newBufferedReader(Path.of(READ_OFFER_PATH));
        ImportOfferRootDTO importOfferRootDTO = (ImportOfferRootDTO) unmarshaller
                .unmarshal(new File(READ_OFFER_PATH));
        return importOfferRootDTO.getImportOfferDTOList().stream()
                .map(importOfferDTO -> importOffer(importOfferDTO))
                .collect(Collectors.joining("\n"));
    }

    private String importOffer(ImportOfferDTO importOfferDTO) {
        Set<ConstraintViolation<ImportOfferDTO>> validateErrors = validator.validate(importOfferDTO);
        if (!validateErrors.isEmpty()) {
            return "Invalid offer";
        }
        Optional<Agent> optionalAgent = agentRepository
                .findByFirstName(importOfferDTO.getAgent().getFirstName());
        if (optionalAgent.isEmpty()) {
            return "Invalid offer";
        }
        Offer offer = modelMapper.map(importOfferDTO, Offer.class);

        Optional<Apartment> optionalApartment = apartmentRepository
                .findById(importOfferDTO.getApartment().getId());
        offer.setApartment(optionalApartment.get());
        offer.setAgent(optionalAgent.get());

        offerRepository.save(offer);
        return String.format("Successfully imported offer %.2f", offer.getPrice());
    }

    @Override
    public String exportOffers() {
        List<Offer> offers = offerRepository
                .findByApartmentApartmentTypeInOrderByApartmentAreaDescPriceAsc(ApartmentType.three_rooms);
        return offers.stream().map(offer -> offer.toString()).collect(Collectors.joining("\n"));

//        or
//        StringBuilder stringBuilder = new StringBuilder();
//        for (Offer offer : offers) {
//            stringBuilder.append(offer.toString()).append("\n");
//        }
//        return stringBuilder.toString();
    }
}
