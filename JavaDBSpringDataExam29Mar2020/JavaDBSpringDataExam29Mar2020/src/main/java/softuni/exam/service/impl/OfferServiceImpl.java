package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportOfferDTO;
import softuni.exam.models.dto.ImportOfferRootDTO;
import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Offer;
import softuni.exam.models.entity.Picture;
import softuni.exam.models.entity.Seller;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.repository.PictureRepository;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.OfferService;
import softuni.exam.util.ValidationUtilImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {

    private final String READ_OFFER_PATH = "src/main/resources/files/xml/offers.xml";
    private final OfferRepository offerRepository;
    private final PictureRepository pictureRepository;
    private final CarRepository carRepository;
    private final SellerRepository sellerRepository;
    private final ValidationUtilImpl validationUtil;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, PictureRepository pictureRepository
            , CarRepository carRepository, SellerRepository sellerRepository
            , ValidationUtilImpl validationUtil, ModelMapper modelMapper) throws JAXBException {
        this.offerRepository = offerRepository;
        this.pictureRepository = pictureRepository;
        this.carRepository = carRepository;
        this.sellerRepository = sellerRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;

        JAXBContext jaxbContext = JAXBContext.newInstance(ImportOfferRootDTO.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(Path.of(READ_OFFER_PATH));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        ImportOfferRootDTO importOfferRootDTO = (ImportOfferRootDTO) unmarshaller
                .unmarshal(new File(READ_OFFER_PATH));
        return importOfferRootDTO.getImportOfferDTOList().stream()
                .map(importOfferDTO -> importOffer(importOfferDTO))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importOffer(ImportOfferDTO importOfferDTO) {
        if (!validationUtil.isValid(importOfferDTO)){
            return "Invalid offer";
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(importOfferDTO.getAddedOn(), dateTimeFormatter);

        Optional<Offer>optionalOffer = offerRepository
                .findByDescriptionAndAddedOn(importOfferDTO.getDescription(), localDateTime);
        if (optionalOffer.isPresent()){
            return "Invalid offer";
        }
        Offer offer = modelMapper.map(importOfferDTO, Offer.class);

//        Optional<Set<Picture>>optionalPictureSet = pictureRepository
//                .findAllByCarId(importOfferDTO.getCar().getId());
//        offer.setPictures(optionalPictureSet.get());

        Optional<Car>optionalCar = carRepository.findById(importOfferDTO.getCar().getId());
        offer.setCar(optionalCar.get());

        Optional<Seller>optionalSeller = sellerRepository.findById(importOfferDTO.getSeller().getId());
        offer.setSeller(optionalSeller.get());

        offerRepository.save(offer);
        return "Successfully import offer " + offer;
    }
}
