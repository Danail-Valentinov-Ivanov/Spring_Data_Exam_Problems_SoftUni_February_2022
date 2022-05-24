package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportPictureDTO;
import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Picture;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.PictureRepository;
import softuni.exam.service.PictureService;
import softuni.exam.util.ValidationUtilImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PictureServiceImpl implements PictureService {

    private final String READ_PICTURE_PATH = "src/main/resources/files/json/pictures.json";
    private final PictureRepository pictureRepository;
    private final CarRepository carRepository;
    private final ValidationUtilImpl validationUtil;
    private final Gson gson;
    private final ModelMapper modelMapper;

    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository, CarRepository carRepository
            , ValidationUtilImpl validationUtil, Gson gson, ModelMapper modelMapper) {
        this.pictureRepository = pictureRepository;
        this.carRepository = carRepository;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return pictureRepository.count() > 0;
    }

    @Override
    public String readPicturesFromFile() throws IOException {
        return Files.readString(Path.of(READ_PICTURE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
        ImportPictureDTO[] importPictureDTOS = gson.fromJson(readPicturesFromFile(), ImportPictureDTO[].class);
        return Arrays.stream(importPictureDTOS)
                .map(importPictureDTO -> importPicture(importPictureDTO))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importPicture(ImportPictureDTO importPictureDTO) {
        if (!validationUtil.isValid(importPictureDTO)){
            return "Invalid picture";
        }
        Optional<Picture> optionalPicture = pictureRepository.findByName(importPictureDTO.getName());
        if (optionalPicture.isPresent()){
            return "Invalid picture";
        }
        Picture picture = modelMapper.map(importPictureDTO, Picture.class);
        Optional<Car> optionalCar = carRepository.findById(importPictureDTO.getCar());
        picture.setCar(optionalCar.get());
        pictureRepository.save(picture);
        return "Successfully import picture - " + picture.getName();
    }
}
