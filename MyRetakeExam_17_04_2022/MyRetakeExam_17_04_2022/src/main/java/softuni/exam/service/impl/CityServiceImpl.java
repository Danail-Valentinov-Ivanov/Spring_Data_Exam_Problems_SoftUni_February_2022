package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCityDTO;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CityService;

import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    private final String READ_CITY_PATH = "src/main/resources/files/json/cities.json";
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository, CountryRepository countryRepository
            , Validator validator, ModelMapper modelMapper, Gson gson) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return cityRepository.count() > 0;
    }

    @Override
    public String readCitiesFileContent() throws IOException {
        return Files.readString(Path.of(READ_CITY_PATH));
    }

    @Override
    public String importCities() throws IOException {
        ImportCityDTO[] importCityDTOS = gson.fromJson(readCitiesFileContent(), ImportCityDTO[].class);
        return Arrays.stream(importCityDTOS)
                .map(importCityDTO -> importCity(importCityDTO))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importCity(ImportCityDTO importCityDTO) {
        if (!validator.validate(importCityDTO).isEmpty()){
            return "Invalid city";
        }
        Optional<City>optionalCity = cityRepository.findByCityName(importCityDTO.getCityName());
        if (optionalCity.isPresent()){
            return "Invalid city";
        }
        City city = modelMapper.map(importCityDTO, City.class);
        Optional<Country>optionalCountry = countryRepository.findById(importCityDTO.getCountry());
        if (optionalCountry.isEmpty()){
            return "Invalid city";
        }
        city.setCountry(optionalCountry.get());
        cityRepository.save(city);
        return String.format("Successfully imported city %s - %d", city.getCityName(), city.getPopulation());
    }
}
