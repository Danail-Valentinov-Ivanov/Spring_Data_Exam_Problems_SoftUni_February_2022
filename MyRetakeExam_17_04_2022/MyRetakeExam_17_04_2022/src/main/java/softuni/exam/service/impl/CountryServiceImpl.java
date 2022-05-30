package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCountryDTO;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;

import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CountryServiceImpl implements CountryService {

    private final String READ_COUNTRY_PATH = "src/main/resources/files/json/countries.json";
    private final CountryRepository countryRepository;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository, Validator validator
            , ModelMapper modelMapper, Gson gson) {
        this.countryRepository = countryRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return countryRepository.count() > 0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        return Files.readString(Path.of(READ_COUNTRY_PATH));
    }

    @Override
    public String importCountries() throws IOException {
        ImportCountryDTO[] importCountryDTOS = gson
                .fromJson(readCountriesFromFile(), ImportCountryDTO[].class);
        return Arrays.stream(importCountryDTOS)
                .map(importCountryDTO -> importCountry(importCountryDTO))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String importCountry(ImportCountryDTO importCountryDTO) {
        if (!validator.validate(importCountryDTO).isEmpty()){
            return "Invalid country";
        }
        Optional<Country>optionalCountry = countryRepository
                .findByCountryName(importCountryDTO.getCountryName());
        if (optionalCountry.isPresent()){
            return "Invalid country";
        }
        Country country = modelMapper.map(importCountryDTO, Country.class);
        countryRepository.save(country);
        return String.format("Successfully imported country %s - %s"
                , country.getCountryName(), country.getCurrency());
    }
}
