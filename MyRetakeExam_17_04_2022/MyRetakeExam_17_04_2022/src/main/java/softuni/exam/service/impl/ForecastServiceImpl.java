package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportForecastDTO;
import softuni.exam.models.dto.ImportForecastRootDTO;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Forecast;
import softuni.exam.models.entity.enums.DaysOfWeek;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.ForecastService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.validation.Validator;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForecastServiceImpl implements ForecastService {

    private final String READ_FORECAST_PATH = "src/main/resources/files/xml/forecasts.xml";
    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;

    @Autowired
    public ForecastServiceImpl(ForecastRepository forecastRepository, CityRepository cityRepository
            , Validator validator, ModelMapper modelMapper) throws JAXBException {
        this.forecastRepository = forecastRepository;
        this.cityRepository = cityRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
        JAXBContext jaxbContext = JAXBContext.newInstance(ImportForecastRootDTO.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return forecastRepository.count() > 0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(Path.of(READ_FORECAST_PATH));
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        BufferedReader bufferedReader = Files.newBufferedReader(Path.of(READ_FORECAST_PATH));
        ImportForecastRootDTO importForecastRootDTO = (ImportForecastRootDTO) unmarshaller
                .unmarshal(bufferedReader);
        return importForecastRootDTO.getImportForecastDTOList().stream()
                .map(importForecastDTO -> importForecast(importForecastDTO))
                .collect(Collectors.joining(System.lineSeparator()));
    }
    private String importForecast(ImportForecastDTO importForecastDTO) {
        if (!validator.validate(importForecastDTO).isEmpty()){
            return "Invalid forecast";
        }
        Optional<Forecast>optionalForecast = forecastRepository
                .findByDaysOfWeekAndCityId(importForecastDTO.getDaysOfWeek(), importForecastDTO.getCity());
        if (optionalForecast.isPresent()){
            return "Invalid forecast";
        }
        Forecast forecast = modelMapper.map(importForecastDTO, Forecast.class);
        Optional<City>optionalCity = cityRepository.findById(importForecastDTO.getCity());
        if (optionalCity.isEmpty()){
            return "Invalid forecast";
        }
        forecast.setCity(optionalCity.get());
        forecastRepository.save(forecast);
        return String.format("Successfully import forecast %s - %.2f"
                , forecast.getDaysOfWeek(), forecast.getMaxTemperature());
    }

    @Override
    public String exportForecasts() {
        List<Forecast>forecastList = forecastRepository
                .findAllByDaysOfWeekAndCityPopulationLessThanOrderByMaxTemperatureDescIdAsc
                        (DaysOfWeek.SUNDAY, 150000);
        return forecastList.stream().map(forecast -> forecast.toString())
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
