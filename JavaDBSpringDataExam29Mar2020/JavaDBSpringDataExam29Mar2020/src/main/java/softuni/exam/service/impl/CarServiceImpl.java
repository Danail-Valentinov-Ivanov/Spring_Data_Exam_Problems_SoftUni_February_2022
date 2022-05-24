package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCarDTO;
import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Offer;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtil;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final String READ_CAR_PATH = "src/main/resources/files/json/cars.json";
    private final CarRepository carRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, Gson gson
            , ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.carRepository = carRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return carRepository.count() > 0;
    }

    @Override
    public String readCarsFileContent() throws IOException {
        Path path = Path.of(READ_CAR_PATH);
        return Files.readString(path);
    }

    @Override
    public String importCars() throws IOException {
        ImportCarDTO[] importCarDTOS = gson.fromJson(readCarsFileContent(), ImportCarDTO[].class);
        return Arrays.stream(importCarDTOS)
                .map(importCarDTO -> importCar(importCarDTO))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importCar(ImportCarDTO importCarDTO) {
        if (!validationUtil.isValid(importCarDTO)){
            return "Invalid car";
        }
        Optional<Car> optionalCar = carRepository.findByMakeAndModelAndKilometers(importCarDTO.getMake()
                , importCarDTO.getModel(), importCarDTO.getKilometers());
        if (optionalCar.isPresent()){
            return "Invalid car";
        }
        Car car = modelMapper.map(importCarDTO, Car.class);
        carRepository.save(car);
        return String.format("Successfully imported car - %s - %s", car.getMake(), car.getModel());
    }

    @Override
//    @Transactional
    public String getCarsOrderByPicturesCountThenByMake() {
        List<Car>carList = carRepository.findAllByOrderByPicturesCountDescMakeAsc();
        return carList.stream().map(car -> car.toExportMethod())
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
